package com.github.dependencymonitoring.terraform.gitcore.operations.git.github;

import lombok.val;
import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import com.github.dependencymonitoring.terraform.core.operations.parse.semver.VersionParseOperation;
import org.kohsuke.github.GHTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
public class GitHubListTagsOperation extends AbstractGitHubRepoOperation<List<GHTag>> {

    public GitHubListTagsOperation(String repo) throws OperationException {
        super(repo);
    }

    @Override
    public List<GHTag> performOperation() throws OperationException {
        try {
            val repository = gitHub.getRepository(repoName);
            val ghTags = repository.listTags().asList();

            // Removing non VERSION file complaint.
            val tagList = new ArrayList<GHTag>(ghTags.size());
            for (val ghTag : ghTags) {
                boolean add = true;
                try {
                    val versionParseOperation = new VersionParseOperation(ghTag.getName());
                    versionParseOperation.performOperation();
                } catch (Exception ex) {
                    // Ignore as this won't be part of the return list
                    add = false;
                }
                if (add)
                    tagList.add(ghTag);
            }

            return tagList;
        } catch (IOException e) {
            throw wrapException(e);
        }
    }
}
