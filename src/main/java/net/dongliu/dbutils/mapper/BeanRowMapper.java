package net.dongliu.dbutils.mapper;

import net.dongliu.commons.collection.Sets;
import net.dongliu.dbutils.exception.MissingPropertyException;
import net.dongliu.dbutils.exception.ReflectionException;
import net.dongliu.dbutils.mapping.BeanMapping;
import net.dongliu.dbutils.mapping.Property;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Convert row to bean
 *
 * @param <T>
 */
public class BeanRowMapper<T> implements RowMapper<T> {
    private final Class<T> cls;
    private final boolean requireAllColumns;
    private final Constructor<T> constructor;
    private final BeanMapping beanMapping;

    private BeanRowMapper(Class<T> cls, boolean requireAllColumns) {
        this.cls = cls;
        this.beanMapping = BeanMapping.getBeanMapping(cls);
        this.requireAllColumns = requireAllColumns;
        try {
            this.constructor = cls.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new ReflectionException(e);
        }
    }

    public static <T> BeanRowMapper<T> getInstance(Class<T> cls, boolean requireAllColumns) {
        return new BeanRowMapper<>(cls, requireAllColumns);
    }

    @Override
    public T map(ColumnNamesProvider provider, ResultSet rs) throws SQLException {
        String[] names = provider.get();
        T bean;
        try {
            bean = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            Property property = beanMapping.getProperty(name);
            if (property == null) {
                property = beanMapping.getProperty(name.replace("_", ""));
            }
            if (property == null) {
                if (requireAllColumns) {
                    throw new MissingPropertyException(cls.getName(), name);
                }
                continue;
            }
            setColumnValue(rs, i + 1, bean, property);
        }
        return bean;
    }


    private static final Set<Class<?>> wrappers = Sets.of(Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class, Character.class, Boolean.class);

    // Additional support for java8 time types.
    // Many drivers do not support java8 time well, so handle this using java.sql.* as bridge.
    // Note that this will lose the nano seconds.
    private static final Set<Class<?>> java8TimeTypes = Sets.of(
            LocalDate.class, LocalDateTime.class, LocalTime.class, OffsetDateTime.class, OffsetTime.class
    );

    // Types that has a jdbc getXXX method support, and not primitive and wrapper types.
    // The getXXX methods may have wilder conversion than getObject.
    private static final Set<Class<?>> jdbcConvectionTypes = Sets.of(
            String.class,
            BigDecimal.class,
            byte[].class,
            java.sql.Date.class, Time.class, Timestamp.class,
            Clob.class, Blob.class,
            Array.class,
            Struct.class,
            Ref.class,
            URL.class,
            RowId.class,
            SQLXML.class
    );

    /**
     * Set a ResultSet column value into an object.
     */
    private void setColumnValue(ResultSet rs, int index, Object bean, Property property) throws SQLException {

        Class<?> type = property.type();
        if (type == String.class) {
            // String is most frequent used type, place it here
            property.set(bean, rs.getString(index));
        } else if (type.isPrimitive()) {
            setPrimitive(rs, index, bean, property, type);
        } else if (wrappers.contains(type)) {
            setWrapper(rs, index, bean, property, type);
        } else if (jdbcConvectionTypes.contains(type)) {
            setJdbcTypes(rs, index, bean, property, type);
        } else if (java8TimeTypes.contains(type)) {
            setJava8Times(rs, index, bean, property, type);
        } else if (type.getClass().isEnum()) {
            // support for java enum, by use the name of enum
            String str = rs.getString(index);
            if (str == null) {
                property.set(bean, null);
            } else {
                property.set(bean, Enum.valueOf(type.asSubclass(Enum.class), str));
            }
        } else {
            // Note: java8 LocalDate/LocalTime/LocalDateTime/OffsetDateTime/OffsetTime may be supported here.
            Object value = rs.getObject(index, type);
            property.set(bean, value);
        }
    }

    private void setJava8Times(ResultSet rs, int index, Object bean, Property property, Class<?> type)
            throws SQLException {
        if (type == LocalDate.class) {
            Date date = rs.getDate(index);
            if (date == null) {
                property.set(bean, null);
            } else {
                property.set(bean, date.toLocalDate());
            }
        } else if (type == LocalTime.class) {
            Time time = rs.getTime(index);
            if (time == null) {
                property.set(bean, null);
            } else {
                property.set(bean, time.toLocalTime());
            }
        } else if (type == LocalDateTime.class) {
            Timestamp timestamp = rs.getTimestamp(index);
            if (timestamp == null) {
                property.set(bean, null);
            } else {
                property.set(bean, timestamp.toLocalDateTime());
            }
        } else if (type == OffsetDateTime.class) {
            Timestamp timestamp = rs.getTimestamp(index);
            if (timestamp == null) {
                property.set(bean, null);
            } else {
                property.set(bean, OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp.getTime()), ZoneId.systemDefault()));
            }
        } else if (type == OffsetTime.class) {
            Timestamp timestamp = rs.getTimestamp(index);
            if (timestamp == null) {
                property.set(bean, null);
            } else {
                property.set(bean, OffsetTime.ofInstant(Instant.ofEpochMilli(timestamp.getTime()), ZoneId.systemDefault()));
            }
        } else {
            throw new RuntimeException("not handle java8 types: " + type.getName());
        }
    }

    private void setJdbcTypes(ResultSet rs, int index, Object bean, Property property, Class<?> type)
            throws SQLException {
        if (type == String.class) {
            property.set(bean, rs.getString(index));
        } else if (type == BigDecimal.class) {
            property.set(bean, rs.getBigDecimal(index));
        } else if (type == byte[].class) {
            property.set(bean, rs.getBytes(index));
        } else if (type == Timestamp.class) {
            property.set(bean, rs.getTimestamp(index));
        } else if (type == java.sql.Date.class) {
            property.set(bean, rs.getDate(index));
        } else if (type == Time.class) {
            property.set(bean, rs.getTime(index));
        } else if (type == Blob.class) {
            property.set(bean, rs.getBlob(index));
        } else if (type == Clob.class) {
            property.set(bean, rs.getClob(index));
        } else if (type == Array.class) {
            property.set(bean, rs.getArray(index));
        } else if (type == Struct.class) {
            property.set(bean, rs.getObject(index, type));
        } else if (type == Ref.class) {
            property.set(bean, rs.getRef(index));
        } else if (type == URL.class) {
            property.set(bean, rs.getURL(index));
        } else if (type == RowId.class) {
            property.set(bean, rs.getRowId(index));
        } else if (type == SQLXML.class) {
            property.set(bean, rs.getSQLXML(index));
        } else {
            throw new RuntimeException("not handle jdbc type: " + type.getName());
        }
    }

    private void setWrapper(ResultSet rs, int index, Object bean, Property property, Class<?> type)
            throws SQLException {
        if (type == Integer.class) {
            int value = rs.getInt(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (type == Boolean.class) {
            boolean value = rs.getBoolean(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (type == Long.class) {
            long value = rs.getLong(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (type == Double.class) {
            double value = rs.getDouble(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (type == Float.class) {
            float value = rs.getFloat(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (type == Short.class) {
            short value = rs.getShort(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (type == Byte.class) {
            byte value = rs.getByte(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (type == Character.class) {
            property.set(bean, rs.getObject(index, type));
        } else {
            throw new RuntimeException("Not box type: " + type);
        }
    }

    private void setPrimitive(ResultSet rs, int index, Object bean, Property property, Class<?> type)
            throws SQLException {
        if (type == int.class) {
            property.set(bean, rs.getInt(index));
        } else if (type == boolean.class) {
            property.set(bean, rs.getBoolean(index));
        } else if (type == long.class) {
            property.set(bean, rs.getLong(index));
        } else if (type == double.class) {
            property.set(bean, rs.getDouble(index));
        } else if (type == float.class) {
            property.set(bean, rs.getFloat(index));
        } else if (type == short.class) {
            property.set(bean, rs.getShort(index));
        } else if (type == byte.class) {
            property.set(bean, rs.getByte(index));
        } else if (type == char.class) {
            property.set(bean, rs.getObject(index, type));
        } else {
            throw new RuntimeException("Not primitive type: " + type);
        }
    }

}
