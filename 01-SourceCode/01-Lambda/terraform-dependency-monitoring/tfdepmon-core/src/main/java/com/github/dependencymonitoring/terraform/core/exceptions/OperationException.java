package com.github.dependencymonitoring.terraform.core.exceptions;

import java.io.IOException;

/**
 * Class that represents the exception thrown during the operation
 * layer of the application
 *
 * @author  <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 */
public class OperationException extends IOException{

    /**
     * Contrustor using simple a string message
     *
     * @param message - description of what has happened
     */
    public OperationException(String message) {
        super(message);
    }

    /**
     * Constructor using not only the description message bute also the root cause
     * of the exception
     *
     * @param message - description of what has happened
     * @param cause - root cause of the exception
     */
    public OperationException(String message, Throwable cause) {
        super(message);
        this.initCause(cause);
    }

}
