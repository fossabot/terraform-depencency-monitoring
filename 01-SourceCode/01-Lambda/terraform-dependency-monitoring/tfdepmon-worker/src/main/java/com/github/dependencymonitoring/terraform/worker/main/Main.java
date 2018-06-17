package com.github.dependencymonitoring.terraform.worker.main;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.github.dependencymonitoring.terraform.worker.controllers.FlowDispatcherController;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.beans.TerraformLambdaPayload;
import com.github.dependencymonitoring.terraform.core.operations.parse.input.TypedLambdaInputParseOperation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
public class Main implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        // Reading input
        val lambdaInputParseOperation = new TypedLambdaInputParseOperation<TerraformLambdaPayload>(input);
        val lambdaPayload = lambdaInputParseOperation.performOperation(TerraformLambdaPayload.class);
        System.out.println("lambdaPayload: " + lambdaPayload.toString());

        // Dispatch the flow
        new FlowDispatcherController(lambdaPayload).execute();

        output.write("Ok".getBytes());
        output.flush();
    }

    public static void main(String args[]) throws IOException {
        val h = new Main();
        h.handleRequest(h.getClass().getResourceAsStream("/default_input.json"), System.out, null);
    }

}
