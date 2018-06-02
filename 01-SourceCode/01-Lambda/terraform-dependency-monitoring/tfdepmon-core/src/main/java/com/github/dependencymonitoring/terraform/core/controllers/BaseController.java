package com.github.dependencymonitoring.terraform.core.controllers;

import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;

/**
 * Base class for controllers with no return type
 *
 * @author  <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 */
public abstract class BaseController extends AbstractController{
    /**
     * Execute operation
     * @throws ControllerException - layer specific exception
     */
    public abstract void execute() throws ControllerException;
}
