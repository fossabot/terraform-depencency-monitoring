package com.github.dependencymonitoring.terraform.core.operations.parse.input;

import com.amazonaws.jmespath.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class that represents the abstraction layer for parsing AWS Lambda payloads into JSON objects
 *
 * @author <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 */
public class BasicLambdaInputParseOperation extends AbstractLambdaInputParseOperation<JsonNode> {

    /**
     * Default construction
     *
     * @param inputStream - input stream containing the AWS lambda payload
     */
    public BasicLambdaInputParseOperation(InputStream inputStream){
        super(inputStream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonNode performOperation() throws OperationException {
        try {
            return ObjectMapperSingleton.getObjectMapper().readTree(this.inputStream);
        } catch (IOException e) {
            throw wrapException(e);
        }
    }
}
