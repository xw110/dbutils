package net.dongliu.dbutils;

import java.util.List;
import java.util.Map;

/**
 * For reuse code across NamedSQLRunner and TransactionContext.
 */
abstract class NamedSQLExecutor<T extends SQLExecutor> {
    protected final T delegated;

    protected NamedSQLExecutor(T sqlRunner) {
        this.delegated = sqlRunner;
    }

    /**
     * Execute select named-parameter sql, and return query result
     */
    public QueryContext query(String clause, Map<String, ?> params) {
        SQL sql = NamedSQLParser.translate(clause, params);
        return delegated.query(sql.clause(), sql.params());
    }

    /**
     * Execute insert/update/delete named-parameter sql, and return affected row num
     */
    public int update(String clause, Map<String, ?> params) {
        SQL sql = NamedSQLParser.translate(clause, params);
        return delegated.update(sql.clause(), sql.params());
    }

    /**
     * Execute insert named-parameter sql, and return inserted auto-gen keys as result
     */
    public QueryContext insert(String clause, Map<String, ?> params) {
        SQL sql = NamedSQLParser.translate(clause, params);
        return delegated.insert(sql.clause(), sql.params());
    }

    /**
     * Execute batch insert/update/delete named-parameter sql, and return affected row nums
     */
    public int[] batchUpdate(String clause, List<Map<String, ?>> params) {
        BatchSQL sql = NamedSQLParser.translate(clause, params);
        return delegated.batchUpdate(sql.clause(), sql.params());
    }

    /**
     * Execute batch insert named-parameter sql, and return inserted keys as result
     */
    public final QueryContext batchInsert(String clause, List<Map<String, ?>> params) {
        BatchSQL sql = NamedSQLParser.translate(clause, params);
        return delegated.batchInsert(sql.clause(), sql.params());
    }

    /**
     * Execute select named-parameter sql, and return query result
     */
    public QueryContext query(String clause, Object bean) {
        SQL sql = NamedSQLParser.translateBean(clause, bean);
        return delegated.query(sql.clause(), sql.params());
    }

    /**
     * Execute insert/update/delete named-parameter sql, and return affected row num
     */
    public int updateByBean(String clause, Object bean) {
        SQL sql = NamedSQLParser.translateBean(clause, bean);
        return delegated.update(sql.clause(), sql.params());
    }

    /**
     * Execute insert named-parameter sql, and return inserted auto-gen keys as result
     */
    public QueryContext insertByBean(String clause, Object bean) {
        SQL sql = NamedSQLParser.translateBean(clause, bean);
        return delegated.insert(sql.clause(), sql.params());
    }

    /**
     * Execute batch insert/update/delete named-parameter sql, and return affected row nums
     */
    public int[] batchUpdateByBeans(String clause, List<?> beans) {
        BatchSQL sql = NamedSQLParser.translateBean(clause, beans);
        return delegated.batchUpdate(sql.clause(), sql.params());
    }

    /**
     * Execute batch insert named-parameter sql, and return inserted auto-gen keys as result
     */
    public QueryContext batchInsertByBeans(String clause, List<?> beans) {
        BatchSQL sql = NamedSQLParser.translateBean(clause, beans);
        return delegated.batchInsert(sql.clause(), sql.params());
    }
}
