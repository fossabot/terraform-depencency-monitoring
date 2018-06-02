package com.github.dependencymonitoring.terraform.gitcore.operations.git;


import lombok.val;
import com.github.dependencymonitoring.terraform.core.services.kms.KMSService;

import java.util.logging.Logger;

/**
 *
 * @author  <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 *
 */
public abstract class AbstractBaseGitOperation {
    // Logger
    protected Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    protected static String gitUsername;
    protected static String gitPassword;

    static {
        val kms = KMSService.getInstance();
        gitUsername = kms.decryptKey(System.getenv("git_username"));
        gitPassword = kms.decryptKey(System.getenv("git_password"));
    }
}
