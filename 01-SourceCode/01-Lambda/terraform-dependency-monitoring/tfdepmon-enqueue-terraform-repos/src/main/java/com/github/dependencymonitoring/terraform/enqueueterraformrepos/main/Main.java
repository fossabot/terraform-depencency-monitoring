package com.github.dependencymonitoring.terraform.enqueueterraformrepos.main;

import com.amazonaws.jmespath.ObjectMapperSingleton;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.operations.parse.input.BasicLambdaInputParseOperation;
import com.github.dependencymonitoring.terraform.enqueueterraformrepos.controllers.FlowDispatcherController;

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
        val lambdaInputParseOperation = new BasicLambdaInputParseOperation(input);
        val lambdaPayload = lambdaInputParseOperation.performOperation();
        System.out.println("lambdaPayload: " + ObjectMapperSingleton.getObjectMapper().writeValueAsString(lambdaPayload));

        // Dispatch the flow
        new FlowDispatcherController().execute();

        output.write("Ok".getBytes());
        output.flush();
    }

    public static void main(String args[]) throws IOException {
        val h = new Main();
        h.handleRequest(h.getClass().getResourceAsStream("/default_input.json"), System.out, null);
    }

}
