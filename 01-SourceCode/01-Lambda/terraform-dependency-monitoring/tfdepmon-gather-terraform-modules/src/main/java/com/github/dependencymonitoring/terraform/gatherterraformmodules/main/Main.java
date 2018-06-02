package com.github.dependencymonitoring.terraform.gatherterraformmodules.main;

import com.amazonaws.jmespath.ObjectMapperSingleton;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.github.dependencymonitoring.terraform.gatherterraformmodules.controllers.FlowDispatcherController;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.operations.parse.beans.LambdaPayload;
import com.github.dependencymonitoring.terraform.core.operations.parse.input.TypedLambdaInputParseOperation;
import com.github.dependencymonitoring.terraform.core.services.s3.S3Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 */
public class Main implements RequestStreamHandler {


    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        // Reading input
        val lambdaInputParseOperation = new TypedLambdaInputParseOperation<LambdaPayload>(input);
        val lambdaPayload = lambdaInputParseOperation.performOperation(LambdaPayload.class);
        System.out.println("lambdaPayload: " + lambdaPayload.toString());

        // Dispatch the flow
        val outputJson = new FlowDispatcherController(lambdaPayload).execute();
        val outputStr = ObjectMapperSingleton.getObjectMapper().writeValueAsString(outputJson);
        System.out.println("outputJson: " + outputStr);

        S3Service.getInstance().uploadFile(lambdaPayload.getBucketName(), lambdaPayload.getKey(), outputStr.getBytes());

        output.write("Ok".getBytes());
        output.flush();
    }

    public static void main(String args[]) throws IOException {
        val h = new Main();
        h.handleRequest(h.getClass().getResourceAsStream("/default_input.json"), System.out, null);
    }

}
