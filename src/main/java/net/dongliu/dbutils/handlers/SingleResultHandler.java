package net.dongliu.dbutils.handlers;

import net.dongliu.dbutils.ResultSetHandler;
import net.dongliu.dbutils.RowMapper;
import net.dongliu.dbutils.exception.TooManyResultException;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Class that simplify development of ResultSetHandler classes that convert single row resultSet into object.
 *
 * @param <T> the target List generic type
 */
public class SingleResultHandler<T> implements ResultSetHandler<T> {
    private final RowMapper<T> rowMapper;

    public SingleResultHandler(RowMapper<T> rowMapper) {
        this.rowMapper = Objects.requireNonNull(rowMapper);
    }

    @Nullable
    @Override
    public T handle(ResultSet rs) throws SQLException {
        if (rs.next()) {
            T value = rowMapper.convert(rs, 1);
            if (rs.next()) {
                throw new TooManyResultException("Two many rows returned");
            }
            return value;
        }
        return null;
    }

}
