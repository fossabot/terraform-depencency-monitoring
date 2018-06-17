package com.github.dependencymonitoring.terraform.core.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Class the represents the Lambda payload passed to the worker-dispatcher and
 * worker lambda functions
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
@NoArgsConstructor
@ToString
public class TerraformLambdaPayload {
    @Getter @Setter private TerraformProjectRepository terraformProjectRepository;
    @Getter @Setter private String sqsQueueUrl;
    @Getter @Setter private String sqsReceiptHandle;
    @Getter @Setter private List<TerraformModuleDefinition> terraformModuleDefinitions;
}
