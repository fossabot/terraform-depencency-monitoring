package com.github.dependencymonitoring.terraform.gatherterraformmodules.controllers;

import com.amazonaws.util.IOUtils;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.beans.TerraformModuleDefinition;
import com.github.dependencymonitoring.terraform.core.controllers.TypedBaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;
import com.github.dependencymonitoring.terraform.core.operations.parse.semver.Version;
import com.github.dependencymonitoring.terraform.core.operations.parse.semver.VersionParseOperation;
import com.github.dependencymonitoring.terraform.gitcore.operations.git.github.GitHubListProjectsOperation;
import com.github.dependencymonitoring.terraform.gitcore.operations.git.github.GitHubListTagsOperation;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;

import static com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants.GIT_MASTER_BRANCH;
import static com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants.VERSION_FILE;


/**
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
class TerraformModuleInfoController extends TypedBaseController<Map<String, TerraformModuleDefinition>> {

    private final String[] inclusions;
    private final String[] exclusions;
    private Map<String, TerraformModuleDefinition> terraformModuleMap;

    TerraformModuleInfoController(String[] inclusions, String[] exclusions) {
        this.inclusions = inclusions;
        this.exclusions = exclusions;
        this.terraformModuleMap = new HashMap<>();
    }

    @Override
    public Map<String, TerraformModuleDefinition> execute() throws ControllerException {
        try {
            val terraformModuleRepos = new GitHubListProjectsOperation(
                    inclusions, exclusions
            ).performOperation();


            ForkJoinPool customThreadPool = new ForkJoinPool(30);
            final List<GHRepository> finalTerraformModuleRepos = terraformModuleRepos;
            customThreadPool.submit(() -> finalTerraformModuleRepos.stream().parallel().forEach(repository -> {
                logger.info("[GHRepository] :: Name: " + repository.getFullName() + " Url: " + repository.getHtmlUrl());

                try {
                    GHContent versionFileContent = repository.getFileContent(VERSION_FILE, GIT_MASTER_BRANCH);
                    String versionString = IOUtils.toString(versionFileContent.read());

                    // Obtaining latest version
                    VersionParseOperation versionParseOperation = new VersionParseOperation(versionString);
                    Version latestVersion = versionParseOperation.performOperation();

                    // Obtaining possible versions
                    GitHubListTagsOperation gitHubListTagsOperation = new GitHubListTagsOperation(repository.getFullName());
                    List<GHTag> tagList = gitHubListTagsOperation.performOperation();

                    // Wrapping in to Version classes
                    TreeSet<Version> listVersions = new TreeSet<>();
                    for (GHTag ghTag : tagList) {
                        listVersions.add(new VersionParseOperation(ghTag.getName()).performOperation());
                    }

                    // finally add it to the HaspMap O(1)
                    TerraformModuleDefinition terraformModuleDefinition = new TerraformModuleDefinition();
                    terraformModuleDefinition.setName(repository.getName());
                    terraformModuleDefinition.setLatestVersion(latestVersion);
                    terraformModuleDefinition.setVersions(listVersions);

                    terraformModuleMap.put(repository.getName(), terraformModuleDefinition);

                    logger.info("[Terraform-mod-*] :: Name: " + repository.getName() + " Latest version: " + VersionParseOperation.VersionFormatter.format(latestVersion));
                } catch (Exception e) {
                    logger.severe("[Terraform-mod-*] :: I wasn't able to read VERSION file from " + repository.getFullName());
                }
            })).get();
            return terraformModuleMap;
        } catch (Exception e) {
            throw wrapException("Error while gethering terraform repositories", e);
        }
    }
}
