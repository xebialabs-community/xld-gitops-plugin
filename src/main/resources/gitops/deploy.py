#
# Copyright 2020 XEBIALABS
#
# Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#

import json
import sys
from overtherepy import OverthereHostSession
from com.xebialabs.overthere import OperatingSystemFamily
from com.google.common.io import Resources
from java.nio.charset import Charset
from string import Template
from java.lang import Thread, Integer


from overtherepy import LocalConnectionOptions, OverthereHost, OverthereHostSession, SshConnectionOptions,BashScriptBuilder


class XebiaLabsCLI(object):

    def __init__(self, job):
        self.job =  job
        self.repository = self.job.repository

    def apply(self) :
        with self._new_session() as session:
            data_str = self.generate_content('gitops/deploy.sh.template')
            remote_file = session.upload_text_content_to_work_dir(data_str,'xl_apply.sh', executable=True)
            self._execute(session, remote_file.path)

    def get_last_commit(self):
        output = self.run('git ls-remote {job_repository_url} refs/heads/{job_branch}'.format(**self._to_map(self.job,'job')))
        return output.split('\t')[0]


    def load_classpath_resource(self,resource):
        url = Thread.currentThread().contextClassLoader.getResource(resource)
        if url is None:
            raise Exception("Resource [%s] not found on classpath." % resource)

        return Resources.toString(url, Charset.defaultCharset())

    def _to_map(self,ci,prefix):
        values={}
        if ci is None:
            return values
        #print ">_to_map:"+ci.id
        #print ">_to_map: {0}".format(ci.type)
        for pd in ci.getType().getDescriptor().getPropertyDescriptors():
            key='{0}_{1}'.format(prefix,pd.name)
            if pd.kind in ['STRING','BOOLEAN','INTEGER']:
                values[key]=ci.getProperty(pd.name)
            if pd.kind in ['CI']:
                subvalues =  self._to_map(ci.getProperty(pd.name),key)
                values.update(subvalues)

        #print values
        #print "<_to_map: {0} {1}".format(ci.type,values)

        return values


    def generate_content(self, template_url):
        values = self._to_map(self.job,'job')
        template_content = self.load_classpath_resource(template_url)
        #print template_content
        template = Template(template_content)
        content = template.safe_substitute(values)
        #print "------------------------------"
        #print content
        #print "------------------------------"
        return content

    def _new_session(self):
        localopts = LocalConnectionOptions(os=OperatingSystemFamily.UNIX)
        localhost = OverthereHost(localopts)
        return OverthereHostSession(localhost)


    def run(self,command_line, raise_on_fail=False):
        session = self._new_session()
        #print command_line
        response = session.execute(command_line, suppress_streaming_output=True, check_success=False)
        if response.rc == 0:
            stdout =  "\n".join(response.stdout)
            return stdout
        else:
            if raise_on_fail:
                raise Exception("Kubectl Error when running '{0}':{1}".format(command_line,'\n'.join(response.stderr)))
            else:
                return '\n'.join(response.stderr)

    def _execute(self, session, command_line):
        #print command_line
        builder = BashScriptBuilder( )
        builder.add_line("cd {0}".format(session.work_dir().path))
        builder.add_line(command_line,check_rc=True)
        shell_file_content = builder.build()
        xld_apply_sh_file=session.upload_text_content_to_work_dir(shell_file_content,'XebiaLabsCLI.sh',executable=True)
        #print '-'*60
        #print xld_apply_sh_file
        #print '-'*60
        response = session.execute(xld_apply_sh_file.path, check_success=False, suppress_streaming_output=True)
        stdout = self._dump_response(response)
        if response.rc != 0:
            raise Exception("Failed to apply the resource definition :{0}".format(" ".join(response.stdout)))

    def _dump_response(self, response):
        for r in response.stdout:
            sys.stdout.write("{0}\n".format(r.encode('utf-8')))
        for r in response.stderr:
            sys.stderr.write("{0}\n".format(r.encode('utf-8')))
        if response.rc != 0:
            sys.stderr.write("Exit Code : {0}\n".format(response.rc))





print thisCi
import traceback
try:
    xl = XebiaLabsCLI(thisCi)
    commit = xl.get_last_commit()
    if not commit == thisCi.latestCommitId:
        xl.apply()
        thisCi.latestCommitId = commit
        repositoryService.update([thisCi])
except:
    raise Exception(str(traceback.format_exc()))
