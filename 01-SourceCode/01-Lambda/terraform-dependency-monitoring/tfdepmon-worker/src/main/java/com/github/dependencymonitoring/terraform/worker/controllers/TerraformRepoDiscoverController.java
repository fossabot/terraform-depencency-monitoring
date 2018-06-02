package com.github.dependencymonitoring.terraform.worker.controllers;

import lombok.val;
import com.github.dependencymonitoring.terraform.core.beans.TerraformLambdaPayload;
import com.github.dependencymonitoring.terraform.core.beans.TerraformModuleDefinition;
import com.github.dependencymonitoring.terraform.core.beans.TerraformProjectRepository;
import com.github.dependencymonitoring.terraform.core.controllers.TypedBaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;
import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import com.github.dependencymonitoring.terraform.core.operations.parse.semver.Version;
import com.github.dependencymonitoring.terraform.gitcore.operations.git.basic.GitCloneOperation;
import com.github.dependencymonitoring.terraform.worker.beans.TerraformModuleUpgrade;
import com.github.dependencymonitoring.terraform.worker.operations.terraform.TerraformRepoAnalyzerOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants.GIT_MASTER_BRANCH;
import static com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants.GIT_RELEASE_BRANCH;

class TerraformRepoDiscoverController extends TypedBaseController<Map<String, List<TerraformModuleUpgrade>>> {

    private final TerraformLambdaPayload lambdaPayload;

    TerraformRepoDiscoverController(TerraformLambdaPayload lambdaPayload) {
        this.lambdaPayload = lambdaPayload;
    }

    @Override
    public Map<String, List<TerraformModuleUpgrade>> execute() throws ControllerException {
        try {
            Map<String, List<TerraformModuleUpgrade>> returnMap = new HashMap<>();

            val projectRepository = lambdaPayload.getTerraformProjectRepository();
            val repoHtmlUrl = projectRepository.getHtmlUrl();

            Map<String, List<TerraformModuleUpgrade>> analysisMap;

            // By default all repositories should use the Git flow process and subsequently, they should have a release branch
            analysisMap = downloadAndAnalyse(projectRepository, repoHtmlUrl, GIT_RELEASE_BRANCH);
            if (analysisMap.isEmpty()) {
                // However, in some cases I found a few terraform stack repos that don't use it, hence, I will double check it
                analysisMap = downloadAndAnalyse(projectRepository, repoHtmlUrl, GIT_MASTER_BRANCH);
            }

            returnMap.putAll(analysisMap);

            if (!returnMap.isEmpty()) {
                // Move modules definition to a Map structure to avoid iterating through the list many times.
                Map<String, TerraformModuleDefinition> terraformModuleDefinitionMap = buildTerraformModuleDefinitionMap();

                // Evaluate potential upgrades
                logger.info("Evaluating potential upgrades");
                findPotentialPainlessUpgrades(returnMap, terraformModuleDefinitionMap);

                // Remove non-upgradeable modules
                returnMap = removeNonUpgradeableModules(returnMap);
            }

            return returnMap;
        } catch (OperationException e) {
            throw this.wrapException("Error during the Terraform repository discovery", e);
        }
    }

    private Map<String, List<TerraformModuleUpgrade>> downloadAndAnalyse(TerraformProjectRepository projectRepository, String repoHtmlUrl, String gitBranch) throws OperationException {
        Map<String, List<TerraformModuleUpgrade>> analysisMap;
        logger.info("Cloning " + repoHtmlUrl);
        val cloneOperation = new GitCloneOperation(repoHtmlUrl, gitBranch);
        cloneOperation.performOperation();

        // Analyse repository
        logger.info("Analysing source code");
        val terraformRepoAnalyzerOperation = new TerraformRepoAnalyzerOperation(projectRepository, gitBranch);
        analysisMap = terraformRepoAnalyzerOperation.performOperation();
        return analysisMap;
    }

    private Map<String, TerraformModuleDefinition> buildTerraformModuleDefinitionMap() {
        val terraformModuleDefinitionMap = new HashMap<String, TerraformModuleDefinition>(lambdaPayload.getTerraformModuleDefinitions().size());
        for (val moduleDefinition : lambdaPayload.getTerraformModuleDefinitions()) {
            terraformModuleDefinitionMap.put(moduleDefinition.getName(), moduleDefinition);
        }
        return terraformModuleDefinitionMap;
    }

    private void findPotentialPainlessUpgrades(Map<String, List<TerraformModuleUpgrade>> returnMap, Map<String, TerraformModuleDefinition> terraformModuleDefinitionMap) {
        for (val entry : returnMap.entrySet()) {
            logger.info("Usages of " + entry.getKey());

            for (val moduleUpgrade : entry.getValue()) {
                val moduleDefinition = terraformModuleDefinitionMap.get(moduleUpgrade.getModuleName());

                Version possibleUpgrade = moduleUpgrade.getCurrentVersion();
                for (val version : moduleDefinition.getVersions()) {
                    if (version.getMajor() == possibleUpgrade.getMajor()) {
                        if (version.getMinor() > possibleUpgrade.getMinor()) {
                            possibleUpgrade = version;
                        } else if (version.getMinor() == possibleUpgrade.getMinor()) {
                            if (version.getFix() > possibleUpgrade.getFix()) {
                                possibleUpgrade = version;
                            }
                        }
                    }
                }
                moduleUpgrade.setProposedVersion(possibleUpgrade);
                logger.info(String.format(
                        "Current version: %s latest backwards-compatible version: %s",
                        moduleUpgrade.getCurrentVersion().format(),
                        moduleDefinition.getLatestVersion().format()
                ));
            }
        }
    }

    private Map<String, List<TerraformModuleUpgrade>> removeNonUpgradeableModules(Map<String, List<TerraformModuleUpgrade>> returnMap) {
        returnMap.entrySet().forEach((entry) ->
                entry.setValue(entry.getValue().stream().filter(
                        (tm) -> !tm.getCurrentVersion().equals(tm.getProposedVersion())
                ).collect(Collectors.toList()))
        );
        returnMap = returnMap.entrySet().stream().filter((entry) -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return returnMap;
    }


}
