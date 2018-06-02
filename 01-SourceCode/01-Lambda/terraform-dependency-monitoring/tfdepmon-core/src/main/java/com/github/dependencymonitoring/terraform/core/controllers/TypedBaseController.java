package com.github.dependencymonitoring.terraform.core.controllers;

import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;

/**
 * Base class for controllers with a generic return type.
 * @param <T> - Return type that will be returned.
 *
 * @author  <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 */
public abstract class TypedBaseController<T> extends AbstractController{
    /**
     * Execute operation
     *
     * @return Generic type specified during compiling phase.
     * @throws ControllerException - layer specific exception
     */
    public abstract T execute() throws ControllerException;
}
