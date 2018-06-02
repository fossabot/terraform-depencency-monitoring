package com.github.dependencymonitoring.terraform.worker.operations.terraform;

import lombok.val;
import com.github.dependencymonitoring.terraform.core.beans.TerraformProjectRepository;
import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import com.github.dependencymonitoring.terraform.core.operations.ITypedOperation;
import com.github.dependencymonitoring.terraform.core.operations.parse.semver.Version;
import com.github.dependencymonitoring.terraform.worker.beans.TerraformModuleUpgrade;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static com.github.dependencymonitoring.terraform.gitcore.utils.IGitCoreConstants.TEMP_GIT_REPO_FOLDER;


/**
 * @author <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 */
public class TerraformRepoAnalyzerOperation implements ITypedOperation<Map<String, List<TerraformModuleUpgrade>>> {

    private TerraformProjectRepository repository;
    private final String branchName;
    private Map<String, List<TerraformModuleUpgrade>> modulesUpgradeMap = new HashMap<>();

    // Logger
    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    // Regex filters
    private static final String REGEX_FIND_SOURCE = "source[\\s\\t]*\\=[\\s\\t]*\\\"(.*?)\\\"";
    private static final String REGEX_FIND_MODULE_SOURCE = "git\\@github\\.com\\:([A-Za-z0-9\\-\\_]*?)\\/([A-Za-z0-9\\-\\_\\.]*?)\\.git\\?ref\\=([v\\d\\.]+)\\/?";

    public TerraformRepoAnalyzerOperation(TerraformProjectRepository repository, String branchName) {
        this.repository = repository;
        this.branchName = branchName;
        modulesUpgradeMap = new HashMap<>();
    }

    @Override
    public Map<String, List<TerraformModuleUpgrade>> performOperation() throws OperationException {
        try {

            // List all terraform files
            val dir = new File(TEMP_GIT_REPO_FOLDER);
            val listFiles = (Collection<File>) FileUtils.listFiles(dir, new SuffixFileFilter(".tf"), DirectoryFileFilter.INSTANCE);

            if (!listFiles.isEmpty()) {
                logger.info(String.format("[Repo: %s] : Looks a promising repo to investigate further", repository.getName()));

                // Trying to find source references
                for (val file : listFiles) {
                    // Parsing its contents
                    val fileContents = FileUtils.readFileToString(file);

                    // Finding source declaration and line number
                    val findSourcePattern = Pattern.compile(REGEX_FIND_SOURCE);
                    val moduleSourcePattern = Pattern.compile(REGEX_FIND_MODULE_SOURCE);

                    val scanner = new Scanner(fileContents);
                    int lineNumber = 1;
                    while (scanner.hasNextLine()) {
                        val matcher = findSourcePattern.matcher(scanner.nextLine());

                        while (matcher.find()) {
                            val found = matcher.group(1);
                            logger.info("####### Found this one " + found);

                            // Variables section
                            String terraform_module = "";
                            String terraform_version = "";

                            if (found.startsWith("git@")) {

                                // Figuring out what terraform stack this found modules belongs to.
                                val matcherTerraformModule = moduleSourcePattern.matcher(found);
                                while (matcherTerraformModule.find()) {
                                    terraform_module = matcherTerraformModule.group(2);
                                    terraform_version = matcherTerraformModule.group(3);
                                }

                                logger.info(String.format("############################## %s %s", terraform_module, terraform_version));

                                // Finally upsert the terraform module usage
                                if (!terraform_module.isEmpty() && !terraform_version.isEmpty()) {

                                    val moduleUpgrade = new TerraformModuleUpgrade();
                                    moduleUpgrade.setFilePath(file.getPath());
                                    moduleUpgrade.setLineNumber(lineNumber);
                                    moduleUpgrade.setModuleName(terraform_module);
                                    moduleUpgrade.setCurrentVersion(Version.factory(terraform_version));
                                    moduleUpgrade.setRepository(repository);
                                    moduleUpgrade.setBranch(branchName);

                                    upsertToMap(terraform_module, moduleUpgrade);
                                }
                            }
                        }
                        lineNumber++;
                    }

                }
            } else {
                logger.info(String.format("[Repo: %s] : This is certainly not a terraform stack repo... skipping it", repository.getName()));
            }
        } catch (IOException e) {
            throw new OperationException("Error when analysing terraform repository", e);
        }

        return modulesUpgradeMap;
    }

    private void upsertToMap(String terraformModuleKey, TerraformModuleUpgrade terraformModuleUpgradeValue) {
        List<TerraformModuleUpgrade> moduleUpgrades;
        if (modulesUpgradeMap.containsKey(terraformModuleKey)) {
            moduleUpgrades = modulesUpgradeMap.get(terraformModuleKey);
            moduleUpgrades.add(terraformModuleUpgradeValue);
        } else {
            moduleUpgrades = new ArrayList<>();
            moduleUpgrades.add(terraformModuleUpgradeValue);
            modulesUpgradeMap.put(terraformModuleKey, moduleUpgrades);
        }
    }


}
