package net.dongliu.dbutils;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.util.Objects;

/**
 * @author Liu Dong
 */
public abstract class BatchSQLContext<T extends BatchSQLContext<T>> extends SQLContext<T> {
    @Nullable
    private Object[][] params;
    private static final Object[][] emptyParams = {};

    BatchSQLContext(Connection connection, boolean closeConn) {
        super(connection, closeConn);
    }

    /**
     * Set sql execute params.
     */
    protected T params(Object[]... params) {
        this.params = Objects.requireNonNull(params);
        return self();
    }

    protected Object[][] params() {
        return params == null ? emptyParams : params;
    }
}
