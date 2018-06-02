package com.github.dependencymonitoring.terraform.gitcore.operations.git.github;

import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import org.kohsuke.github.GHIssue;

import java.util.List;

import static com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants.DEFAULT_ORGANIZATION;

public class GitHubPullRequestSearchOperation extends AbstractGitHubRepoOperation<List<GHIssue>> {

    private final String inTitleText;

    public GitHubPullRequestSearchOperation(String repoName, String inTitleText) throws OperationException {
        super(repoName);
        this.inTitleText = inTitleText;
    }

    @Override
    public List<GHIssue> performOperation() throws OperationException {
        return gitHub.searchIssues().q(
                String.format("repo:%s/%s is:pr \"%s\" in:title ", DEFAULT_ORGANIZATION, this.repoName, this.inTitleText)
        ).list().asList();
    }
}
