package com.github.dependencymonitoring.terraform.core.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class that represents a Github repository that potentially
 * utilises terraform modules
 *
 * @author  <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 *
 */
@NoArgsConstructor
@ToString
public class TerraformProjectRepository {
    @Getter @Setter private String name;
    @Getter @Setter private String htmlUrl;
}
