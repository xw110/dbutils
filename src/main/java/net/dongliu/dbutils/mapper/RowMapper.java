package net.dongliu.dbutils.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper row to result
 */
public interface RowMapper<T> {

    /**
     * Map one resultSet row to T instance
     *
     * @param provider for get the columns names
     * @param rs       the result set
     */
    T map(ColumnNamesProvider provider, ResultSet rs) throws SQLException;
}
