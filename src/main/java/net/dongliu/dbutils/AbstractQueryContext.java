package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.TooManyResultException;
import net.dongliu.dbutils.exception.UncheckedSQLException;
import net.dongliu.dbutils.mapper.ColumnNamesProvider;
import net.dongliu.dbutils.mapper.RowMapper;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

abstract class AbstractQueryContext<S extends AbstractQueryContext<S>> {
    private String[] keyColumns = emptyColumn;
    private int fetchSize = 0;

    private static final String[] emptyColumn = {};

    AbstractQueryContext() {
    }

    /**
     * Set the key Columns.
     * This method is for fetching Insert sql clause result.
     * If not set, would use auto-generated key columns of table.
     */
    @SuppressWarnings("unchecked")
    public S keyColumns(String[] keyColumns) {
        this.keyColumns = requireNonNull(keyColumns);
        return (S) this;
    }

    /**
     * Set the num of rows resultSet fetch each time. Default 0, means not set.
     */
    @SuppressWarnings("unchecked")
    public S fetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return (S) this;
    }

    /**
     * Handler result with single row or no row, and return converted value
     */
    @Nullable
    protected <T> T convertTo(RowMapper<T> mapper) {
        return handle(rs -> {
            if (!rs.next()) {
                return null;
            }
            T value = mapper.map(columnNamesProvider(rs), rs);
            if (rs.next()) {
                throw new TooManyResultException(1);
            }
            return value;
        });
    }

    /**
     * Handler result, and return converted values as List
     */
    protected <T> List<T> convertToList(RowMapper<T> mapper) {
        return handle(rs -> {
            List<T> list = new ArrayList<>();
            ColumnNamesProvider provider = columnNamesProvider(rs);
            while (rs.next()) {
                T value = mapper.map(provider, rs);
                list.add(value);
            }
            return list;
        });
    }

    /**
     * Handler result, and return converted values
     */
    public <T> T handle(ResultSetHandler<T> handler) {
        try (MyConnection conn = retrieveConnection();
             PreparedStatement statement = prepare(fetchSize, keyColumns, conn)) {
            try (ResultSet resultSet = execute(fetchSize, statement)) {
                return handler.handle(resultSet);
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    /**
     * Executes the SQL query and return result as stream.
     * Make sure stream is closed when no longer used.
     */
    public <T> Stream<T> asStream(RowMapper<T> mapper) {
        MyConnection conn;
        try {
            conn = retrieveConnection();
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
        PreparedStatement statement;
        try {
            statement = prepare(fetchSize, keyColumns, conn);
        } catch (SQLException e) {
            close(e, conn);
            throw new UncheckedSQLException(e);
        } catch (Throwable t) {
            close(t, conn);
            throw t;
        }

        ResultSet resultSet;
        try {
            resultSet = execute(fetchSize, statement);
        } catch (SQLException e) {
            close(e, statement, conn);
            throw new UncheckedSQLException(e);
        } catch (Throwable t) {
            close(t, statement, conn);
            throw t;
        }
        return asStream(resultSet, mapper, statement, conn);
    }

    /**
     * Wrap resultSet as stream. Make sure stream is closed when no longer used.
     */
    private <T> Stream<T> asStream(ResultSet resultSet, RowMapper<T> mapper, Statement statement, MyConnection conn) {
        Iterator<T> iterator = asIterator(resultSet, mapper);
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        Stream<T> stream = StreamSupport.stream(spliterator, false);
        return stream.onClose(wrapRunnable(resultSet::close))
                .onClose(wrapRunnable(statement::close))
                .onClose(wrapRunnable(conn::close));
    }

    private static void close(Throwable t, Connection connection) {
        try {
            connection.close();
        } catch (Throwable e) {
            t.addSuppressed(e);
        }
    }

    private static void close(Throwable t, Statement statement, Connection connection) {
        try {
            statement.close();
        } catch (Throwable e) {
            t.addSuppressed(e);
        }
        try {
            connection.close();
        } catch (Throwable e) {
            t.addSuppressed(e);
        }
    }

    @FunctionalInterface
    interface SQLRunnable {
        void run() throws SQLException;
    }


    private static Runnable wrapRunnable(SQLRunnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
    }

    protected <T> Iterator<T> asIterator(ResultSet rs, RowMapper<T> mapper) {
        ColumnNamesProvider provider = columnNamesProvider(rs);
        return new Iterator<T>() {
            int row = 0;
            boolean hasNext;
            boolean inspected;

            @Override
            public boolean hasNext() {
                if (!inspected) {
                    inspectNext();
                }
                return hasNext;
            }

            public void inspectNext() {
                try {
                    hasNext = rs.next();
                    inspected = true;
                } catch (SQLException e) {
                    throw new UncheckedSQLException(e);
                }
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                row++;
                try {
                    T value = mapper.map(provider, rs);
                    inspected = false;
                    return value;
                } catch (SQLException e) {
                    throw new UncheckedSQLException(e);
                }
            }
        };
    }

    private ColumnNamesProvider columnNamesProvider(ResultSet rs) {
        return new ColumnNamesProvider() {
            private String[] names;

            @Override
            public String[] get() throws SQLException {
                if (names == null) {
                    names = getColumnNames(rs);
                }
                return names;
            }
        };
    }

    private String[] getColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int count = metaData.getColumnCount();
        String[] names = new String[count];
        for (int i = 1; i <= count; i++) {
            String columnName = metaData.getColumnLabel(i);
            if (columnName == null || columnName.isEmpty()) {
                columnName = metaData.getColumnName(i);
            }
            names[i - 1] = columnName;
        }
        return names;
    }

    protected abstract PreparedStatement prepare(int fetchSize, String[] keyColumns, Connection connection)
            throws SQLException;

    protected abstract ResultSet execute(int fetchSize, PreparedStatement statement) throws SQLException;

    protected abstract MyConnection retrieveConnection() throws SQLException;
}
