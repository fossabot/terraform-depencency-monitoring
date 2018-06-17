package com.github.dependencymonitoring.terraform.core.operations.parse.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Class the represents the Lambda payload sent by the Amazon CloudWatch Events rules
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
@NoArgsConstructor
@ToString
public class LambdaPayload {
    /**
     * S3 bucket name
     */
    @Getter @Setter private String bucketName;
    /**
     * S3 key
     */
    @Getter @Setter private String key;
    /**
     * List of inclusions (currently used for both projects and terraform modules)
     */
    @Getter @Setter private List<String> inclusions;
    /**
     * List of exclusions (currently used for both projects and terraform modules)
     */
    @Getter @Setter private List<String> exclusions;
}
