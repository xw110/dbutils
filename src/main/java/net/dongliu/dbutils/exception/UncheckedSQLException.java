package net.dongliu.dbutils.exception;

import java.sql.SQLException;

/**
 * @author Liu Dong
 */
public class UncheckedSQLException extends RuntimeException {
    public UncheckedSQLException() {
    }

    public UncheckedSQLException(String message) {
        super(message);
    }

    public UncheckedSQLException(String message, SQLException cause) {
        super(message, cause);
    }

    public UncheckedSQLException(SQLException cause) {
        super(cause);
    }
}
