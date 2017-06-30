package net.dongliu.dbutils;

import static java.util.Objects.requireNonNull;

/**
 * Hold sql clause and params
 */
public class SQL {
    private final String clause;
    private final Object[] params;
    public final static Object[] emptyParams = {};

    public SQL(String clause, Object[] params) {
        this.clause = requireNonNull(clause);
        this.params = requireNonNull(params);
    }

    public SQL(String clause) {
        this(clause, emptyParams);
    }

    public String clause() {
        return clause;
    }

    public Object[] params() {
        return params;
    }
}
