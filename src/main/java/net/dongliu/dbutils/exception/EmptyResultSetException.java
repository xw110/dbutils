package net.dongliu.dbutils.exception;

import java.sql.SQLException;

/**
 * @author Liu Dong
 */
public class EmptyResultSetException extends UncheckedSQLException {
    public EmptyResultSetException() {
    }

    public EmptyResultSetException(String message) {
        super(message);
    }

    public EmptyResultSetException(String message, SQLException cause) {
        super(message, cause);
    }
}
