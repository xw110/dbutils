package net.dongliu.dbutils.mapper;

import net.dongliu.dbutils.Record;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper that convert a row to Record
 */
public class RecordRowMapper implements RowMapper<Record> {

    private RecordRowMapper() {
    }

    private static final RecordRowMapper instance = new RecordRowMapper();

    public static RecordRowMapper getInstance() {
        return instance;
    }

    @Override
    public Record map(ColumnNamesProvider provider, ResultSet rs) throws SQLException {
        String[] names = provider.get();
        Object[] values = new Object[names.length];
        for (int i = 0; i < names.length; i++) {
            values[i] = rs.getObject(i + 1);
        }
        return new Record(names, values);
    }
}
