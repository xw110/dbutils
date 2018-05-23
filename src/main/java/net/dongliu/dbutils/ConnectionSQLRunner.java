package net.dongliu.dbutils;

import java.sql.Connection;
import java.util.Objects;

/**
 * Sql Runner wrap a connection
 */
class ConnectionSQLRunner extends SQLRunner {
    private final Connection connection;

    ConnectionSQLRunner(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    protected MyConnection supplyConnection() {
        return new MyConnection(connection, false);
    }

}
