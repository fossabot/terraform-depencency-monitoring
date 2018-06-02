package com.github.dependencymonitoring.terraform.gathergithubrepos.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import com.github.dependencymonitoring.terraform.core.controllers.TypedBaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;
import org.kohsuke.github.GHRepository;

import java.util.List;

/**
 *
 * @author  <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 *
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class JsonOutputGenerationController extends TypedBaseController<ArrayNode> {

    private List<GHRepository> repositories;

    @Override
    public ArrayNode execute() throws ControllerException {
        val objectMapper = new ObjectMapper();

        val arrayNode = objectMapper.createArrayNode();

        repositories.forEach((value) -> {
            val objectNode = objectMapper.createObjectNode();

            objectNode.put("name", value.getName());
            objectNode.put("htmlUrl", value.getHtmlUrl().toExternalForm());
            arrayNode.add(objectNode);

        });
        return arrayNode;
    }

}
