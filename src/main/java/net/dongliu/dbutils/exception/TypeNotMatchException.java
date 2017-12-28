package net.dongliu.dbutils.exception;

/**
 * Thrown when can not convert value to desired type.
 */
public class TypeNotMatchException extends UncheckedSQLException {
    private static final long serialVersionUID = 8498808571328676086L;

    public TypeNotMatchException(Class<?> cls) {
        super("sql value cannot convert to type: " + cls.getName());
    }
}
