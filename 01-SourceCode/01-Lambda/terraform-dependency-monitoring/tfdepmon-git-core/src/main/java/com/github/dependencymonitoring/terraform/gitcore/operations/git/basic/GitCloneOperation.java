package com.github.dependencymonitoring.terraform.gitcore.operations.git.basic;

import com.github.dependencymonitoring.terraform.gitcore.utils.FileUtils;
import com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;


/**
 * @author <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 */
public class GitCloneOperation extends AbstractGitBasicOperation {

    public GitCloneOperation(String repoUrl, String branchName) {
        super(repoUrl, branchName);
    }

    private void cleanUp() throws OperationException {
        try {
            // Clean up the folder used to clone the repository
            val file = new File(IGitCoreConstants.TEMP_GIT_REPO_FOLDER);
            if (file.exists()) {
                FileUtils.removeDirectory(file.getPath());
            }
            if (!file.mkdirs()) {
                throw new IOException(String.format("I wasn't able to recursively remove the %s folder",
                        IGitCoreConstants.TEMP_GIT_REPO_FOLDER));
            }
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    @Override
    public void performOperation() throws OperationException {
        try {
            // clean things up
            cleanUp();
            // cloning the repo
            val file = new File(IGitCoreConstants.TEMP_GIT_REPO_FOLDER);

            logger.info("Cloning from " + repoUrl + " to " + IGitCoreConstants.TEMP_GIT_REPO_FOLDER);
            try (val result = Git.cloneRepository()
                    .setURI(this.repoUrl)
                    .setDirectory(file)
                    .setBranch(this.branchName)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitUsername, gitPassword))
                    .call()) {
                // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
                logger.fine("Cloned: " + result.getRepository().getDirectory());
            } catch (GitAPIException e) {
                throw wrapException(e);
            }
        } catch (IOException e) {
            throw wrapException(e);
        }
    }


}
