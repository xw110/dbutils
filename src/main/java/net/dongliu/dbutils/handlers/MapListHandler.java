package net.dongliu.dbutils.handlers;

import java.util.Map;

/**
 * ResultSetHandler implementation that converts a ResultSet into a List of Maps.
 * This class is not thread safe.
 */
public class MapListHandler extends ListResultHandler<Map<String, Object>> {

    public static MapListHandler getInstance() {
        return new MapListHandler();
    }

    /**
     * Creates a new instance of MapHandler using a
     * BasicRowProcessor for conversion.
     */
    private MapListHandler() {
        super(new MapMapper());
    }

}
