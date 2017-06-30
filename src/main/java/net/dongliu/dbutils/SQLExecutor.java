package net.dongliu.dbutils;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Parent class for all which can execute sqls.
 *
 * @author Liu Dong
 */
public abstract class SQLExecutor {

    protected abstract ConnectionInfo supplyConnection();

    /**
     * Execute select sql, and return query result
     */
    public QuerySQLContext query(String clause, Object... params) {
        ConnectionInfo connectionInfo = supplyConnection();
        return new QuerySQLContext(connectionInfo.connection, connectionInfo.autoClose)
                .clause(clause).params(params);
    }

    /**
     * Execute insert/update/delete sql, and return affected row num
     */
    public UpdateSQLContext update(String clause, Object... params) {
        ConnectionInfo connectionInfo = supplyConnection();
        return new UpdateSQLContext(connectionInfo.connection, connectionInfo.autoClose)
                .clause(clause).params(params);
    }

    /**
     * Execute insert sql, and return inserted keys as result
     */
    public InsertSQLContext insert(String clause, Object... params) {
        ConnectionInfo connectionInfo = supplyConnection();
        return new InsertSQLContext(connectionInfo.connection, connectionInfo.autoClose)
                .clause(clause).params(params);
    }

    /**
     * Execute batch insert/update/delete sql, and return affected row nums
     */
    public BatchUpdateSQLContext batchUpdate(String clause, Object[]... params) {
        ConnectionInfo connectionInfo = supplyConnection();
        return new BatchUpdateSQLContext(connectionInfo.connection, connectionInfo.autoClose)
                .clause(clause).params(params);
    }

    /**
     * Execute batch insert sql, and return inserted keys as result
     */
    public BatchInsertSQLContext batchInsert(String clause, Object[]... params) {
        ConnectionInfo connectionInfo = supplyConnection();
        return new BatchInsertSQLContext(connectionInfo.connection, connectionInfo.autoClose)
                .clause(clause).params(params);
    }

    /**
     * Execute select sql, and return query result
     */
    public QuerySQLContext query(String clause, List<?> params) {
        return query(clause, params.toArray());
    }

    /**
     * Execute insert/update/delete sql, and return affected row num
     */
    public UpdateSQLContext update(String clause, List<?> params) {
        return update(clause, params.toArray());
    }

    /**
     * Execute insert sql, and return inserted keys as result
     */
    public InsertSQLContext insert(String clause, List<?> params) {
        return insert(clause, params.toArray());
    }

    /**
     * Execute batch insert/update/delete sql, and return affected row nums
     */
    public BatchUpdateSQLContext batchUpdate(String clause, List<Object[]> params) {
        return batchUpdate(clause, params.toArray());
    }

    /**
     * Execute batch insert sql, and return inserted keys as result
     */
    public BatchInsertSQLContext batchInsert(String clause, List<Object[]> params) {
        return batchInsert(clause, params.toArray());
    }

    /**
     * Execute select named-parameter sql, and return query result
     */
    public QuerySQLContext queryNamed(String clause, Map<String, ?> params) {
        SQL sql = NamedSQLParser.translate(clause, params);
        return query(sql.clause(), sql.params());
    }

    /**
     * Execute insert/update/delete named-parameter sql, and return affected row num
     */
    public UpdateSQLContext updateNamed(String clause, Map<String, ?> params) {
        SQL sql = NamedSQLParser.translate(clause, params);
        return update(sql.clause(), sql.params());
    }

    /**
     * Execute insert named-parameter sql, and return inserted keys as result
     */
    public InsertSQLContext insertNamed(String clause, Map<String, ?> params) {
        SQL sql = NamedSQLParser.translate(clause, params);
        return insert(sql.clause(), sql.params());
    }

    /**
     * Execute batch insert/update/delete named-parameter sql, and return affected row nums
     */
    public BatchUpdateSQLContext batchUpdateNamed(String clause, List<? extends Map<String, ?>> params) {
        BatchSQL sql = NamedSQLParser.translate(clause, params);
        return batchUpdate(sql.clause(), sql.params());
    }

    /**
     * Execute batch insert named-parameter sql, and return inserted keys as result
     */
    public BatchInsertSQLContext batchInsertNamed(String clause, List<? extends Map<String, ?>> params) {
        BatchSQL sql = NamedSQLParser.translate(clause, params);
        return batchInsert(sql.clause(), sql.params());
    }

    /**
     * Execute select named-parameter sql, and return query result
     */
    public QuerySQLContext queryNamed(String clause, Object bean) {
        SQL sql = NamedSQLParser.translateBean(clause, bean);
        return query(sql.clause(), sql.params());
    }

    /**
     * Execute insert/update/delete named-parameter sql, and return affected row num
     */
    public UpdateSQLContext updateNamed(String clause, Object bean) {
        SQL sql = NamedSQLParser.translateBean(clause, bean);
        return update(sql.clause(), sql.params());
    }

    /**
     * Execute insert named-parameter sql, and return inserted keys as result
     */
    public InsertSQLContext insertNamed(String clause, Object bean) {
        SQL sql = NamedSQLParser.translateBean(clause, bean);
        return insert(sql.clause(), sql.params());
    }

    /**
     * Execute batch insert/update/delete named-parameter sql, and return affected row nums
     */
    @SafeVarargs
    public final <T> BatchUpdateSQLContext batchUpdateNamed(String clause, T... beans) {
        BatchSQL sql = NamedSQLParser.translateBean(clause, Arrays.asList(beans));
        return batchUpdate(sql.clause(), sql.params());
    }

    /**
     * Execute batch insert named-parameter sql, and return inserted keys as result
     */
    @SafeVarargs
    public final <T> BatchInsertSQLContext batchInsertNamed(String clause, T... beans) {
        BatchSQL sql = NamedSQLParser.translateBean(clause, Arrays.asList(beans));
        return batchInsert(sql.clause(), sql.params());
    }

    protected static class ConnectionInfo {
        private final boolean autoClose;
        private final Connection connection;

        public ConnectionInfo(Connection connection, boolean autoClose) {
            this.autoClose = autoClose;
            this.connection = connection;
        }

        public boolean isAutoClose() {
            return autoClose;
        }

        public Connection getConnection() {
            return connection;
        }
    }
}
