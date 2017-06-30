package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.UncheckedSQLException;
import net.dongliu.dbutils.handlers.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Class for result set retrieve.
 *
 * @author Liu Dong
 */
public class SQLResultSet implements AutoCloseable {

    private final ResultSet resultSet;
    private final Statement statementToClose;
    private final Connection connectionToClose;

    public SQLResultSet(ResultSet resultSet, Statement statementToClose, Connection connectionToClose) {
        this.resultSet = Objects.requireNonNull(resultSet);
        this.statementToClose = statementToClose;
        this.connectionToClose = connectionToClose;
    }

    /**
     * Handler result, and return converted values
     */
    @SuppressWarnings("unchecked")
    public <T> T handle(ResultSetHandler<T> handler) {
        try {
            return handler.handle(resultSet);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        } finally {
            close();
        }
    }

    /**
     * Return query result as List of raw object array
     */
    @Nonnull
    public List<Object[]> toArrayList() {
        return handle(ArrayListHandler.getInstance());
    }

    /**
     * Return query result as object array
     */
    @Nullable
    public Object[] toArray() {
        return handle(ArrayHandler.getInstance());
    }

    /**
     * Return query result as raw Map List
     */
    @Nonnull
    public List<Map<String, Object>> toMapList() {
        return handle(MapListHandler.getInstance());
    }

    /**
     * Return query result as raw Map
     */
    @Nullable
    public Map<String, Object> toMap() {
        return handle(MapHandler.getInstance());
    }

    /**
     * Return query result as Bean List
     */
    @Nonnull
    public <T> List<T> toBeanList(Class<T> cls) {
        return handle(BeanListHandler.getInstance(cls));
    }

    /**
     * Return query result as bean
     */
    @Nullable
    public <T> T toBean(Class<T> cls) {
        return handle(BeanHandler.getInstance(cls));
    }

    /**
     * Return query result as type T, using processor to convert row
     */
    @Nullable
    public <T> T to(RowMapper<T> processor) {
        return handle(new SingleResultHandler<>(processor));
    }

    /**
     * Return query result as List, using processor to convert row
     */
    @Nonnull
    public <T> List<T> toList(RowMapper<T> processor) {
        return handle(new ListResultHandler<>(processor));
    }

    /**
     * Get result as long with specified column.
     */
    public long getLong(int column) {
        return handle(rs -> rs.getLong(column));
    }

    /**
     * Get result as long with specified column
     */
    public long getLong(String columnName) {
        return handle(rs -> rs.getLong(columnName));
    }

    /**
     * Get result as int with specified column
     */
    public int getInt(int column) {
        return handle(rs -> rs.getInt(column));
    }

    /**
     * Get result as int with specified column
     */
    public int getInt(String columnName) {
        return handle(rs -> rs.getInt(columnName));
    }

    /**
     * Get result as string with specified column
     */
    public String getString(int column) {
        return handle(rs -> rs.getString(column));
    }

    /**
     * Get result as string with specified column
     */
    public String getString(String columnName) {
        return handle(rs -> rs.getString(columnName));
    }

    /**
     * Executes the SQL query and return result as stream.
     * You need consume all data in stream, or close stream manually
     */
    public <T> Stream<T> stream(RowMapper<T> processor) {
        return ResultSets.asStream(resultSet, processor, statementToClose, connectionToClose);
    }

    /**
     * Query result as Object array stream, with column index as array index
     */
    public Stream<Object[]> arrayStream() {
        return stream(ArrayMapper.getInstance());
    }

    /**
     * Query result as map stream, with column name as key
     */
    public Stream<Map<String, Object>> mapStream() {
        return stream(new MapMapper());
    }

    /**
     * Query result as bean stream
     */
    public <T> Stream<T> beanStream(Class<T> cls) {
        return stream(new BeanMapper<>(cls));
    }

    /**
     * Call close if not consume result by other methods
     */
    @Override
    public void close() throws UncheckedSQLException {
        try (ResultSet r = resultSet; Statement s = statementToClose; Connection n = connectionToClose) {
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }
}
