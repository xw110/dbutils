package net.dongliu.dbutils.handlers;

import net.dongliu.dbutils.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Processor convert row to map with column name as key
 *
 * @author Liu Dong
 */
public class MapMapper implements RowMapper<Map<String, Object>> {

    private String[] names;

    private void init(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int count = metaData.getColumnCount();
        String[] names = new String[count + 1];
        for (int i = 1; i <= count; i++) {
            String columnName = metaData.getColumnLabel(i);
            if (columnName == null || columnName.isEmpty()) {
                columnName = metaData.getColumnName(i);
            }
            names[i] = columnName;
        }
        this.names = names;
    }

    @Override
    public Map<String, Object> convert(ResultSet rs, int row) throws SQLException {
        if (row == 1) {
            init(rs);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 1; i < names.length; i++) {
            result.put(names[i], rs.getObject(i));
        }

        return result;
    }
}
