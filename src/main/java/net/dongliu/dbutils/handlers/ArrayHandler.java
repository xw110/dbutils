package net.dongliu.dbutils.handlers;


import javax.annotation.concurrent.Immutable;

/**
 * ResultSetHandler implementation that converts a
 * ResultSet into an Object[]. This class is
 * thread safe.
 */

@Immutable
public class ArrayHandler extends SingleResultHandler<Object[]> {
    private static final ArrayHandler instance = new ArrayHandler();

    private ArrayHandler() {
        super(new ArrayMapper());
    }

    public static ArrayHandler getInstance() {
        return instance;
    }
}
