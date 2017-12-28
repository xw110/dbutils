package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.ColumnNotFoundException;
import net.dongliu.dbutils.exception.TypeNotMatchException;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * One jdbc ResultSet row.
 */
public class Record implements Map<String, Object> {

    private final String[] names;
    private final Object[] values;
    private transient Map<String, Object> map;

    public Record(String[] names, Object[] values) {
        this.names = requireNonNull(names);
        this.values = requireNonNull(values);
    }

    /**
     * Get value of column as object.
     *
     * @param index start from 0
     */
    public Object getObject(int index) {
        return values[index];
    }

    /**
     * Get value of column as object.
     *
     * @param name the column name
     * @throws ColumnNotFoundException if column with name not exists
     */
    public Object getObject(String name) {
        ensureMap();
        name = name.toLowerCase();
        if (!map.containsKey(name)) {
            throw new ColumnNotFoundException(name);
        }
        return map.get(name);
    }


    private void ensureMap() {
        if (map == null) {
            Map<String, Object> m = new LinkedHashMap<>();
            for (int i = 0; i < names.length; i++) {
                m.put(names[i].toLowerCase(), values[i]);
            }
            map = Collections.unmodifiableMap(m);
        }
    }

    /**
     * Get value of column as int.
     *
     * @param index start from 0
     * @throws NullPointerException if column value is null
     */
    public int getInt(int index) {
        Object value = getObject(index);
        Objects.requireNonNull(value);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Long) {
            return Math.toIntExact((Long) value);
        }

        if (value instanceof String) {
            try {
                return Integer.valueOf((String) value);
            } catch (NumberFormatException ignore) {
            }
        }

        throw new TypeNotMatchException(int.class);
    }

    /**
     * Get value of column as int.
     *
     * @param name the column name
     * @return null if value is null or name not exists
     * @throws NullPointerException if column value is null
     */
    public int getInt(String name) {
        Object value = getObject(name);
        Objects.requireNonNull(value);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Long) {
            return Math.toIntExact((Long) value);
        }

        if (value instanceof String) {
            try {
                return Integer.valueOf((String) value);
            } catch (NumberFormatException ignore) {
            }
        }

        throw new TypeNotMatchException(int.class);
    }

    /**
     * Get value of column as long.
     *
     * @param index start from 0
     * @throws NullPointerException if column value is null
     */
    public long getLong(int index) {
        Object value = getObject(index);
        Objects.requireNonNull(value);
        if (value instanceof Integer || value instanceof Long) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.valueOf((String) value);
            } catch (NumberFormatException ignore) {
            }
        }

        throw new TypeNotMatchException(long.class);
    }

    /**
     * Get value of column as long.
     *
     * @param name the column name
     * @return null if value is null or name not exists
     * @throws NullPointerException if column value is null
     */
    public long getLong(String name) {
        Object value = getObject(name);
        Objects.requireNonNull(value);
        if (value instanceof Integer || value instanceof Long) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.valueOf((String) value);
            } catch (NumberFormatException ignore) {
            }
        }

        throw new TypeNotMatchException(long.class);
    }

    /**
     * Get value of column as float.
     *
     * @param index start from 0
     * @throws NullPointerException if column value is null
     */
    public float getFloat(int index) {
        Object value = getObject(index);
        Objects.requireNonNull(value);
        if (value instanceof Float) {
            return ((Float) value);
        }
        if (value instanceof Number) {
            double d = ((Number) value).doubleValue();
            if (d > Float.MAX_VALUE || d < Float.MIN_VALUE) {
                throw new ArithmeticException();
            }
            return (float) d;
        }
        if (value instanceof String) {
            try {
                return Float.valueOf((String) value);
            } catch (NumberFormatException ignore) {
            }
        }

        throw new TypeNotMatchException(double.class);
    }

    /**
     * Get value of column as float.
     *
     * @param name the column name
     * @return null if value is null or name not exists
     * @throws NullPointerException if column value is null
     */
    public float getFloat(String name) {
        Object value = getObject(name);
        Objects.requireNonNull(value);
        if (value instanceof Float) {
            return ((Float) value);
        }
        if (value instanceof Number) {
            double d = ((Number) value).doubleValue();
            if (d > Float.MAX_VALUE || d < Float.MIN_VALUE) {
                throw new ArithmeticException();
            }
            return (float) d;
        }
        if (value instanceof String) {
            try {
                return Float.valueOf((String) value);
            } catch (NumberFormatException ignore) {
            }
        }

        throw new TypeNotMatchException(double.class);
    }

    /**
     * Get value of column as double.
     *
     * @param index start from 0
     * @throws NullPointerException if column value is null
     */
    public double getDouble(int index) {
        Object value = getObject(index);
        Objects.requireNonNull(value);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.valueOf((String) value);
            } catch (NumberFormatException ignore) {
            }
        }

        throw new TypeNotMatchException(double.class);
    }

    /**
     * Get value of column as double.
     *
     * @param name the column name
     * @return null if value is null or name not exists
     * @throws NullPointerException if column value is null
     */
    public double getDouble(String name) {
        Object value = getObject(name);
        Objects.requireNonNull(value);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.valueOf((String) value);
            } catch (NumberFormatException ignore) {
            }
        }

        throw new TypeNotMatchException(double.class);
    }

    /**
     * Get value of column as boolean.
     *
     * @param index start from 0
     * @throws NullPointerException if column value is null
     */
    public boolean getBoolean(int index) {
        Object value = getObject(index);
        Objects.requireNonNull(value);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new TypeNotMatchException(boolean.class);
    }

    /**
     * Get value of column as boolean.
     *
     * @param name the column name
     * @return null if value is null or name not exists
     * @throws NullPointerException if column value is null
     */
    public boolean getBoolean(String name) {
        Object value = getObject(name);
        Objects.requireNonNull(value);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new TypeNotMatchException(boolean.class);
    }

    /**
     * Get value of column as BigDecimal.
     *
     * @param index start from 0
     */
    public BigDecimal getDemcimal(int index) {
        Object value = getObject(index);
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Integer || value instanceof Long) {
            return BigDecimal.valueOf(((Number) value).longValue());
        }
        if (value instanceof Float || value instanceof Double) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        throw new TypeNotMatchException(BigDecimal.class);
    }

    /**
     * Get value of column as BigDecimal.
     *
     * @param name the column name
     */
    public BigDecimal getDemcimal(String name) {
        Object value = getObject(name);
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Integer || value instanceof Long) {
            return BigDecimal.valueOf(((Number) value).longValue());
        }
        if (value instanceof Float || value instanceof Double) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        throw new TypeNotMatchException(BigDecimal.class);
    }

    /**
     * Get value of column as String.
     *
     * @param index start from 0
     */
    public String getString(int index) {
        Object value = getObject(index);
        if (value instanceof String) {
            return (String) value;
        }
        throw new TypeNotMatchException(String.class);
    }

    /**
     * Get value of column as String.
     *
     * @param name the column name
     */
    public String getString(String name) {
        Object value = getObject(name);
        if (value instanceof String) {
            return (String) value;
        }
        throw new TypeNotMatchException(String.class);
    }

    /**
     * Get value of column as java.sql.Date.
     *
     * @param index start from 0
     */
    public Date getDate(int index) {
        Object value = getObject(index);
        if (value instanceof Date) {
            return (Date) value;
        }
        throw new TypeNotMatchException(Date.class);
    }

    /**
     * Get value of column as java.sql.Date.
     */
    public Date getDate(String column) {
        Object value = getObject(column);
        if (value instanceof Date) {
            return (Date) value;
        }
        throw new TypeNotMatchException(Date.class);
    }

    /**
     * Get value of column as java.sql.Time.
     */
    public Time getTime(String column) {
        Object value = getObject(column);
        if (value instanceof Time) {
            return (Time) value;
        }
        throw new TypeNotMatchException(Time.class);
    }

    /**
     * Get value of column as java.sql.Time.
     *
     * @param index start from 0
     */
    public Time getTime(int index) {
        Object value = getObject(index);
        if (value instanceof Time) {
            return (Time) value;
        }
        throw new TypeNotMatchException(Time.class);
    }

    /**
     * Get value of column as java.sql.Timestamp.
     */
    public Timestamp getTimestamp(String column) {
        Object value = getObject(column);
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        }
        throw new TypeNotMatchException(Timestamp.class);
    }

    /**
     * Get value of column as java.sql.Timestamp.
     *
     * @param index start from 0
     */
    public Timestamp getTimestamp(int index) {
        Object value = getObject(index);
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        }
        throw new TypeNotMatchException(Timestamp.class);
    }

    /**
     * Get value of column as LocalDate.
     *
     * @param index start from 0
     */
    public LocalDate getLocalDate(int index) {
        Object value = getObject(index);
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof Date) {
            return ((Date) value).toLocalDate();
        }
        throw new TypeNotMatchException(LocalDate.class);
    }

    /**
     * Get value of column as LocalDate.
     */
    public LocalDate getLocalDate(String column) {
        Object value = getObject(column);
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof Date) {
            return ((Date) value).toLocalDate();
        }
        throw new TypeNotMatchException(LocalDate.class);
    }

    /**
     * Get value of column as LocalTime.
     */
    public LocalTime getLocalTime(String column) {
        Object value = getObject(column);
        if (value instanceof LocalTime) {
            return (LocalTime) value;
        }
        if (value instanceof Time) {
            return ((Time) value).toLocalTime();
        }
        throw new TypeNotMatchException(LocalTime.class);
    }

    /**
     * Get value of column as LocalTime.
     *
     * @param index start from 0
     */
    public LocalTime getLocalTime(int index) {
        Object value = getObject(index);
        if (value instanceof LocalTime) {
            return (LocalTime) value;
        }
        if (value instanceof Time) {
            return ((Time) value).toLocalTime();
        }
        throw new TypeNotMatchException(LocalTime.class);
    }


    /**
     * Get value of column as java.time.LocalDateTime.
     */
    public LocalDateTime getLocalDateTime(String column) {
        Object value = getObject(column);
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        }
        throw new TypeNotMatchException(LocalDateTime.class);
    }

    /**
     * Get value of column as LocalDateTime.
     *
     * @param index start from 0
     */
    public LocalDateTime getLocalDateTime(int index) {
        Object value = getObject(index);
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        }
        throw new TypeNotMatchException(LocalDateTime.class);
    }

    /**
     * Get value of column as java.time.java.time.OffsetDateTime, using system default timezone.
     */
    public OffsetDateTime getOffsetDateTime(String column) {
        Object value = getObject(column);
        if (value instanceof OffsetDateTime) {
            return (OffsetDateTime) value;
        }
        if (value instanceof Timestamp) {
            return OffsetDateTime.ofInstant(((Timestamp) value).toInstant(), ZoneId.systemDefault());
        }
        throw new TypeNotMatchException(OffsetDateTime.class);
    }

    /**
     * Get value of column as OffsetDateTime, using system default timezone.
     *
     * @param index start from 0
     */
    public OffsetDateTime getOffsetDateTime(int index) {
        Object value = getObject(index);
        if (value instanceof OffsetDateTime) {
            return (OffsetDateTime) value;
        }
        if (value instanceof Timestamp) {
            return OffsetDateTime.ofInstant(((Timestamp) value).toInstant(), ZoneId.systemDefault());
        }
        throw new TypeNotMatchException(OffsetDateTime.class);
    }

    /**
     * Get value of column as java.time.java.time.OffsetTime, using system default timezone.
     */
    public OffsetTime getOffsetTime(String column) {
        Object value = getObject(column);
        if (value instanceof OffsetTime) {
            return (OffsetTime) value;
        }
        if (value instanceof Timestamp) {
            return OffsetTime.ofInstant(((Timestamp) value).toInstant(), ZoneId.systemDefault());
        }
        throw new TypeNotMatchException(OffsetTime.class);
    }

    /**
     * Get value of column as OffsetTime, using system default timezone.
     *
     * @param index start from 0
     */
    public OffsetTime getOffsetTime(int index) {
        Object value = getObject(index);
        if (value instanceof OffsetTime) {
            return (OffsetTime) value;
        }
        if (value instanceof Timestamp) {
            return OffsetTime.ofInstant(((Timestamp) value).toInstant(), ZoneId.systemDefault());
        }
        throw new TypeNotMatchException(OffsetTime.class);
    }


    /**
     * Get value of column as byte array.
     */
    public byte[] getByteArray(String column) {
        Object value = getObject(column);
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        throw new TypeNotMatchException(byte[].class);
    }

    /**
     * Get value of column as byte array.
     *
     * @param index start from 0
     */
    public byte[] getByteArray(int index) {
        Object value = getObject(index);
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        throw new TypeNotMatchException(byte[].class);
    }

    /**
     * Return values of row
     */
    Object[] getValues() {
        return values;
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public boolean isEmpty() {
        return values.length == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        ensureMap();
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (Object v : values) {
            if (Objects.equals(v, value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        ensureMap();
        return map.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        ensureMap();
        return map.keySet();
    }

    @Override
    public Collection<Object> values() {
        ensureMap();
        return map.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        ensureMap();
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Arrays.equals(names, record.names) &&
                Arrays.equals(values, record.values);
    }

    @Override
    public int hashCode() {

        int result = Arrays.hashCode(names);
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "Record{", "}");
        for (int i = 0; i < names.length; i++) {
            joiner.add(names[i] + '=' + values[i]);
        }
        return joiner.toString();
    }
}
