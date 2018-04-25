package net.dongliu.dbutils;

import static java.util.Objects.requireNonNull;

/**
 * Hold sql clause, and batch sql params
 */
public class BatchSQL {
    private final String clause;
    private final Object[][] params;

    public BatchSQL(String clause, Object[][] params) {
        this.clause = requireNonNull(clause);
        this.params = requireNonNull(params);
    }

    public String clause() {
        return clause;
    }

    public Object[][] params() {
        return params;
    }
}
