package net.dongliu.dbutils.mapping;

import net.dongliu.dbutils.exception.BeanMappingException;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Property hold a filed
 */
public class FieldProperty implements Property {
    private volatile SoftReference<Field> fieldRef;
    private final WeakReference<Class<?>> classRef;
    private final String name;

    public FieldProperty(Field field) {
        this.fieldRef = new SoftReference<>(field);
        this.classRef = new WeakReference<>(field.getDeclaringClass());
        this.name = field.getName();
    }

    private Field field() {
        Field field = fieldRef.get();
        if (field != null) {
            return field;
        }
        Class<?> cls = classRef.get();
        if (cls == null) {
            // should not happen
            throw new RuntimeException("class unload");
        }
        try {
            field = cls.getField(name);
        } catch (NoSuchFieldException e) {
            // should not happen
            throw new BeanMappingException(e);
        }
        fieldRef = new SoftReference<>(field);
        return field;
    }

    @Override
    public void set(Object bean, Object value) {
        try {
            field().set(bean, value);
        } catch (IllegalAccessException e) {
            throw new BeanMappingException(e);
        }
    }

    @Override
    public Object get(Object bean) {
        try {
            return field().get(bean);
        } catch (IllegalAccessException e) {
            throw new BeanMappingException(e);
        }
    }

    @Override
    public Class<?> type() {
        return field().getType();
    }

    @Override
    public String name() {
        return name;
    }
}
