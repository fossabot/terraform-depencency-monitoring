package com.github.dependencymonitoring.terraform.workerdispatcher.parse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class LambdaPayload {
    @Getter @Setter private String sqsQueueName;
    @Getter @Setter private int workerReservedConcurrentExecutions;
    @Getter @Setter private String lambdaWorkerFunctionName;
    @Getter @Setter private String s3BucketName;
}
