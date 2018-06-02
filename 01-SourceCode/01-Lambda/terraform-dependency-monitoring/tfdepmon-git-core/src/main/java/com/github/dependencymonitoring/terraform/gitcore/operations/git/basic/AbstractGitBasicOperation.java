package com.github.dependencymonitoring.terraform.gitcore.operations.git.basic;


import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import com.github.dependencymonitoring.terraform.core.operations.IBaseOperation;
import com.github.dependencymonitoring.terraform.gitcore.operations.git.AbstractBaseGitOperation;

import java.util.logging.Logger;

/**
 * @author <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 */
abstract class AbstractGitBasicOperation extends AbstractBaseGitOperation implements IBaseOperation {

    protected Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    String repoUrl;
    String branchName;

    AbstractGitBasicOperation(String repoUrl, String branchName) {
        this.repoUrl = repoUrl;
        this.branchName = branchName;
    }

    OperationException wrapException(String message, Throwable e) {
        logger.severe(message);
        return new OperationException(message, e);
    }

    OperationException wrapException(Throwable e) {
        return this.wrapException("Error when performing operation", e);
    }
}
