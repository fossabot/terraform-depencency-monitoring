package com.github.dependencymonitoring.terraform.gitcore.operations.git.github;

import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;

/**
 *
 * @author  <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 *
 */
abstract class AbstractGitHubRepoOperation<T> extends AbstractGitHubOperation<T>{

    String repoName;

    AbstractGitHubRepoOperation(String repoName) throws OperationException {
        this.repoName = repoName;
    }

}
