package net.dongliu.dbutils;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Wrap connection, for manager close.
 */
class MyConnection implements Connection {
    private final boolean autoClose;
    private final Connection delegated;
    private boolean closed;

    public MyConnection(Connection connection, boolean autoClose) {
        this.autoClose = autoClose;
        this.delegated = connection;
    }

    @Override
    public Statement createStatement() throws SQLException {
        checkClosed();
        return delegated.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkClosed();
        return delegated.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        checkClosed();
        return delegated.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        checkClosed();
        return delegated.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkClosed();
        delegated.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        checkClosed();
        return delegated.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        checkClosed();
        delegated.commit();
    }

    @Override
    public void rollback() throws SQLException {
        checkClosed();
        delegated.rollback();
    }

    @Override
    public void close() throws SQLException {
        if (autoClose && !closed) {
            delegated.close();
            closed = true;
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    private void checkClosed() throws SQLException {
        if (closed) {
            throw new SQLException("connection closed");
        }
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        checkClosed();
        return delegated.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        checkClosed();
        delegated.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        checkClosed();
        return delegated.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        checkClosed();
        delegated.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        checkClosed();
        return delegated.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        checkClosed();
        delegated.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        checkClosed();
        return delegated.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkClosed();
        return delegated.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkClosed();
        delegated.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        checkClosed();
        return delegated.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        checkClosed();
        return delegated.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkClosed();
        return delegated.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        checkClosed();
        return delegated.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        checkClosed();
        delegated.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        checkClosed();
        delegated.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        checkClosed();
        return delegated.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        checkClosed();
        return delegated.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        checkClosed();
        return delegated.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        checkClosed();
        delegated.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkClosed();
        delegated.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        checkClosed();
        return delegated.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        checkClosed();
        return delegated.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        checkClosed();
        return delegated.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        checkClosed();
        return delegated.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        checkClosed();
        return delegated.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        checkClosed();
        return delegated.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        checkClosed();
        return delegated.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        checkClosed();
        return delegated.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        checkClosed();
        return delegated.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        checkClosed();
        return delegated.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        checkClosed();
        return delegated.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        delegated.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        delegated.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return delegated.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return delegated.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return delegated.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return delegated.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        delegated.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return delegated.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        checkClosed();
        delegated.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        checkClosed();
        delegated.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        checkClosed();
        return delegated.getNetworkTimeout();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(delegated.getClass())) {
            return (T) delegated;
        }
        return delegated.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface.isAssignableFrom(delegated.getClass())) {
            return true;
        }
        return delegated.isWrapperFor(iface);
    }
}
