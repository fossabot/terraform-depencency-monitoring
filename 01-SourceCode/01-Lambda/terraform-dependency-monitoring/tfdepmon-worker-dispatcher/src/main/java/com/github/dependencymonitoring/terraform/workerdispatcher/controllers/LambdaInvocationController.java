package com.github.dependencymonitoring.terraform.workerdispatcher.controllers;

import com.amazonaws.jmespath.ObjectMapperSingleton;
import com.amazonaws.services.sqs.model.Message;
import com.github.dependencymonitoring.terraform.workerdispatcher.parse.LambdaPayload;
import lombok.AllArgsConstructor;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.beans.TerraformLambdaPayload;
import com.github.dependencymonitoring.terraform.core.beans.TerraformModuleDefinition;
import com.github.dependencymonitoring.terraform.core.beans.TerraformSQSMessage;
import com.github.dependencymonitoring.terraform.core.controllers.BaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;
import com.github.dependencymonitoring.terraform.core.services.lambda.LambdaService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
class LambdaInvocationController extends BaseController {

    private LambdaPayload inputLambdaPayload;
    private Set<Message> receivedMessages;
    private Map<String, byte[]> moduleMetadataMap;


    @Override
    public void execute() throws ControllerException {
        try {
            val mapper = ObjectMapperSingleton.getObjectMapper();
            val terraformModuleDefinitionType = mapper.getTypeFactory().constructCollectionType(List.class, TerraformModuleDefinition.class);
            for (val message : receivedMessages) {
                val terraformSQSMessage = mapper.readValue(message.getBody(), TerraformSQSMessage.class);
                List<TerraformModuleDefinition> moduleDefinitions =
                        mapper.readValue(moduleMetadataMap.get(terraformSQSMessage.getModulesKey()), terraformModuleDefinitionType);

                // create lambda payload
                val lambdaPayload = new TerraformLambdaPayload();
                lambdaPayload.setTerraformModuleDefinitions(moduleDefinitions);
                lambdaPayload.setTerraformProjectRepository(terraformSQSMessage.getTerraformProjectRepository());
                lambdaPayload.setSqsQueueUrl(inputLambdaPayload.getSqsQueueName());
                lambdaPayload.setSqsReceiptHandle(message.getReceiptHandle());

                LambdaService.getInstance().fireEventFunction(
                        inputLambdaPayload.getLambdaWorkerFunctionName(),
                        mapper.writeValueAsString(lambdaPayload)
                );


            }
        } catch (IOException e) {
            throw this.wrapException("Error when invokating a lambda function", e);
        }

    }
}
