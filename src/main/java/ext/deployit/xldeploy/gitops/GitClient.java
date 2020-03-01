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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

import java.util.Collection;

import static java.lang.String.format;
import static org.eclipse.jgit.lib.Constants.HEAD;
import static org.eclipse.jgit.lib.Constants.R_HEADS;

public class GitClient {

    static final ThreadLocal<GitRepository> currentGitConnectionSettings = new ThreadLocal<>();

    public GitClient(GitRepository gitRepository) {
        currentGitConnectionSettings.set(gitRepository);
    }

    public String getLatestRevision() throws GitAPIException {
        return getLatestRevisionObjectId().getName();
    }

    public ObjectId getLatestRevisionObjectId() throws GitAPIException {
        Collection<Ref> refs = fetchRemoteReferences();
        String branch = currentGitConnectionSettings.get().getBranch();
        return branch != null && !branch.isEmpty() ? findBranchSha1(refs, currentGitConnectionSettings.get().getBranch()) : findDefaultBranchSha1(refs);
    }

    private Collection<Ref> fetchRemoteReferences() throws GitAPIException {
        LsRemoteCommand lsRemoteCommand = Git.lsRemoteRepository().setRemote(currentGitConnectionSettings.get().getUrl()).setTags(false);
        return lsRemoteCommand.call();
    }

    private ObjectId findBranchSha1(Collection<Ref> refs, String branch) {
        return findSha1(refs, R_HEADS + branch);
    }

    private ObjectId findDefaultBranchSha1(Collection<Ref> refs) {
        return findSha1(refs, HEAD);
    }

    private ObjectId findSha1(Collection<Ref> refs, String input) {
        for (Ref ref : refs) {
            if (ref.getName().equals(input)) {
                return ref.getObjectId();
            }
        }
        throw new IllegalArgumentException(format("'%s' not found on %s", input, currentGitConnectionSettings.get().getUrl()));
    }
}
