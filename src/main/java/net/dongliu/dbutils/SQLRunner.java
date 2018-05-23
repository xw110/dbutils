package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.UncheckedSQLException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * Parent class for all which can execute sql.
 *
 * @author Liu Dong
 */
public abstract class SQLRunner extends SQLExecutor {

    /**
     * Create a sql runner from data source.
     */
    public static SQLRunner of(DataSource dataSource) {
        return new DataSourceSQLRunner(dataSource);
    }

    /**
     * Create a sql runner, with jdbc url, using internal non-pooled data source.
     */
    public static SQLRunner of(String jdbcUrl, String user, String password) {
        return new DataSourceSQLRunner(SimpleDataSource.create(jdbcUrl, user, password));
    }

    /**
     * Create a SQL Runner with one connection.
     * The connection retrieve and close are managed by user self.
     */
    public static SQLRunner of(Connection connection) {
        return new ConnectionSQLRunner(connection);
    }

    /**
     * Start a transaction, and return transaction context for executing sql.
     *
     * @return a transaction context to commit/rollback context
     */
    public TransactionContext startTransaction() {
        MyConnection connection;
        try {
            connection = supplyConnection();
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
        return new TransactionContext(connection);
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
    public static class TransactionContext extends SQLExecutor {

        // for backup/restore origin auto commit value
        private final boolean autoCommit;
        private final MyConnection connection;

        TransactionContext(MyConnection connection) {
            this.connection = connection;
            try {
                this.autoCommit = connection.getAutoCommit();
                this.connection.setAutoCommit(false);
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        }

        /**
         * Roll back transaction
         */
        public void rollback() {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            } finally {
                restoreAndRelease();
            }
        }

        /**
         * Commit transaction
         */
        public void commit() {
            try {
                connection.commit();
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            } finally {
                restoreAndRelease();
            }
        }

        /**
         * called when transaction ended
         */
        private void restoreAndRelease() {
            try (Connection c = connection) {
                c.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        }

        @Override
        protected MyConnection supplyConnection() {
            return new MyConnection(connection, false);
        }
    }
}
