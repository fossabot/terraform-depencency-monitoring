package com.github.dependencymonitoring.terraform.workerdispatcher.controllers;

import com.github.dependencymonitoring.terraform.workerdispatcher.parse.LambdaPayload;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.controllers.BaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;

/**
 * @author <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 */
public class FlowDispatcherController extends BaseController {

    private final LambdaPayload payload;

    public FlowDispatcherController(LambdaPayload payload) {
        this.payload = payload;
    }

    @Override
    public void execute() throws ControllerException {

        val sqsReceivedMessagesController = new SQSReceivedMessagesController(payload);
        val receivedMessages = sqsReceivedMessagesController.execute();

        logger.info("Number of messages received: " + receivedMessages.size());
        receivedMessages.stream().forEach((it) -> logger.info("Message received: " + it.toString()));

        if (!receivedMessages.isEmpty()) {
            val map = new S3ModuleMetadataDownloadController(payload, receivedMessages).execute();
            new LambdaInvocationController(payload, receivedMessages, map).execute();
        }
    }

}
