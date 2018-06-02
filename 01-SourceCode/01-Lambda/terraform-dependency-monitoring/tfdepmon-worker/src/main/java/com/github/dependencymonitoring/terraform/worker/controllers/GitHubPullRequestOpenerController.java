package com.github.dependencymonitoring.terraform.worker.controllers;

import com.github.dependencymonitoring.terraform.worker.operations.terraform.ReplaceVersionsWithinRepoOperation;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.beans.TerraformLambdaPayload;
import com.github.dependencymonitoring.terraform.core.controllers.BaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;
import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import com.github.dependencymonitoring.terraform.core.operations.parse.semver.Version;
import com.github.dependencymonitoring.terraform.gitcore.operations.git.basic.GitCloneOperation;
import com.github.dependencymonitoring.terraform.gitcore.operations.git.basic.GitCommitOperation;
import com.github.dependencymonitoring.terraform.gitcore.operations.git.basic.GitPushOperation;
import com.github.dependencymonitoring.terraform.gitcore.operations.git.github.GitHubCreateBranchOperation;
import com.github.dependencymonitoring.terraform.gitcore.operations.git.github.GitHubPullRequestSearchOperation;
import com.github.dependencymonitoring.terraform.gitcore.operations.git.github.GitPullRequestCreateOperation;
import com.github.dependencymonitoring.terraform.worker.beans.TerraformModuleUpgrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.dependencymonitoring.terraform.worker.utils.IWorkerConstants.*;

class GitHubPullRequestOpenerController extends BaseController {

    private final TerraformLambdaPayload lambdaPayload;
    private final Map<String, List<TerraformModuleUpgrade>> modulesUpgradeMap;

    GitHubPullRequestOpenerController(TerraformLambdaPayload lambdaPayload, Map<String, List<TerraformModuleUpgrade>> modulesUpgradeMap) {
        this.lambdaPayload = lambdaPayload;
        this.modulesUpgradeMap = modulesUpgradeMap;
    }

    @Override
    public void execute() throws ControllerException {
        for (Map.Entry<String, List<TerraformModuleUpgrade>> entry : this.modulesUpgradeMap.entrySet()) {
            // organise PRs per module/version
            val tmpVerModUpgradeMap = groupByModuleVersion(entry);

            // filter out PRs that have been already created in the past for the same module.
            val unOpenedPRs = tmpVerModUpgradeMap.entrySet().stream().filter(
                    this::prDoesntExist).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (!unOpenedPRs.isEmpty()) {
                for (val listEntry : unOpenedPRs.entrySet()) {
                    val modUpg = listEntry.getValue().stream().findAny().get();
                    val newBranchName = String.format(BRANCH_NAME_TEMPLATE, modUpg.getModuleName(), modUpg.getProposedVersion().format());

                    try {
                        // Create branch for the PR
                        val createBranchOperation = new GitHubCreateBranchOperation(
                                modUpg.getRepository().getName(),
                                modUpg.getBranch(),
                                newBranchName
                        );
                        createBranchOperation.performOperation();

                        // Clone just created branch
                        val cloneOperation = new GitCloneOperation(modUpg.getRepository().getHtmlUrl(), newBranchName);
                        cloneOperation.performOperation();

                        // Make the changes
                        val replaceVersionsOperation = new ReplaceVersionsWithinRepoOperation(listEntry.getValue());
                        replaceVersionsOperation.performOperation();

                        // Commit and push
                        val commitOperation = new GitCommitOperation(
                                modUpg.getRepository().getHtmlUrl(),
                                newBranchName,
                                String.format(COMMIT_MESSAGE_TEMPLATE, modUpg.getModuleName(), modUpg.getProposedVersion().format())
                        );
                        commitOperation.performOperation();
                        val pushOperation = new GitPushOperation(modUpg.getRepository().getHtmlUrl(), newBranchName);
                        pushOperation.performOperation();

                        // Open the PR.
                        val pullRequestCreateOperation = new GitPullRequestCreateOperation(
                                modUpg.getRepository().getName(),
                                String.format(PR_TITLE_TEMPLATE, modUpg.getModuleName(), modUpg.getProposedVersion().format()),
                                newBranchName,
                                modUpg.getBranch(),
                                String.format(PR_BODY_TEMPLATE, modUpg.getModuleName())
                        );
                        pullRequestCreateOperation.performOperation();
                    } catch (OperationException e) {
                        throw this.wrapException("Error when opening a Pull Request", e);
                    }
                }
            }

        }
    }

    private Map<Version, List<TerraformModuleUpgrade>> groupByModuleVersion(Map.Entry<String, List<TerraformModuleUpgrade>> entry) {
        val tmpVerModUpgradeMap = new HashMap<Version, List<TerraformModuleUpgrade>>();
        for (val moduleUpgrade : entry.getValue()) {
            if (tmpVerModUpgradeMap.containsKey(moduleUpgrade.getProposedVersion())) {
                tmpVerModUpgradeMap.get(moduleUpgrade.getProposedVersion()).add(moduleUpgrade);
            } else {
                val list = new ArrayList<TerraformModuleUpgrade>();
                list.add(moduleUpgrade);
                tmpVerModUpgradeMap.put(moduleUpgrade.getProposedVersion(), list);
            }
        }
        return tmpVerModUpgradeMap;
    }

    private boolean prDoesntExist(Map.Entry<Version, List<TerraformModuleUpgrade>> entry) {
        boolean retValue = false;
        val projectRepository = this.lambdaPayload.getTerraformProjectRepository();
        val modUpg = entry.getValue().stream().findAny().get();

        try {
            val pullRequestSearchOperation = new GitHubPullRequestSearchOperation(
                    projectRepository.getName(),
                    String.format(PR_TITLE_TEMPLATE, modUpg.getModuleName(), modUpg.getProposedVersion().format()));
            retValue = pullRequestSearchOperation.performOperation().isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error while trying to check whether or not a PR exists");
        }
        return retValue;
    }
}
