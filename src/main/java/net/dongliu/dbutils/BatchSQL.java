package net.dongliu.dbutils;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Hold sql clause, and batch sql params
 */
public class BatchSQL {
    private final String clause;
    private final List<Object[]> params;

    public BatchSQL(String clause, List<Object[]> params) {
        this.clause = requireNonNull(clause);
        this.params = requireNonNull(params);
    }

    public String clause() {
        return clause;
    }

    public List<Object[]> params() {
        return params;
    }
}
