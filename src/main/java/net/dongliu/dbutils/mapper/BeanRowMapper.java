package net.dongliu.dbutils.mapper;

import net.dongliu.dbutils.exception.MissingPropertyException;
import net.dongliu.dbutils.exception.ReflectionException;
import net.dongliu.dbutils.exception.UncheckedSQLException;
import net.dongliu.dbutils.mapping.BeanMapping;
import net.dongliu.dbutils.mapping.Property;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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


    private static final Set<Class<?>> wrappers = new HashSet<>();

    static {
        wrappers.add(Byte.class);
        wrappers.add(Short.class);
        wrappers.add(Integer.class);
        wrappers.add(Long.class);
        wrappers.add(Float.class);
        wrappers.add(Double.class);
        wrappers.add(Character.class);
        wrappers.add(Boolean.class);
    }

    /**
     * Set a ResultSet column value into an object.
     */
    private void setColumnValue(ResultSet rs, int index, Object bean, Property property) throws SQLException {

        Class<?> propertyType = property.type();
        if (propertyType == String.class) {
            property.set(bean, rs.getString(index));
        } else if (propertyType.isPrimitive()) {
            setPrimitive(rs, index, bean, property, propertyType);
        } else if (propertyType == byte[].class) {
            property.set(bean, rs.getBytes(index));
        } else if (wrappers.contains(propertyType)) {
            setWrapper(rs, index, bean, property, propertyType);
        } else if (propertyType == Timestamp.class || propertyType == Date.class) {
            property.set(bean, rs.getTimestamp(index));
        } else if (propertyType == java.sql.Date.class) {
            property.set(bean, rs.getDate(index));
        } else if (propertyType == java.sql.Time.class) {
            property.set(bean, rs.getTime(index));
        } else if (propertyType == Instant.class) {
            Timestamp timestamp = rs.getTimestamp(index);
            if (timestamp != null) {
                property.set(bean, timestamp.toInstant());
            } else {
                property.set(bean, null);
            }
        } else if (propertyType == LocalDateTime.class) {
            Timestamp timestamp = rs.getTimestamp(index);
            if (timestamp != null) {
                property.set(bean, timestamp.toLocalDateTime());
            } else {
                property.set(bean, null);
            }
        } else if (propertyType == LocalDate.class) {
            Date date = rs.getDate(index);
            if (date != null) {
                property.set(bean, date.toLocalDate());
            } else {
                property.set(bean, null);
            }
        } else if (propertyType == LocalTime.class) {
            Time time = rs.getTime(index);
            if (time != null) {
                property.set(bean, time.toLocalTime());
            } else {
                property.set(bean, null);
            }
        } else if (propertyType == SQLXML.class) {
            property.set(bean, rs.getSQLXML(index));
        } else if (propertyType.getClass().isEnum()) {
            String str = rs.getString(index);
            if (str == null) {
                property.set(bean, null);
            } else {
                property.set(bean, Enum.valueOf(propertyType.asSubclass(Enum.class), str));
            }
        } else {
            Object value = rs.getObject(index);
            property.set(bean, value);
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
            throw new UncheckedSQLException("can not convert to char type");
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
            throw new UncheckedSQLException("can not convert to char type");
        } else {
            throw new RuntimeException("Not primitive type: " + type);
        }
    }
}
