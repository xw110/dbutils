package net.dongliu.dbutils;

import net.dongliu.commons.collection.Lists;
import net.dongliu.dbutils.exception.ParameterNotFoundException;
import net.dongliu.dbutils.mapping.BeanMapping;
import net.dongliu.dbutils.mapping.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parse sql clause with named parameter. The name is prefixed by ':' char.
 */
class NamedSQLParser {

    static SQL translate(String clause, Map<String, ?> map) {
        ParseResult parseResult = parseClause(clause);
        Object[] params = getParameters(map, parseResult.paramNames);
        return new SQL(parseResult.clause, params);
    }


    static BatchSQL translate(String clause, List<Map<String, ?>> maps) {
        ParseResult result = parseClause(clause);

        List<Object[]> list = Lists.convert(maps, map -> getParameters(map, result.paramNames));
        return new BatchSQL(result.clause, list);
    }


    private static Object[] getParameters(Map<String, ?> map, List<String> names) {
        Object[] params = new Object[names.size()];
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            if (!map.containsKey(name)) {
                throw new ParameterNotFoundException(name);
            }
            params[i] = map.get(name);
        }
        return params;
    }


    static SQL translateBean(String clause, Object bean) {
        ParseResult parseResult = parseClause(clause);
        Object[] params = getParameters(bean, parseResult.paramNames);
        return new SQL(parseResult.clause, params);
    }

    static BatchSQL translateBean(String clause, List<?> beans) {
        ParseResult pair = parseClause(clause);

        List<Object[]> params = Lists.convert(beans, bean -> getParameters(bean, pair.paramNames));
        return new BatchSQL(pair.clause, params);
    }

    private static Object[] getParameters(Object bean, List<String> names) {
        BeanMapping beanMapping = BeanMapping.getBeanMapping(bean.getClass());
        Object[] params = new Object[names.size()];
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            Property property = beanMapping.getProperty(name);
            if (property == null) {
                throw new ParameterNotFoundException(name);
            }
            params[i] = property.get(bean);
        }
        return params;
    }

    private static final int NORMAL = 0;
    private static final int EXPECT_NAME = 1;
    private static final int IN_NAME = 2;

    static class ParseResult {
        private final String clause;
        private final List<String> paramNames;

        ParseResult(String clause, List<String> paramNames) {
            this.clause = clause;
            this.paramNames = paramNames;
        }

        public String clause() {
            return clause;
        }

        public List<String> paramNames() {
            return paramNames;
        }
    }

    static ParseResult parseClause(String clause) {
        int state = NORMAL;
        StringBuilder sb = new StringBuilder(clause.length());
        StringBuilder buffer = new StringBuilder();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < clause.length(); i++) {
            char c = clause.charAt(i);

            switch (state) {
                case NORMAL:
                    if (c == ':') {
                        state = EXPECT_NAME;
                    } else {
                        sb.append(c);
                    }
                    break;
                case EXPECT_NAME:
                    if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_') {
                        buffer.setLength(0);
                        buffer.append(c);
                        state = IN_NAME;
                    } else {
                        // illegal name character, just skip it?
                        sb.append(':').append(c);
                        state = NORMAL;
                    }
                    break;

                case IN_NAME:
                    if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                        names.add(buffer.toString());
                        sb.append("?").append(c);
                        state = NORMAL;
                    } else if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c >= '0' && c <= '9') {
                        buffer.append(c);
                    } else {
                        names.add(buffer.toString());
                        sb.append("?").append(c);
                        state = NORMAL;
                    }
            }

        }
        if (state == IN_NAME) {
            names.add(buffer.toString());
            sb.append("?");
        } else if (state == EXPECT_NAME) {
            sb.append(':');
        }
        return new ParseResult(sb.toString(), names);
    }
}
