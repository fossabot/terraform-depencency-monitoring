package com.github.dependencymonitoring.terraform.gitcore.operations.git.github;


import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import com.github.dependencymonitoring.terraform.core.operations.ITypedOperation;
import com.github.dependencymonitoring.terraform.gitcore.operations.git.AbstractBaseGitOperation;
import org.kohsuke.github.GitHub;

import java.io.IOException;

/**
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
abstract class AbstractGitHubOperation<T> extends AbstractBaseGitOperation implements ITypedOperation<T> {

    GitHub gitHub;

    AbstractGitHubOperation() throws OperationException {
        try {
            gitHub = GitHub.connectUsingPassword(gitUsername, gitPassword);
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    OperationException wrapException(String message, Throwable e) {
        logger.severe(message);
        return new OperationException(message, e);
    }

    OperationException wrapException(Throwable e) {
        return this.wrapException("Error when performing operation", e);
    }

}
