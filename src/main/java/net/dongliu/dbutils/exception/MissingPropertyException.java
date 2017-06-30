package net.dongliu.dbutils.exception;

/**
 * Thrown when bean do not have property to map with resultSet column
 */
public class MissingPropertyException extends UncheckedSQLException {
    private static final long serialVersionUID = 6710560309701838985L;

    public MissingPropertyException(String typeName, String columnName) {
        super(typeName + " missing property for column: " + columnName);
    }
}
