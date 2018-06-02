package com.github.dependencymonitoring.terraform.core.exceptions;

/**
 * Class that represents the exception thrown when a invalid version is found on /VERSION file.
 *
 * @author  <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 *
 */
public class InvalidVersionException extends OperationException {

    /**
     * Contrustor using the found version value
     *
     * @param version - version obtained in runtime
     */
    public InvalidVersionException(String version) {
        super("Invalid version: " + version);
    }
}
