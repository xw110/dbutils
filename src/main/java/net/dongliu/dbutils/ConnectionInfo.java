package net.dongliu.dbutils;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Wrap connection
 */
class ConnectionInfo implements Closeable {
    private final boolean autoClose;
    private final Connection connection;

    public ConnectionInfo(Connection connection, boolean autoClose) {
        this.autoClose = autoClose;
        this.connection = connection;
    }

    public boolean autoClose() {
        return autoClose;
    }

    public Connection connection() {
        return connection;
    }

    /**
     * Close inner connection
     */
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
//            throw new UncheckedSQLException(e);
        }
    }

    @Override
    public void close() {
        if (autoClose) {
            closeConnection();
        }
    }
}
