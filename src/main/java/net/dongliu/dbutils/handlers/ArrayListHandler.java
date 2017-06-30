package net.dongliu.dbutils.handlers;

import javax.annotation.concurrent.Immutable;

/**
 * ResultSetHandler implementation that converts the
 * ResultSet into a List of Object[]s.
 * This class is thread safe.
 */
@Immutable
public class ArrayListHandler extends ListResultHandler<Object[]> {

    private static final ArrayListHandler instance = new ArrayListHandler();

    private ArrayListHandler() {
        super(ArrayMapper.getInstance());
    }

    public static ArrayListHandler getInstance() {
        return instance;
    }

}
