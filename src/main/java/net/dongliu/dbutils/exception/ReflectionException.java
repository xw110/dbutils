package net.dongliu.dbutils.exception;

import java.beans.IntrospectionException;

/**
 * Used to wrap reflect operation exceptions
 */
public class ReflectionException extends RuntimeException {
    public ReflectionException(ReflectiveOperationException cause) {
        super(cause);
    }

    public ReflectionException(IntrospectionException cause) {
        super(cause);
    }
}
