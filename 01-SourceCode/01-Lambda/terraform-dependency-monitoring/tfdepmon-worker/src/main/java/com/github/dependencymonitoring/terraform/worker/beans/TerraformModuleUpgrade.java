package com.github.dependencymonitoring.terraform.worker.beans;

import lombok.*;
import com.github.dependencymonitoring.terraform.core.beans.TerraformProjectRepository;
import com.github.dependencymonitoring.terraform.core.operations.parse.semver.Version;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class TerraformModuleUpgrade {
    @Getter @Setter private String moduleName;
    @Getter @Setter private Version currentVersion;
    @Getter @Setter private Version proposedVersion;
    @Getter @Setter private String filePath;
    @Getter @Setter private int lineNumber;
    @Getter @Setter private TerraformProjectRepository repository;
    @Getter @Setter private String branch;
}
