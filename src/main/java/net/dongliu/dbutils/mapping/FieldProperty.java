package net.dongliu.dbutils.mapping;

import net.dongliu.dbutils.exception.BeanMappingException;

import java.lang.reflect.Field;

/**
 * Property hold a filed.
 * This class hold string reference to Field and Class.
 */
public class FieldProperty implements Property {
    private Field field;

    public FieldProperty(Field field) {
        this.field = field;
    }

    @Override
    public void set(Object bean, Object value) {
        try {
            field.set(bean, value);
        } catch (IllegalAccessException e) {
            throw new BeanMappingException(e);
        }
    }

    @Override
    public Object get(Object bean) {
        try {
            return field.get(bean);
        } catch (IllegalAccessException e) {
            throw new BeanMappingException(e);
        }
    }

    @Override
    public Class<?> type() {
        return field.getType();
    }

    @Override
    public String name() {
        return field.getName();
    }
}
