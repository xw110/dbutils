package net.dongliu.dbutils.handlers;

import java.util.Map;

/**
 * ResultSetHandler implementation that converts the first ResultSet row into a Map.
 * This class is not thread safe.
 */
public class MapHandler extends SingleResultHandler<Map<String, Object>> {

    public static MapHandler getInstance() {
        return new MapHandler();
    }

    /**
     * Creates a new instance of MapHandler using a
     * BasicRowProcessor for conversion.
     */
    private MapHandler() {
        super(new MapMapper());
    }
}
