package net.dongliu.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Convert one row to result with type T. Used with SingleResultHandler and ListResultHandler
 *
 * @author Liu Dong
 */
@FunctionalInterface
public interface RowMapper<T> {
    /**
     * Called after rs.next return true.
     *
     * @param rs  resultset for row
     * @param row current row num in result set, start from 1
     * @throws SQLException
     */
    T convert(ResultSet rs, int row) throws SQLException;
}
