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

import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.flow.StepExitCode;

public class CheckConnectionStep implements Step {

    private final GitRepository gitRepository;

    public CheckConnectionStep(GitRepository gitRepository) {
        this.gitRepository = gitRepository;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public String getDescription() {
        return "Check the connection to the Git Repository";
    }

    @Override
    public StepExitCode execute(ExecutionContext executionContext) throws Exception {
        GitClient gitClient = new GitClient(this.gitRepository);
        executionContext.logOutput("Connect to " + this.gitRepository.getUrl() + "/" + this.gitRepository.getBranch());
        String latestRevision = gitClient.getLatestRevision();
        executionContext.logOutput("latestRevision = " + latestRevision);
        return StepExitCode.SUCCESS;
    }
}
