package net.dongliu.dbutils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.function.Function;

/**
 * SQL Runner execute sql with named parameters.
 *
 * @author Liu Dong
 */
public class NamedSQLRunner extends NamedSQLExecutor<SQLRunner> {
    protected NamedSQLRunner(SQLRunner sqlRunner) {
        super(sqlRunner);
    }

    /**
     * Create a sql runner from data source.
     */
    public static NamedSQLRunner of(DataSource dataSource) {
        return new NamedSQLRunner(new DataSourceSQLRunner(dataSource));
    }

    /**
     * Create a sql runner, with jdbc url, using internal non-pooled data source.
     */
    public static NamedSQLRunner of(String jdbcUrl, String user, String password) {
        return new NamedSQLRunner(new DataSourceSQLRunner(SimpleDataSource.create(jdbcUrl, user, password)));
    }

    /**
     * Create a SQL Runner with one connection.
     * The connection retrieve and close are managed by user self.
     */
    public static NamedSQLRunner of(Connection connection) {
        return new NamedSQLRunner(new ConnectionSQLRunner(connection));
    }

    /**
     * Start a transaction, and return transaction context for executing sql.
     *
     * @return a transaction context to commit/rollback context
     */
    public TransactionContext startTransaction() {
        return new TransactionContext(delegated.startTransaction());
    }

    /**
     * Start a transaction, execute sql in function, and do commit after function finished,
     * or rollback if exception occurred
     */
    public <T> T withTransaction(Function<TransactionContext, T> function) {
        TransactionContext ctx = startTransaction();
        try {
            T result = function.apply(ctx);
            ctx.commit();
            return result;
        } catch (Throwable t) {
            ctx.rollback();
            throw t;
        }
    }

    /**
     * For holding transaction
     *
     * @author Liu Dong
     */
    public static class TransactionContext extends NamedSQLExecutor<SQLRunner.TransactionContext> {
        protected TransactionContext(SQLRunner.TransactionContext delegated) {
            super(delegated);
        }

        /**
         * Roll back transaction
         */
        public void rollback() {
            delegated.rollback();
        }

        /**
         * Commit transaction
         */
        public void commit() {
            delegated.commit();
        }
    }
}
