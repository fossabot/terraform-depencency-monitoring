package com.github.dependencymonitoring.terraform.core.beans;

import lombok.*;

/**
 * Class that represents the message sent to Amazon Simple Queue Service
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TerraformSQSMessage {
    @Getter @Setter private TerraformProjectRepository terraformProjectRepository;
    @Getter @Setter private String modulesKey;
}
