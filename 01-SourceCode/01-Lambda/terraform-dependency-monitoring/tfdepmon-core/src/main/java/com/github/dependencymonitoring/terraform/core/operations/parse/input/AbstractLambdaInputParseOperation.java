package com.github.dependencymonitoring.terraform.core.operations.parse.input;

import com.amazonaws.jmespath.ObjectMapperSingleton;
import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Class that represents the abstraction layer for parsing AWS Lambda payloads
 *
 * @param <ReturnType> - Return type that will be returned.
 * @author <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 */
abstract class AbstractLambdaInputParseOperation<ReturnType> {
    /**
     * Logger instance
     */
    protected Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    /**
     * Input stream instance
     */
    InputStream inputStream;

    /**
     * Default construction
     *
     * @param inputStream - input stream containing the AWS lambda payload
     */
    AbstractLambdaInputParseOperation(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Perform operation
     *
     * @param returnTypeClass - class that the JSON model will be parsed into
     * @return Generic type specified during compiling phase.
     * @throws OperationException - layer specific exception
     */
    public ReturnType performOperation(Class<ReturnType> returnTypeClass) throws OperationException {
        try {
            byte[] bytes = new byte[this.inputStream.available()];
            this.inputStream.read(bytes);
            this.inputStream.close();
            return ObjectMapperSingleton.getObjectMapper().readValue(new String(bytes), returnTypeClass);
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    /**
     * Perform operation
     *
     * @return Generic type specified during compiling phase.
     * @throws OperationException - layer specific exception
     */
    public ReturnType performOperation() throws OperationException {
        throw new RuntimeException("Not implemented yet");
    }

    /**
     * Utility method for wrapping an exception into a proper exception type for the Operation layer
     *
     * @param message - Description message explaining what have possibly happened during execution time.
     * @param e       - Root cause of the error.
     * @return - a OperationException instance.
     */
    protected OperationException wrapException(String message, Throwable e) {
        logger.severe(message);
        return new OperationException(message, e);
    }

    /**
     * Utility method for wrapping an exception into a proper exception type for the Operation layer
     *
     * @param e - Root cause of the error.
     * @return - a OperationException instance.
     */
    protected OperationException wrapException(Throwable e) {
        return this.wrapException("Error when performing operation", e);
    }
}
