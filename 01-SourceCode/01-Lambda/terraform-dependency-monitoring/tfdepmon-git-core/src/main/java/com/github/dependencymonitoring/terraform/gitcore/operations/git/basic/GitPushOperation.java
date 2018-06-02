package com.github.dependencymonitoring.terraform.gitcore.operations.git.basic;

import com.github.dependencymonitoring.terraform.gitcore.operations.git.AbstractBaseGitOperation;
import com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

/**
 * Created by vagrant on 6/29/17.
 */
public class GitPushOperation extends AbstractGitBasicOperation {

    public GitPushOperation(String repoUrl, String branchName) {
        super(repoUrl, branchName);
    }

    @Override
    public void performOperation() throws OperationException {
        try (val git = Git.init().setDirectory(new File(IGitCoreConstants.TEMP_GIT_REPO_FOLDER)).call()) {

            val config = git.getRepository().getConfig();
            config.setString("remote", "origin", "url", repoUrl);
            config.save();

            val refSpec = new RefSpec(String.format("%s:%s", branchName, branchName));

            git.push()
                    .setCredentialsProvider(
                            new UsernamePasswordCredentialsProvider(AbstractBaseGitOperation.gitUsername, AbstractBaseGitOperation.gitPassword)
                    )
                    .setRefSpecs(refSpec).call();
            System.out.println("Pushed successfully \\o/");

        } catch (GitAPIException e) {
            throw wrapException(e);
        } catch (IOException e) {
            throw wrapException(e);
        }
    }
}
