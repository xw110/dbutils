package net.dongliu.dbutils.mapping;

import net.dongliu.dbutils.exception.ReflectionException;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Bean mapping info
 *
 * @author Liu Dong
 */
public class BeanMapping {
    private final Map<String, Property> propertyMap;

    private BeanMapping(Map<String, Property> propertyMap) {
        this.propertyMap = propertyMap;
    }

    /**
     * Get column names this bean can mapping
     */
    public Collection<String> columnNames() {
        return propertyMap.keySet();
    }

    /**
     * Get property by name, case insensitive
     */
    public Property getProperty(String name) {
        return propertyMap.get(name);
    }

    /**
     * Return all bean properties
     */
    public Collection<Property> Properties() {
        return propertyMap.values();
    }


    private static final WeakHashMap<Class<?>, BeanMapping> cache = new WeakHashMap<>();
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Returns a PropertyDescriptor[] for the given Class.
     *
     * @param cls The Class to retrieve PropertyDescriptors for.
     * @return A PropertyDescriptor[] describing the Class.
     */
    public static BeanMapping getBeanMapping(Class<?> cls) {
        lock.readLock().lock();
        try {
            BeanMapping beanMapping = cache.get(cls);
            if (beanMapping != null) {
                return beanMapping;
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            BeanMapping beanMapping = cache.get(cls);
            if (beanMapping != null) {
                return beanMapping;
            }
            beanMapping = _getBeanMapping(cls);
            cache.put(cls, beanMapping);
            return beanMapping;
        } finally {
            lock.writeLock().unlock();
        }

    }

    private static BeanMapping _getBeanMapping(Class<?> cls) {
        // process bean properties
        Map<String, Property> propertyMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        PropertyDescriptor[] descriptors;
        try {
            descriptors = Introspector.getBeanInfo(cls).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new ReflectionException(e);
        }
        for (PropertyDescriptor descriptor : descriptors) {
            Method writeMethod = descriptor.getWriteMethod();
            Method readMethod = descriptor.getReadMethod();
            // A Illegal property to mapping to sql result set, should have both getter and setter
            // getClass was filtered by this
            if (writeMethod == null || readMethod == null) {
                continue;
            }

            Column column = getColumnAnnotation(cls, descriptor);
            String name;
            if (column != null) {
                name = column.value();
            } else {
                name = descriptor.getName();
            }
            propertyMap.put(name, new GetterSetterProperty(descriptor));
        }

        // process bean public fields
        for (Field field : cls.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            String name = field.getName();
            Column column = field.getDeclaredAnnotation(Column.class);
            if (column != null) {
                name = column.value();
            }
            if (propertyMap.containsKey(name)) {
                continue;
            }
            propertyMap.put(name, new FieldProperty(field));

        }

        return new BeanMapping(propertyMap);
    }

    private static Column getColumnAnnotation(Class<?> cls, PropertyDescriptor descriptor) {
        // try to get backing field
        Column column = null;
        Field backingField = getBackingField(cls, descriptor);
        if (backingField != null) {
            column = backingField.getAnnotation(Column.class);
        }
        if (column == null) {
            column = descriptor.getReadMethod().getAnnotation(Column.class);
        }
        if (column == null) {
            column = descriptor.getWriteMethod().getAnnotation(Column.class);
        }
        return column;
    }

    private static Field getBackingField(Class<?> cls, PropertyDescriptor descriptor) {
        String name = descriptor.getName();
        Field backingField;
        try {
            backingField = cls.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            Class<?> type = descriptor.getPropertyType();
            if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                try {
                    backingField = cls.getDeclaredField("is" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
                } catch (NoSuchFieldException e1) {
                    backingField = null;
                }
            } else {
                backingField = null;
            }
        }
        return backingField;
    }
}
