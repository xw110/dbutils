package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.ParameterNotFoundException;
import net.dongliu.dbutils.mapping.BeanMapping;
import net.dongliu.dbutils.mapping.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Liu Dong
 */
public class NamedSQLParser {

    static SQL translate(String clause, Map<String, ?> map) {
        ParseResult parseResult = parseClause(clause);
        Object[] params = getParameters(map, parseResult.paramNames);
        return new SQL(parseResult.clause, params);
    }


    static BatchSQL translate(String clause, List<? extends Map<String, ?>> mapList) {
        ParseResult pair = parseClause(clause);

        Object[][] paramsArray = new Object[mapList.size()][];
        for (int i = 0; i < mapList.size(); i++) {
            Map<String, ?> map = mapList.get(i);
            paramsArray[i] = getParameters(map, pair.paramNames);
        }

        return new BatchSQL(pair.clause, paramsArray);
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
        ParseResult pair = parseClause(clause);
        Object[] params = getParameters(bean, pair.paramNames);
        return new SQL(pair.clause, params);
    }

    static BatchSQL translateBean(String clause, List<?> beanList) {
        ParseResult pair = parseClause(clause);

        Object[][] paramsArray = new Object[beanList.size()][];
        for (int i = 0; i < beanList.size(); i++) {
            Object bean = beanList.get(i);
            paramsArray[i] = getParameters(bean, pair.paramNames);
        }

        return new BatchSQL(pair.clause, paramsArray);
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
                    if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'z' || c == '_') {
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
                    } else if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'z' || c == '_' || c >= '0' && c <= '9') {
                        buffer.append(c);
                    } else {
                        // illegal name character, just skip it?
                        sb.append(buffer);
                        sb.append(c);
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
