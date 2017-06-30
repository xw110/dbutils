package net.dongliu.dbutils;

public class SQL {
    private final String clause;
    private final Object[] params;

    public SQL(String clause, Object[] params) {
        this.clause = clause;
        this.params = params;
    }

    public String clause() {
        return clause;
    }

    public Object[] params() {
        return params;
    }
}
