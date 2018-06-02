package com.github.dependencymonitoring.terraform.workerdispatcher.controllers;

import com.amazonaws.services.sqs.model.Message;
import com.github.dependencymonitoring.terraform.workerdispatcher.parse.LambdaPayload;
import lombok.RequiredArgsConstructor;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.controllers.TypedBaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;
import com.github.dependencymonitoring.terraform.core.services.sqs.SQSService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
class SQSReceivedMessagesController extends TypedBaseController<Set<Message>> {

    private final LambdaPayload payload;


    @Override
    public Set<Message> execute() throws ControllerException {
        val maxMsg = payload.getWorkerReservedConcurrentExecutions();
        val qName = payload.getSqsQueueName();

        val messageSet = new HashSet<Message>(maxMsg);
        List<Message> messageList;
        while (!(messageList = SQSService.getInstance().receiveMessages(qName)).isEmpty()) {
            messageSet.addAll(messageList);
            if (messageSet.size() >= maxMsg) break;
        }

        return messageSet;
    }
}
