package com.github.dependencymonitoring.terraform.gitcore.operations.git.github;

import lombok.val;
import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants.DEFAULT_ORGANIZATION;


/**
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
public class GitHubListProjectsOperation extends AbstractGitHubOperation<List<GHRepository>> {

    private String[] inclusions;
    private String[] exclusions;

    public GitHubListProjectsOperation(String[] inclusions, String[] exclusions) throws OperationException {
        this.inclusions = inclusions;
        this.exclusions = exclusions;
    }

    @Override
    public List<GHRepository> performOperation() throws OperationException {
        try {
            val repositories = gitHub.getOrganization(DEFAULT_ORGANIZATION).listRepositories();

            val repositoryList = new ArrayList<GHRepository>();
            for (val repository : repositories) {
                boolean add = true;

                if (inclusions != null && inclusions.length != 0) {
                    add = false;
                    for (val inclusion : inclusions) {
                        if (repository.getName().startsWith(inclusion)) {
                            add = true;
                            break;
                        }
                    }
                }
                if (exclusions != null && exclusions.length != 0) {
                    for (val exclusion : exclusions) {
                        if (repository.getName().startsWith(exclusion)) {
                            add = false;
                            break;
                        }
                    }
                }

                if (add)
                    repositoryList.add(repository);
            }
            return repositoryList;
        } catch (IOException e) {
            throw wrapException(e);
        }
    }
}
