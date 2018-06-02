package com.github.dependencymonitoring.terraform.core.services.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQSService {

    private static SQSService instance;
    private AmazonSQS sqs;
    private Map<String, String> queueNameMap;

    private SQSService() {
        this.sqs = AmazonSQSClientBuilder.defaultClient();
        this.queueNameMap = new HashMap<>();
    }

    public static SQSService getInstance() {
        if (instance == null)
            instance = new SQSService();
        return instance;
    }

    private void buildQueueMap(String queueName) {
        // To ensure that we obtain the queue url if we really need to, otherwise, cached version should be used.
        if (!this.queueNameMap.containsKey(queueName)) {
            val queueUrlResult = sqs.getQueueUrl(queueName);
            this.queueNameMap.put(queueName, queueUrlResult.getQueueUrl());
        }
    }

    public void sendMessage(String queueName, String body) {
        buildQueueMap(queueName);
        val sendMessageRequest = new SendMessageRequest(this.queueNameMap.get(queueName), body);
        sqs.sendMessage(sendMessageRequest);
    }

    public List<Message> receiveMessages(String queueName, int maxNumberOfMessages) {
        buildQueueMap(queueName);

        val receiveMessageRequest = new ReceiveMessageRequest(this.queueNameMap.get(queueName));
        receiveMessageRequest.setMaxNumberOfMessages(maxNumberOfMessages);

        val receiveMessageResult = sqs.receiveMessage(receiveMessageRequest);
        return receiveMessageResult.getMessages();
    }

    public List<Message> receiveMessages(String queueName) {
        return this.receiveMessages(queueName, 10);
    }

    public void deleteMessage(String queueUrl, String receiptHandle) {
        sqs.deleteMessage(queueUrl, receiptHandle);
    }
}
