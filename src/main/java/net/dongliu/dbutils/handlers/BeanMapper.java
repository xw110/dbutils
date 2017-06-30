package net.dongliu.dbutils.handlers;

import net.dongliu.dbutils.RowMapper;
import net.dongliu.dbutils.exception.ReflectionException;
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
 * <p>
 * BeanMapper matches column names to bean property names
 * and converts ResultSet columns into objects for those bean
 * properties.  Subclasses should override the methods in the processing chain
 * to customize behavior.
 * </p>
 * <p>
 * This class is not thread-safe.
 * </p>
 */
public class BeanMapper<T> implements RowMapper<T> {

    private final Class<T> type;
    private final Constructor<T> constructor;

    /**
     * column start with index 1
     */
    private Property[] columnToProperty;

    /**
     * Constructor for BeanMapper configured with column to property name overrides.
     */
    public BeanMapper(Class<T> type) {
        this.type = type;
        try {
            this.constructor = type.getConstructor();
            this.constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ReflectionException(e);
        }
    }

    private void init(ResultSet rs) throws SQLException {
        BeanMapping beanMapping = BeanMapping.getBeanMapping(type);
        ResultSetMetaData metaData = rs.getMetaData();
        this.columnToProperty = this.mapColumnsToProperties(metaData, beanMapping);
    }

    @Override
    public T convert(ResultSet rs, int row) throws SQLException {
        if (row == 1) {
            init(rs);
        }
        T bean;
        try {
            bean = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
        for (int i = 1; i < columnToProperty.length; i++) {
            Property property = columnToProperty[i];
            if (property == null) {
                continue;
            }
            try {
                this.processColumn(rs, i, bean, property);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new ReflectionException(e);
            }
        }

        return bean;
    }

    /**
     * Map resultSet columns to bean properties
     *
     * @param metaData The ResultSetMetaData containing column information.
     * @throws SQLException if a database access error occurs
     */
    private Property[] mapColumnsToProperties(ResultSetMetaData metaData, BeanMapping beanMapping)
            throws SQLException {

        final int count = metaData.getColumnCount();
        final Property[] properties = new Property[count + 1];

        for (int col = 1; col <= count; col++) {
            String columnName = metaData.getColumnLabel(col);
            if (columnName == null || columnName.isEmpty()) {
                columnName = metaData.getColumnName(col);
            }

            String propertyName = columnName.toLowerCase();
            Property property = beanMapping.getProperty(propertyName);
            if (property == null) {
                property = beanMapping.getProperty(propertyName.replace("_", ""));
                if (property == null) {
                    continue;
                }
            }
            properties[col] = property;
        }

        return properties;
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
     * Convert a ResultSet column into an object.  Simple
     * implementations could just call rs.getObject(index) while
     * more complex implementations could perform type manipulation to match
     * the column's type to the bean property type.
     * <p>
     * <p>
     * This implementation calls the appropriate ResultSet getter
     * method for the given property type to perform the type conversion.  If
     * the property type doesn't match one of the supported
     * ResultSet types, getObject is called.
     * </p>
     *
     * @param rs    The ResultSet currently being processed.  It is
     *              positioned on a valid row before being passed into this method.
     * @param index The current column index being processed.
     * @throws SQLException if a database access error occurs
     */
    private void processColumn(ResultSet rs, int index, Object bean, Property property)
            throws SQLException, InvocationTargetException, IllegalAccessException {

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

    private void setWrapper(ResultSet rs, int index, Object bean, Property property,
                            Class<?> propertyType)
            throws SQLException, IllegalAccessException, InvocationTargetException {
        if (propertyType == Integer.class) {
            int value = rs.getInt(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (propertyType == Boolean.class) {
            boolean value = rs.getBoolean(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (propertyType == Long.class) {
            long value = rs.getLong(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (propertyType == Double.class) {
            double value = rs.getDouble(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (propertyType == Float.class) {
            float value = rs.getFloat(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (propertyType == Short.class) {
            short value = rs.getShort(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (propertyType == Byte.class) {
            byte value = rs.getByte(index);
            if (rs.wasNull()) {
                property.set(bean, null);
            } else {
                property.set(bean, value);
            }
        } else if (propertyType == Character.class) {
            String value = rs.getString(index);
            if (value == null || value.isEmpty()) {
                property.set(bean, null);
            } else {
                property.set(bean, value.charAt(0));
            }
        }
    }

    private void setPrimitive(ResultSet rs, int index, Object bean, Property property, Class<?> type)
            throws IllegalAccessException, InvocationTargetException, SQLException {
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
            // should not use char...
            String s = rs.getString(index);
            if (s != null && !s.isEmpty()) {
                property.set(bean, s.charAt(0));
            }
        } else {
            throw new RuntimeException("Not primitive type: " + type);
        }
    }
}
