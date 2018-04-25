package net.dongliu.dbutils.exception;

import java.beans.IntrospectionException;

/**
 * Used to wrap reflect operation exceptions, or other cases, when do database - bean mapping.
 */
public class BeanMappingException extends UncheckedSQLException {
    public BeanMappingException(ReflectiveOperationException cause) {
        super(cause);
    }

    public BeanMappingException(String message) {
        super(message);
    }

    public BeanMappingException(IntrospectionException cause) {
        super(cause);
    }
}
