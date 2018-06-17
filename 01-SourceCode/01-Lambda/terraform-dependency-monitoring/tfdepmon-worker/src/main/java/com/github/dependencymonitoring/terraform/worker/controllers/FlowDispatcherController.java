package com.github.dependencymonitoring.terraform.worker.controllers;

import lombok.val;
import com.github.dependencymonitoring.terraform.core.beans.TerraformLambdaPayload;
import com.github.dependencymonitoring.terraform.core.controllers.BaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;
import com.github.dependencymonitoring.terraform.core.services.sqs.SQSService;

/**
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
public class FlowDispatcherController extends BaseController {

    private final TerraformLambdaPayload lambdaPayload;

    public FlowDispatcherController(TerraformLambdaPayload lambdaPayload) {
        this.lambdaPayload = lambdaPayload;
    }

    @Override
    public void execute() throws ControllerException {
        // Analyse repo
        val modulesUpgradeMap = new TerraformRepoDiscoverController(lambdaPayload).execute();

        // validate previous PRs and/or create new ones
        val gitHubPullRequestOpenerController = new GitHubPullRequestOpenerController(lambdaPayload, modulesUpgradeMap);
        gitHubPullRequestOpenerController.execute();

        // delete message from SQS queue
        SQSService.getInstance().deleteMessage(lambdaPayload.getSqsQueueUrl(), lambdaPayload.getSqsReceiptHandle());
    }

}
