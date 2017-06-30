package net.dongliu.dbutils.exception;

/**
 * Thrown when require single result but get more rows
 *
 * @author Liu Dong
 */
public class TooManyResultException extends UncheckedSQLException {
    private static final long serialVersionUID = 1992227080098912450L;

    public TooManyResultException(int count) {
        super("Too many row in ResultSet, expect: " + count);
    }
}
