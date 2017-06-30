package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.ColumnNotFoundException;
import org.junit.Test;

import static org.junit.Assert.*;

public class RecordTest {

    @Test
    public void testGet() {
        Record record = new Record(
                new String[]{"id", "name", "age", "male", "address"},
                new Object[]{100000L, "Jim", 10, false, null}
        );
        assertEquals(10, record.getObject(2));
        assertEquals(10, record.getObject("age"));
        assertEquals(10, record.getInt("age"));
        assertEquals(100000L, record.getLong("id"));
        assertEquals("Jim", record.getString("name"));
        assertNull(record.getObject(4));
        assertNull(record.getObject("address"));

        assertTrue(record.containsKey("id"));
        assertFalse(record.containsKey("i1d"));
        assertTrue(record.containsValue("Jim"));

    }

    @Test(expected = ColumnNotFoundException.class)
    public void testException() {
        Record record = new Record(
                new String[]{"id", "name", "age", "male", "address"},
                new Object[]{100000L, "Jim", 10, false, null}
        );
        record.getObject("xxx");
    }
}