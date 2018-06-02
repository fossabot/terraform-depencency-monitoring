package com.github.dependencymonitoring.terraform.worker.utils;

public interface IWorkerConstants {

    String PR_TITLE_TEMPLATE = "[soltius-tfdepmon] :: Upgrade dependency %s to %s";
    String PR_BODY_TEMPLATE =
            "A new version of %s has been released.\n" +
            "Make sure that it doesn't break anything, and happy merging!";
    String BRANCH_NAME_TEMPLATE = "%s-eq-%s";
    String COMMIT_MESSAGE_TEMPLATE = "Upgrade dependency %s to %s";

}
