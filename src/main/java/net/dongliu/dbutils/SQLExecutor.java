package net.dongliu.dbutils;

import net.dongliu.commons.collection.Sets;
import net.dongliu.dbutils.exception.UncheckedSQLException;

import java.sql.*;
import java.time.*;
import java.util.List;
import java.util.Set;

import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_FORWARD_ONLY;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

/**
 * For reuse code across SQLRunner and TransactionContext.
 */
abstract class SQLExecutor {
    protected abstract MyConnection supplyConnection() throws SQLException;

    /**
     * Execute select sql, and return query result.
     */
    public QueryContext query(String clause, Object... params) {
        return new QueryContext() {
            @Override
            protected PreparedStatement prepare(int fetchSize, String[] keyColumns, Connection connection)
                    throws SQLException {
                return fetchSize == 0 ? connection.prepareStatement(clause) :
                        // mysql need to set those flags to make fetch size work
                        connection.prepareStatement(clause, TYPE_FORWARD_ONLY, CONCUR_READ_ONLY);
            }

            @Override
            protected ResultSet execute(int fetchSize, PreparedStatement statement) throws SQLException {
                fillStatement(statement, params);
                statement.setFetchSize(fetchSize);
                statement.execute();
                return statement.getResultSet();
            }

            @Override
            protected MyConnection retrieveConnection() throws SQLException {
                return supplyConnection();
            }
        };
    }

    /**
     * Execute insert/update/delete sql, and return affected row num
     */
    public int update(String clause, Object... params) {
        try (MyConnection ci = supplyConnection();
             PreparedStatement stmt = ci.prepareStatement(clause)) {
            fillStatement(stmt, params);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    /**
     * Execute insert sql, and return inserted auto-gen keys as result
     */
    public QueryContext insert(String clause, Object... params) {
        return new QueryContext() {
            @Override
            protected PreparedStatement prepare(int fetchSize, String[] keyColumns, Connection connection)
                    throws SQLException {
                return keyColumns.length == 0 ? connection.prepareStatement(clause, RETURN_GENERATED_KEYS)
                        : connection.prepareStatement(clause, keyColumns);
            }

            @Override
            protected ResultSet execute(int fetchSize, PreparedStatement statement) throws SQLException {
                fillStatement(statement, params);
                statement.executeUpdate();
                return statement.getGeneratedKeys();
            }

            @Override
            protected MyConnection retrieveConnection() throws SQLException {
                return supplyConnection();
            }

        };
    }

    /**
     * Execute batch insert/update/delete sql, and return affected row nums
     */
    public int[] batchUpdate(String clause, List<Object[]> params) {
        try (MyConnection ci = supplyConnection();
             PreparedStatement stmt = ci.prepareStatement(clause)) {
            for (Object[] param : params) {
                fillStatement(stmt, param);
                stmt.addBatch();
            }
            return stmt.executeBatch();
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    /**
     * Execute batch insert sql, and return inserted auto-gen  keys as result.
     */
    public QueryContext batchInsert(String clause, List<Object[]> params) {
        return new QueryContext() {
            @Override
            protected PreparedStatement prepare(int fetchSize, String[] keyColumns, Connection connection)
                    throws SQLException {
                return keyColumns.length == 0 ?
                        connection.prepareStatement(clause, RETURN_GENERATED_KEYS)
                        : connection.prepareStatement(clause, keyColumns);
            }

            @Override
            protected ResultSet execute(int fetchSize, PreparedStatement statement) throws SQLException {
                for (Object[] param : params) {
                    fillStatement(statement, param);
                    statement.addBatch();
                }
                statement.executeBatch();
                return statement.getGeneratedKeys();
            }

            @Override
            protected MyConnection retrieveConnection() throws SQLException {
                return supplyConnection();
            }
        };
    }

    // Additional support for java8 time types.
    // Many drivers do not support java8 time well, so handle this using java.sql.* as bridge.
    // Note that this will lose the nano seconds.
    private static final Set<Class<?>> java8TimeTypes = Sets.of(
            LocalDate.class, LocalDateTime.class, LocalTime.class,
            OffsetDateTime.class, OffsetTime.class,
            Instant.class
    );

    /**
     * Fill the PreparedStatement replacement parameters with the given objects.
     *
     * @param stmt   PreparedStatement to fill
     * @param params Query replacement parameters; null is a valid value to pass in.
     */
    private static void fillStatement(PreparedStatement stmt, Object... params) throws SQLException {

        // check the parameter count, if we can
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param == null) {
                // VARCHAR works with many drivers regardless
                // of the actual column type. Oddly, NULL and
                // OTHER don't work with Oracle's drivers.
                stmt.setNull(i + 1, Types.VARCHAR);
                continue;
            }

            Class<?> type = param.getClass();
            if (type.isEnum()) {
                // special handle for enum types
                param = ((Enum) param).name();
            } else if (java8TimeTypes.contains(type)) {
                if (param instanceof LocalDate) {
                    param = Date.valueOf((LocalDate) param);
                } else if (param instanceof LocalDateTime) {
                    param = Timestamp.valueOf((LocalDateTime) param);
                } else if (param instanceof LocalTime) {
                    param = Time.valueOf((LocalTime) param);
                } else if (param instanceof OffsetDateTime) {
                    param = Timestamp.from(Instant.from((OffsetDateTime) param));
                } else if (param instanceof OffsetTime) {
                    param = Timestamp.from(Instant.from((OffsetTime) param));
                } else if (param instanceof Instant) {
                    param = Timestamp.from((Instant) param);
                }
            }
            stmt.setObject(i + 1, param);
        }
    }
}
