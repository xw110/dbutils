package net.dongliu.dbutils.exception;

import java.sql.SQLException;

/**
 * RuntimeException, Paranet class of all exceptions thrown when dbutils executing.
 * Also, this can be used to wrap checked SQLException.
 *
 * @author Liu Dong
 */
public class UncheckedSQLException extends RuntimeException {
    private static final long serialVersionUID = 7068173044204809643L;

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

    public UncheckedSQLException(Exception cause) {
        super(cause);
    }
}
