package com.github.dependencymonitoring.terraform.gitcore.operations.git.basic;

import lombok.val;
import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

import static com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants.GIT_DEFAULT_PUSHER;
import static com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants.TEMP_GIT_REPO_FOLDER;

/**
 * Created by vagrant on 6/29/17.
 */
public class GitCommitOperation extends AbstractGitBasicOperation {

    private final String message;

    public GitCommitOperation(String repoUrl, String branchName, String message) {
        super(repoUrl, branchName);
        this.message = message;
    }

    @Override
    public void performOperation() throws OperationException {
        try (val git = Git.init().setDirectory(new File(TEMP_GIT_REPO_FOLDER)).call()) {

            git.add().addFilepattern(".").call();

            // Stage all changed files, omitting new files, and commit with one command
            git.commit()
                    .setAll(true)
                    .setMessage(String.format("[%s] :: %s", GIT_DEFAULT_PUSHER, message))
                    .call();

            System.out.println("File was successfully committed");

        } catch (GitAPIException e) {
            throw wrapException(e);
        }
    }
}
