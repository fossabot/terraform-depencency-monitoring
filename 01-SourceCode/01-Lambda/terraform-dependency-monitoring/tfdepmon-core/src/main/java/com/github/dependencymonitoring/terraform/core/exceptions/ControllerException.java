package com.github.dependencymonitoring.terraform.core.exceptions;

import java.io.IOException;

/**
 * Class that represents the exception thrown during the controller
 * layer of the application
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
public class ControllerException extends IOException {

    /**
     * Contrustor using simple a string message
     *
     * @param message - description of what has happened
     */
    public ControllerException(String message) {
        super(message);
    }

    /**
     * Constructor using not only the description message bute also the root cause
     * of the exception
     *
     * @param message - description of what has happened
     * @param cause - root cause of the exception
     */
    public ControllerException(String message, Throwable cause) {
        super(message);
        this.initCause(cause);
    }
}
