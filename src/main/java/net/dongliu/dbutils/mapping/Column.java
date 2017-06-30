package net.dongliu.dbutils.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark column name of bean property, when map result set to bean.
 * This annotation can be set on field, getter or setter.
 * If multi annotations are set for one property, which one is used is undetermined.
 *
 * @author Liu Dong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface Column {
    /**
     * The mapping column name of resultSet
     */
    String value();
}
