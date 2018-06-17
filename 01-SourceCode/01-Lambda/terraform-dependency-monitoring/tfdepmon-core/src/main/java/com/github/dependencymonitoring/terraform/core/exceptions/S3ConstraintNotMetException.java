package com.github.dependencymonitoring.terraform.core.exceptions;

/**
 * Class that represents the exception thrown when the logical lock on S3 ins't met.
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 *
 */
public class S3ConstraintNotMetException extends Exception {

    /**
     * Default constructor
     */
    public S3ConstraintNotMetException() {
        super("Constraint specified hasn't been met");
    }
}
