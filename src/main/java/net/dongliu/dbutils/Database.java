package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.UncheckedSQLException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * Parent class for which can execute sql. As the name DataSource is already taken, we use Database as name.
 *
 * @author Liu Dong
 */
public abstract class Database extends SQLExecutor {

    /**
     * Create a sql runner from data source.
     */
    public static Database of(DataSource dataSource) {
        return new DataSourceWrapper(dataSource);
    }

    /**
     * Create a sql runner, with jdbc url, using internal non-pooled data source.
     */
    public static Database of(String jdbcUrl, String user, String password) {
        return new DataSourceWrapper(SimpleDataSource.create(jdbcUrl, user, password));
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
