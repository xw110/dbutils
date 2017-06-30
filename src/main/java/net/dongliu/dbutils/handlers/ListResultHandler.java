package net.dongliu.dbutils.handlers;

import net.dongliu.dbutils.ResultSetHandler;
import net.dongliu.dbutils.RowMapper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that simplify development of ResultSetHandler classes that convert ResultSet into List.
 *
 * @param <T> the target List generic type
 */
public class ListResultHandler<T> implements ResultSetHandler<List<T>> {

    private final RowMapper<T> rowMapper;

    public ListResultHandler(RowMapper<T> rowMapper) {
        this.rowMapper = Objects.requireNonNull(rowMapper);
    }

    /**
     * Whole ResultSet handler. It produce List as result.
     *
     * @param rs ResultSet to process.
     * @return a list of all rows in the result set
     * @throws SQLException error occurs
     */
    @Nonnull
    @Override
    public List<T> handle(ResultSet rs) throws SQLException {
        List<T> rows = new ArrayList<>();
        int row = 1;
        while (rs.next()) {
            rows.add(rowMapper.convert(rs, row++));
        }
        return rows;
    }

}
