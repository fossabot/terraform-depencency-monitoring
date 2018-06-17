package com.github.dependencymonitoring.terraform.gatherterraformmodules.controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.controllers.TypedBaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;
import com.github.dependencymonitoring.terraform.core.operations.parse.beans.LambdaPayload;

/**
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class FlowDispatcherController extends TypedBaseController<ArrayNode> {

    private LambdaPayload payload;

    @Override
    public ArrayNode execute() throws ControllerException {

        // Terraform modules available
        val terraformModuleInfoController = new TerraformModuleInfoController(
                payload.getInclusions().toArray(new String[]{}),
                payload.getExclusions().toArray(new String[]{})
        );
        val terraformModulesDefinitionsMap = terraformModuleInfoController.execute();

        // Writing output JSON
        val outputGenerationController = new JsonOutputGenerationController(terraformModulesDefinitionsMap);
        return outputGenerationController.execute();

    }

}
