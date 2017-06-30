package net.dongliu.dbutils.mapping;

import javax.annotation.Nullable;

// Data class property
public interface Property {

    /**
     * Set the value of property
     */
    void set(Object bean, @Nullable Object value);

    /**
     * Get the value of property
     */
    @Nullable
    Object get(Object bean);

    /**
     * The type of this property
     */
    Class<?> type();

    /**
     * Get the name of property
     */
    String name();
}
