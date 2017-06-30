package net.dongliu.dbutils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;

/**
 * @author Liu Dong
 */
public abstract class SingleSQLContext<T extends SingleSQLContext<T>> extends SQLContext<T> {
    @Nullable
    private Object[] params;
    private static final Object[] emptyParams = {};

    SingleSQLContext(Connection connection, boolean closeConn) {
        super(connection, closeConn);
    }

    /**
     * Set sql execute params.
     */
    protected T params(Object... params) {
        this.params = Objects.requireNonNull(params);
        return self();
    }

    /**
     * Set sql execute params.
     */
    protected T params(List<?> params) {
        this.params = Objects.requireNonNull(params).toArray();
        return self();
    }

    @Nonnull
    protected Object[] params() {
        return params == null ? emptyParams : params;
    }
}
