package net.dongliu.dbutils.mapping;

import net.dongliu.dbutils.exception.BeanMappingException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Property by getter/setter method
 */
public class GetterSetterProperty implements Property {
    private final PropertyDescriptor descriptor;

    public GetterSetterProperty(PropertyDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public void set(Object bean, Object value) {
        try {
            descriptor.getWriteMethod().invoke(bean, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanMappingException(e);
        }
    }

    @Override
    public Object get(Object bean) {
        try {
            return descriptor.getReadMethod().invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanMappingException(e);
        }
    }

    @Override
    public Class<?> type() {
        return descriptor.getPropertyType();
    }

    @Override
    public String name() {
        return descriptor.getName();
    }
}
