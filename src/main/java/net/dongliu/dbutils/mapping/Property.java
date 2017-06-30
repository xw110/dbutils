package net.dongliu.dbutils.mapping;


/**
 * Data class property.
 * Implementations should not contains strong reference to The Class object, cause we cache this in weak map.
 */

public interface Property {

    /**
     * Set the value of property
     */
    void set(Object bean, Object value);

    /**
     * Get the value of property
     */
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
