package com.github.dependencymonitoring.terraform.core.operations;

import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;

/**
 * Base class for operations with no return type
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 *
 */
public interface IBaseOperation{
    /**
     * Execute operation
     * @throws OperationException -  - layer specific exception
     */
    void performOperation() throws OperationException;
}
