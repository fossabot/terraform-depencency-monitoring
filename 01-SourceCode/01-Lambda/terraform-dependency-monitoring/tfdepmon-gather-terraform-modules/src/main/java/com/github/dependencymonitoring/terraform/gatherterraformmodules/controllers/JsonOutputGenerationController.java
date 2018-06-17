package com.github.dependencymonitoring.terraform.gatherterraformmodules.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.beans.TerraformModuleDefinition;
import com.github.dependencymonitoring.terraform.core.controllers.TypedBaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;
import com.github.dependencymonitoring.terraform.core.operations.parse.semver.VersionParseOperation;

import java.util.Map;

/**
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class JsonOutputGenerationController extends TypedBaseController<ArrayNode> {

    private Map<String, TerraformModuleDefinition> terraformModulesDefinitionsMap;

    @Override
    public ArrayNode execute() throws ControllerException {
        val objectMapper = new ObjectMapper();

        val arrayNode = objectMapper.createArrayNode();

        terraformModulesDefinitionsMap.forEach((key, value) -> {

            val objectNode = objectMapper.createObjectNode();

            objectNode.put("name", value.getName());
            objectNode.put("latestVersion", VersionParseOperation.VersionFormatter.format(value.getLatestVersion()));


            val versionsArrayNode = objectMapper.createArrayNode();
            value.getVersions().forEach(version -> versionsArrayNode.add(VersionParseOperation.VersionFormatter.format(version)));
            objectNode.put("versions", versionsArrayNode);

            arrayNode.add(objectNode);

        });
        return arrayNode;
    }

}
