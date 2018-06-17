package com.github.dependencymonitoring.terraform.core.services.lambda;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import lombok.val;

/**
 * AWS Lambda base implementation
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
public class LambdaService {
    /**
     * singleton instance variable
     */
    private static LambdaService instance;
    /**
     * AWS Lambda client instance variable
     */
    private AWSLambda awsLambda;

    /**
     * Default constructor
     */
    private LambdaService() {
        awsLambda = AWSLambdaClientBuilder.standard()
                .withRegion(Regions.fromName(System.getenv("AWS_DEFAULT_REGION")))
                .build();
    }

    /**
     * Obtains a singleton instance of the service
     *
     * @return - LambdaService instance
     */
    public static LambdaService getInstance() {
        if (instance == null)
            instance = new LambdaService();
        return instance;
    }

    /**
     * Invokes a Lambda function using invocation type -> Event
     *
     * @param functionName - Name of the lambda function
     * @param payload      - Payload to be sent
     */
    public void fireEventFunction(String functionName, String payload) {
        val invokeRequest = new InvokeRequest().
                withFunctionName(functionName).
                withPayload(payload).
                withInvocationType(InvocationType.Event);
        awsLambda.invoke(invokeRequest);
    }
}
