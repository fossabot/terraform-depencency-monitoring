package com.github.dependencymonitoring.terraform.gathergithubrepos.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import com.github.dependencymonitoring.terraform.core.controllers.TypedBaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;
import com.github.dependencymonitoring.terraform.gitcore.operations.git.github.GitHubListProjectsOperation;
import org.kohsuke.github.GHRepository;

import java.util.List;

/**
 * @author <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class TerraformRepoDiscoverController extends TypedBaseController<List<GHRepository>> {

    private final String[] inclusions;
    private final String[] exclusions;

    @Override
    public List<GHRepository> execute() throws ControllerException {

        try {
            return new GitHubListProjectsOperation(
                    inclusions,
                    exclusions
            ).performOperation();
        } catch (Exception e) {
            throw wrapException("Error when obtaining the list of projects from github", e);
        }
    }
}
