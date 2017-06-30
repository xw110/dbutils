package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.ColumnNotFoundException;

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
     * Get value of column as int.
     *
     * @param index start from 0
     * @throws NullPointerException if column value is null
     */
    public int getInt(int index) {
        return ((Number) getObject(index)).intValue();
    }

    /**
     * Get value of column as long.
     *
     * @param index start from 0
     * @throws NullPointerException if column value is null
     */
    public long getLong(int index) {
        return ((Number) getObject(index)).longValue();
    }

    /**
     * Get value of column as String.
     *
     * @param index start from 0
     */
    public String getString(int index) {
        return (String) getObject(index);
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
     * @param name the column name
     * @return null if value is null or name not exists
     * @throws NullPointerException if column value is null
     */
    public int getInt(String name) {
        return ((Number) getObject(name)).intValue();
    }

    /**
     * Get value of column as long.
     *
     * @param name the column name
     * @return null if value is null or name not exists
     * @throws NullPointerException if column value is null
     */
    public long getLong(String name) {
        return ((Number) getObject(name)).longValue();
    }

    /**
     * Get value of column as String.
     *
     * @param name the column name
     * @return null if value is null or name not exists
     */
    public String getString(String name) {
        return (String) getObject(name);
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
