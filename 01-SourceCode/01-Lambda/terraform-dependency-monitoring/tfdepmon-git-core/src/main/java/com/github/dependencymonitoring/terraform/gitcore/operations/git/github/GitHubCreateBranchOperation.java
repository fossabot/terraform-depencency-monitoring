package com.github.dependencymonitoring.terraform.gitcore.operations.git.github;

import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRef;
import org.kohsuke.github.GHRepository;

import java.io.IOException;

import static com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants.DEFAULT_ORGANIZATION;
import static com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants.GIT_CREATE_BRANCH_FORMAT;

public class GitHubCreateBranchOperation extends AbstractGitHubRepoOperation<GHRef> {

    private final String branch;
    private final String toBranch;

    public GitHubCreateBranchOperation(String repoName, String fromBranch, String toBranch) throws OperationException {
        super(repoName);
        this.branch = fromBranch;
        this.toBranch = toBranch;
    }

    @Override
    public GHRef performOperation() throws OperationException {
        try {
            GHRepository repository = gitHub.getOrganization(DEFAULT_ORGANIZATION).getRepository(repoName);
            GHBranch branch = repository.getBranch(this.branch);
            return repository.createRef(
                    String.format(GIT_CREATE_BRANCH_FORMAT, toBranch),
                    branch.getSHA1()
            );
        } catch (IOException e) {
            throw wrapException(e);
        }
    }
}
