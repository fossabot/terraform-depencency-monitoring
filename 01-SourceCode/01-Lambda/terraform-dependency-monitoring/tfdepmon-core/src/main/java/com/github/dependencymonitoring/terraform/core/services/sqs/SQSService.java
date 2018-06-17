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

/**
 * Amazon Simple Queue Service base implementation
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
public class SQSService {

    /**
     * singleton instance variable
     */
    private static SQSService instance;
    /**
     * Amazon SQS client instance variable
     */
    private AmazonSQS sqs;
    /**
     * Local Queue Name x Url cache
     */
    private Map<String, String> queueNameMap;

    /**
     * Default constructor
     */
    private SQSService() {
        this.sqs = AmazonSQSClientBuilder.defaultClient();
        this.queueNameMap = new HashMap<>();
    }

    /**
     * Obtains a singleton instance of the service
     *
     * @return - SQSService instance
     */
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
