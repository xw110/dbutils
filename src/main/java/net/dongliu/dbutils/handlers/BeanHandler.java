package net.dongliu.dbutils.handlers;


import java.util.Objects;

/**
 * ResultSetHandler implementation that converts the first ResultSet row into a JavaBean.
 * This class is not thread safe.
 *
 * @param <T> the target bean type
 */
public class BeanHandler<T> extends SingleResultHandler<T> {

    /**
     * Creates a new instance of BeanHandler.
     */
    private BeanHandler(BeanMapper<T> beanRowProcessor) {
        super(beanRowProcessor);
    }

    /**
     * Get BeanListHandler instance
     */
    public static <T> BeanHandler<T> getInstance(Class<T> cls) {
        BeanMapper<T> processor = new BeanMapper<>(Objects.requireNonNull(cls));
        return new BeanHandler<>(processor);
    }
}
