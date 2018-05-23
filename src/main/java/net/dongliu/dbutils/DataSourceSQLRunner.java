package net.dongliu.dbutils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

/**
 * Wrap JDBC dataSource, to execute SQL queries.
 */
class DataSourceSQLRunner extends SQLRunner {

    /**
     * The DataSource to retrieve connections from.
     */
    private final DataSource dataSource;

    /**
     * Constructor to provide a DataSource.
     */
    DataSourceSQLRunner(DataSource dataSource) {
        this.dataSource = requireNonNull(dataSource);
    }

    @Override
    protected MyConnection supplyConnection() throws SQLException {
        Connection connection = this.dataSource.getConnection();
        return new MyConnection(connection, true);
    }
}
