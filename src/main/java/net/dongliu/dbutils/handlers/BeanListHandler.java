package net.dongliu.dbutils.handlers;

import java.util.Objects;

/**
 * ResultSetHandler implementation that converts a ResultSet into a List of beans.
 * This class is not thread safe.
 *
 * @param <T> the target bean type
 */
public class BeanListHandler<T> extends ListResultHandler<T> {
    /**
     * Creates a new instance of BeanListHandler.
     */
    private BeanListHandler(BeanMapper<T> beanRowProcessor) {
        super(beanRowProcessor);
    }

    /**
     * Get BeanListHandler instance
     */
    public static <T> BeanListHandler<T> getInstance(Class<T> cls) {
        BeanMapper<T> processor = new BeanMapper<>(Objects.requireNonNull(cls));
        return new BeanListHandler<>(processor);
    }
}
