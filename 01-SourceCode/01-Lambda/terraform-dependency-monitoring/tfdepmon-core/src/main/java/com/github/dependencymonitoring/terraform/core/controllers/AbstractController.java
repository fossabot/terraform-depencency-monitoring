package com.github.dependencymonitoring.terraform.core.controllers;

import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;

import java.util.logging.Logger;

/**
 * Base class for all controllers in the application.
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
abstract class AbstractController {
    /**
     * Logger variable
     */
    protected Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    /**
     * Utility method for wrapping an exception into a proper exception type for the Controller layer
     * @param message - Description message explaining what have possibly happened during execution time.
     * @param e - Root cause of the error.
     * @return - a ControllerException instance.
     */
    protected ControllerException wrapException(String message, Throwable e){
        logger.severe(message);
        return new ControllerException(message, e);
    }
}
