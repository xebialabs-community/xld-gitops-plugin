/**
 * Copyright 2020 XEBIALABS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ext.deployit.xldeploy.gitops;

import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.udm.*;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
@Metadata(root = Metadata.ConfigurationItemRoot.CONFIGURATION)
public class GitRepository extends Configuration {

    @Property(required = true)
    private String url;

    @Property(required = true,defaultValue = "master")
    private String branch;

    @Property(required = true)
    private String path;

    @Property(required = true)
    private String latestCommitId;

    public  GitRepository() {

    }

    public GitRepository(String url, String branch, String path, String latestCommitId) {
        this.url = url;
        this.branch = branch;
        this.path = path;
        this.latestCommitId = latestCommitId;
    }

    public String getBranch() {
        return branch;
    }

    public String getUrl() {
        return url;
    }

    @ControlTask(description = "Check Connection to the Git Repositoy")
    public List<Step> checkConnection()  {
        return null;
    }
}
