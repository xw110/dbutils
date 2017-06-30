package net.dongliu.dbutils.exception;

/**
 * Thrown when column with name not exist
 *
 * @author Liu Dong
 */
public class ColumnNotFoundException extends UncheckedSQLException {
    private static final long serialVersionUID = -8326564172262303144L;

    public ColumnNotFoundException(String name) {
        super("Column with name " + name + " not exit");
    }
}
