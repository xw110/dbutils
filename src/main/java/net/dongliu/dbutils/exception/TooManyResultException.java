package net.dongliu.dbutils.exception;

/**
 * Thrown when require single result but get more rows
 *
 * @author Liu Dong
 */
public class TooManyResultException extends UncheckedSQLException {
    public TooManyResultException() {
    }

    public TooManyResultException(String msg) {
        super(msg);
    }
}
