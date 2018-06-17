package com.github.dependencymonitoring.terraform.core.operations.parse.input;

import java.io.InputStream;

/**
 * Class that represents the abstraction layer for parsing AWS Lambda payloads into a generic type
 *
 * @param <T> - Return type that will be returned.
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
public class TypedLambdaInputParseOperation<T> extends AbstractLambdaInputParseOperation<T> {
    /**
     * Default construction
     *
     * @param inputStream - input stream containing the AWS lambda payload
     */
    public TypedLambdaInputParseOperation(InputStream inputStream) {
        super(inputStream);
    }
}
