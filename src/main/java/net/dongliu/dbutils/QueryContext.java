package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.TooManyResultException;
import net.dongliu.dbutils.mapper.BeanRowMapper;
import net.dongliu.dbutils.mapper.RecordRowMapper;
import net.dongliu.dbutils.mapper.RowMapper;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Class for holding sql execute contexts which returns a resultSet.
 *
 * @author Liu Dong
 */
public abstract class QueryContext extends AbstractQueryContext<QueryContext> {

    QueryContext() {
    }

    /**
     * Set RowMapper to convert the ResultSet
     */
    public <T> TypedQueryContext<T> map(RowMapper<T> rowMapper) {
        return new TypedQueryContext<T>(requireNonNull(rowMapper)) {
            @Override
            protected PreparedStatement prepare(int fetchSize, String[] keyColumns, Connection connection)
                    throws SQLException {
                return QueryContext.this.prepare(fetchSize, keyColumns, connection);
            }

            @Override
            protected ResultSet execute(int fetchSize, PreparedStatement statement) throws SQLException {
                return QueryContext.this.execute(fetchSize, statement);
            }

            @Override
            protected MyConnection retrieveConnection() throws SQLException {
                return QueryContext.this.retrieveConnection();
            }
        };
    }

    /**
     * Set a bean lass to convert the ResultSet to.
     */
    public <T> TypedQueryContext<T> map(Class<T> beanClass) {
        return map(beanClass, true);
    }

    /**
     * Set a bean lass to convert the ResultSet to.
     *
     * @param abortWhenMissingProperty if true: when bean do not have property to hold column, throw exception
     */
    public <T> TypedQueryContext<T> map(Class<T> beanClass, boolean abortWhenMissingProperty) {
        return map(BeanRowMapper.getInstance(beanClass, abortWhenMissingProperty));
    }

    /**
     * Return query result as List of Record
     */
    public List<Record> getList() {
        return convertToList(RecordRowMapper.getInstance());
    }

    /**
     * Return query result as one Record
     *
     * @return null if no record returned.
     * @throws TooManyResultException if has more than one result
     */
    @Nullable
    public Record getOne() throws TooManyResultException {
        return convertTo(RecordRowMapper.getInstance());
    }

    /**
     * Wrap ResultSet as Stream.
     * Need to close this stream if not consumed.
     */
    public Stream<Record> asStream() {
        return asStream(RecordRowMapper.getInstance());
    }
}
