package com.github.dependencymonitoring.terraform.gathergithubrepos.controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.controllers.TypedBaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;
import com.github.dependencymonitoring.terraform.core.operations.parse.beans.LambdaPayload;

/**
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
public class FlowDispatcherController extends TypedBaseController<ArrayNode> {

    private LambdaPayload payload;

    public FlowDispatcherController(LambdaPayload payload) {
        this.payload = payload;
    }

    @Override
    public ArrayNode execute() throws ControllerException {

        // Github Repositories
        val terraformRepoDiscoverController = new TerraformRepoDiscoverController(
                payload.getInclusions().toArray(new String[]{}),
                payload.getExclusions().toArray(new String[]{})
        );
        val repositories = terraformRepoDiscoverController.execute();

        // Writing output JSON
        val outputGenerationController = new JsonOutputGenerationController(repositories);
        return outputGenerationController.execute();
    }

}
