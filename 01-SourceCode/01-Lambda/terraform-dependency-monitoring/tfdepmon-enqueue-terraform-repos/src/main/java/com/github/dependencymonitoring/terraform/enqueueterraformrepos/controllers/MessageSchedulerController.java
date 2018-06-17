package com.github.dependencymonitoring.terraform.enqueueterraformrepos.controllers;

import com.amazonaws.jmespath.ObjectMapperSingleton;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.beans.TerraformProjectRepository;
import com.github.dependencymonitoring.terraform.core.beans.TerraformSQSMessage;
import com.github.dependencymonitoring.terraform.core.controllers.BaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;
import com.github.dependencymonitoring.terraform.core.exceptions.S3ConstraintNotMetException;
import com.github.dependencymonitoring.terraform.core.services.s3.S3Service;
import com.github.dependencymonitoring.terraform.core.services.sqs.SQSService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import static com.github.dependencymonitoring.terraform.core.utils.ICoreConstants.LOGICAL_LOCK_S3_INTERVAL_IN_MIN;

/**
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
class MessageSchedulerController extends BaseController {

    private String bucketName;
    private String moduleKey;
    private String projectReposKey;
    private String sqsQueueName;

    MessageSchedulerController() {
        this.bucketName = System.getenv("bucket_name");
        this.moduleKey = System.getenv("modules_key");
        this.projectReposKey = System.getenv("project_repos_key");
        this.sqsQueueName = System.getenv("sqs_queue_name");
    }


    @Override
    public void execute() throws ControllerException {
        val localDateTime = LocalDateTime.now().minusMinutes(LOGICAL_LOCK_S3_INTERVAL_IN_MIN);
        val date = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
        val s3Service = S3Service.getInstance();

        try {
            s3Service.downloadFile(bucketName, moduleKey, date);
            val terraformProjectRepos = s3Service.downloadFile(bucketName, projectReposKey, date);

            val mapper = ObjectMapperSingleton.getObjectMapper();
            List<TerraformProjectRepository> projectRepositories = mapper.readValue(
                    terraformProjectRepos, new TypeReference<List<TerraformProjectRepository>>() {
                    });

            for (val projectRepository : projectRepositories) {
                val terraformSQSMessage = new TerraformSQSMessage(projectRepository, moduleKey);
                SQSService.getInstance().sendMessage(this.sqsQueueName, mapper.writeValueAsString(terraformSQSMessage));
            }

        } catch (S3ConstraintNotMetException e) {
            String msg = String.format(
                    "skipping further process as apparently both %s and %s don't meet " +
                            "the %d-minutes interval previously established",
                    this.moduleKey, this.projectReposKey, LOGICAL_LOCK_S3_INTERVAL_IN_MIN);
            throw wrapException(msg, e);
        } catch (AmazonS3Exception e) {
            String msg = String.format(
                    "Either %s and %s wasn't found. This is expected in the first execution but not afterwards",
                    this.moduleKey, this.projectReposKey);
            throw wrapException(msg, e);
        } catch (IOException e) {
            String msg = String.format(
                    "Some other error has happened when processing moduleKey: %s and projectReposKey: %s",
                    this.moduleKey, this.projectReposKey);
            throw wrapException(msg, e);
        }
    }
}
