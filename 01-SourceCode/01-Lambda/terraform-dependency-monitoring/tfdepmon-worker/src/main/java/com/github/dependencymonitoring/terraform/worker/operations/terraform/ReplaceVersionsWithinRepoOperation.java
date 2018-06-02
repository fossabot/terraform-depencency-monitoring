package com.github.dependencymonitoring.terraform.worker.operations.terraform;

import lombok.val;
import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;
import com.github.dependencymonitoring.terraform.core.operations.IBaseOperation;
import com.github.dependencymonitoring.terraform.worker.beans.TerraformModuleUpgrade;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ReplaceVersionsWithinRepoOperation implements IBaseOperation {

    private final List<TerraformModuleUpgrade> moduleUpgradeList;

    public ReplaceVersionsWithinRepoOperation(List<TerraformModuleUpgrade> moduleUpgradeList) {
        this.moduleUpgradeList = moduleUpgradeList;
    }

    @Override
    public void performOperation() throws OperationException {
        moduleUpgradeList.forEach(it -> {

            try {
                val tmpFile = File.createTempFile("tmpFile", ".tf");

                int lineNumber = 1;
                String lineStr;
                try (BufferedReader br = new BufferedReader(new FileReader(it.getFilePath())); FileOutputStream fout = new FileOutputStream(tmpFile)) {
                    while ((lineStr = br.readLine()) != null) {
                        if (lineNumber == it.getLineNumber()) {
                            lineStr = lineStr.replace(it.getCurrentVersion().format(), it.getProposedVersion().format());
                        }
                        fout.write(lineStr.getBytes());
                        fout.write(System.lineSeparator().getBytes());
                        lineNumber++;
                    }
                }

                Files.move(Paths.get(tmpFile.toURI()), Paths.get(it.getFilePath()), REPLACE_EXISTING);

            } catch (IOException e) {
                e.printStackTrace();
            }


        });
    }
}
