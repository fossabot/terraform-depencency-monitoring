package com.github.dependencymonitoring.terraform.core.beans;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.github.dependencymonitoring.terraform.core.operations.parse.semver.Version;

import java.util.TreeSet;

/**
 * Class the represents a Terraform module and its versions.
 *
 * @author  <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 *
 */

@NoArgsConstructor
@ToString
public class TerraformModuleDefinition {
    @Getter @Setter private String name;
    @Getter @Setter private Version latestVersion;
    @Getter @Setter private TreeSet<Version> versions;
}
