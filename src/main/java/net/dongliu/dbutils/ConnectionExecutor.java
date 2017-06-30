package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.UncheckedSQLException;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Sql Executor wrap a connection
 */
public class ConnectionExecutor extends SQLExecutor implements Closeable {
    private final Connection connection;

    private ConnectionExecutor(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    /**
     * Create a SQL Executor with one connection.
     * The connection retrieve and close are managed by user self.
     */
    public static ConnectionExecutor create(Connection connection) {
        return new ConnectionExecutor(connection);
    }

    @Override
    protected ConnectionInfo supplyConnection() {
        return new ConnectionInfo(connection, false);
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }
}
