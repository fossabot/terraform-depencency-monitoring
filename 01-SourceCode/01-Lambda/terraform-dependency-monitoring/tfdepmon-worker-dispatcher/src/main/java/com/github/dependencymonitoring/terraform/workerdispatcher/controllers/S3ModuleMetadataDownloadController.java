package com.github.dependencymonitoring.terraform.workerdispatcher.controllers;

import com.amazonaws.jmespath.ObjectMapperSingleton;
import com.amazonaws.services.sqs.model.Message;
import com.github.dependencymonitoring.terraform.workerdispatcher.parse.LambdaPayload;
import lombok.AllArgsConstructor;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.beans.TerraformSQSMessage;
import com.github.dependencymonitoring.terraform.core.controllers.TypedBaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;
import com.github.dependencymonitoring.terraform.core.services.s3.S3Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class S3ModuleMetadataDownloadController extends TypedBaseController<Map<String, byte[]>> {

    private final LambdaPayload payload;
    private Set<Message> messageSet;


    @Override
    public Map<String, byte[]> execute() throws ControllerException {
        try {
            val returnMap = new HashMap<String, byte[]>();
            val mapper = ObjectMapperSingleton.getObjectMapper();

            for (val message : messageSet) {
                val terraformSQSMessage = mapper.readValue(message.getBody(), TerraformSQSMessage.class);

                if (!returnMap.containsKey(terraformSQSMessage.getModulesKey())) {
                    val downloadedFile = S3Service.getInstance().downloadFile(payload.getS3BucketName(), terraformSQSMessage.getModulesKey());
                    returnMap.put(terraformSQSMessage.getModulesKey(), downloadedFile);
                }
            }
            return returnMap;
        } catch (IOException e) {
            throw this.wrapException("Error when downloading from S3", e);
        }
    }
}
