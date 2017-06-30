package net.dongliu.dbutils;


import net.dongliu.dbutils.exception.UncheckedSQLException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Basic data source implementation
 *
 * @author Liu Dong
 */
class SimpleDataSource implements DataSource {

    private final String jdbcUrl;
    private final String user;
    private final String password;
    private final Driver driver;
    private final boolean autoCommit;

    private SimpleDataSource(String jdbcUrl, String user, String password, Driver driver, boolean autoCommit) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        this.driver = driver;
        this.autoCommit = autoCommit;
    }

    private volatile PrintWriter writer;


    /**
     * Create data source
     */
    public static DataSource create(String jdbcUrl, String user, String password) {
        Driver driver;
        try {
            driver = DriverManager.getDriver(jdbcUrl);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
        return new SimpleDataSource(jdbcUrl, user, password, driver, true);
    }

    /**
     * Create data source, with auto commit option
     */
    public static DataSource create(String jdbcUrl, String user, String password, boolean autoCommit) {
        Driver driver;
        try {
            driver = DriverManager.getDriver(jdbcUrl);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
        return new SimpleDataSource(jdbcUrl, user, password, driver, autoCommit);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(user, password);
    }

    @Override
    public Connection getConnection(String user, String password) throws SQLException {
        Properties info = new Properties();
        if (user != null) {
            info.put("user", user);
        }
        if (password != null) {
            info.put("password", password);
        }
        Connection connection = driver.connect(jdbcUrl, info);
        connection.setAutoCommit(autoCommit);
        return connection;
    }

    @Override
    public PrintWriter getLogWriter() {
        return this.writer;
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        this.writer = out;
    }

    @Override
    public void setLoginTimeout(int seconds) {

    }

    @Override
    public int getLoginTimeout() {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

}