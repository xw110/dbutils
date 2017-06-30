package net.dongliu.dbutils.handlers;

import net.dongliu.dbutils.RowMapper;

import javax.annotation.concurrent.Immutable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Processor convert row to object array
 *
 * @author Liu Dong
 */
@Immutable
public class ArrayMapper implements RowMapper<Object[]> {
    private static final ArrayMapper instance = new ArrayMapper();

    public static ArrayMapper getInstance() {
        return instance;
    }

    @Override
    public Object[] convert(ResultSet rs, int row) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        Object[] result = new Object[cols];
        for (int i = 0; i < cols; i++) {
            result[i] = rs.getObject(i + 1);
        }

        return result;
    }
}
