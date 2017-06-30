package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.UncheckedSQLException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * For holding transaction
 *
 * @author Liu Dong
 */
public class TransactionContext extends SQLExecutor {

    // for backup/restore origin auto commit value
    private final boolean autoCommit;
    private final Connection connection;

    TransactionContext(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
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
            restoreConnection();
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
            restoreConnection();
        }
    }

    /**
     * called when transaction ended
     */
    private void restoreConnection() {
        try (Connection c = connection) {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    @Override
    protected ConnectionInfo supplyConnection() {
        return new ConnectionInfo(connection, false);
    }
}
