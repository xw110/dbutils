package net.dongliu.dbutils.mapper;

import java.sql.SQLException;

/**
 * Interface for get column names lazily
 */
@FunctionalInterface
public interface ColumnNamesProvider {

    /**
     * Get column names
     */
    String[] get() throws SQLException;

}
