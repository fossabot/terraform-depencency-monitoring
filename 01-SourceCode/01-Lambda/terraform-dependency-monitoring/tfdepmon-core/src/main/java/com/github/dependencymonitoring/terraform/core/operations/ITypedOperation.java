package com.github.dependencymonitoring.terraform.core.operations;

import com.github.dependencymonitoring.terraform.core.exceptions.OperationException;

/**
 * Base class for controllers with a generic return type.
 * @param <T> - Return type that will be returned.
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 *
 */
public interface ITypedOperation<T> {
    /**
     * Execute operation
     * @return Generic type specified during compiling phase.
     * @throws OperationException - layer specific exception
     */
    T performOperation() throws OperationException;
}
