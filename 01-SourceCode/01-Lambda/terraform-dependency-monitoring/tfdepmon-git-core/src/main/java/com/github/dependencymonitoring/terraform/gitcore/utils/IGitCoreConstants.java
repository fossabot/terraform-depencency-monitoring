package com.github.dependencymonitoring.terraform.gitcore.utils;

/**
 *
 * @author  <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 *
 */
public interface IGitCoreConstants {

    String TEMP_GIT_REPO_FOLDER = "/tmp/repo";
    String DEFAULT_ORGANIZATION = "soltius";
    String GIT_MASTER_BRANCH = "master";
    String GIT_RELEASE_BRANCH = "release";
    String VERSION_FILE = "VERSION";
    String GIT_DEFAULT_PUSHER = "solt-github-automation";

    // Create Refs
    String GIT_CREATE_BRANCH_FORMAT = "refs/heads/%s";
}
