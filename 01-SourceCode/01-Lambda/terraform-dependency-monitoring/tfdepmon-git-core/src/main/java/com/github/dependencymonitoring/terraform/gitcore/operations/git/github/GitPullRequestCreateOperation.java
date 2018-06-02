package com.github.dependencymonitoring.terraform.gitcore.operations.git.github;

import com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import org.kohsuke.github.GHPullRequest;

import java.io.IOException;

public class GitPullRequestCreateOperation extends AbstractGitHubRepoOperation<GHPullRequest> {

    private String title, head, base, body;

    public GitPullRequestCreateOperation(String repoName, String title, String head, String base, String body) throws OperationException {
        super(repoName);
        this.title = title;
        this.head = head;
        this.base = base;
        this.body = body;
    }

    @Override
    public GHPullRequest performOperation() throws OperationException {
        try {
            val repository = gitHub.getOrganization(IGitCoreConstants.DEFAULT_ORGANIZATION).getRepository(repoName);
            return repository.createPullRequest(title, head, base, body);
        } catch (IOException e) {
            throw wrapException(e);
        }
    }
}
