package net.dongliu.dbutils;

import net.dongliu.commons.collection.Lists;
import net.dongliu.commons.collection.Maps;
import net.dongliu.dbutils.NamedSQLParser.ParseResult;
import net.dongliu.dbutils.mock.Student;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class NamedSQLParserTest {
    @Test
    public void translate() {
        SQL sql = NamedSQLParser.translate("select * from table where name=:name and age>:age",
                new HashMap<String, Object>() {{
                    put("name", "Jack");
                    put("age", 10);
                }});
        assertEquals("select * from table where name=? and age>?", sql.clause());
        assertArrayEquals(new Object[]{"Jack", 10}, sql.params());

        BatchSQL batchSQL = NamedSQLParser.translate("select * from table where name=:name and age>:age",
                Lists.of(
                        Maps.of("name", "Jack", "age", 10),
                        Maps.of("name", "Marry", "age", 11)
                ));
        assertEquals("select * from table where name=? and age>?", batchSQL.clause());
        assertArrayEquals(new Object[]{"Jack", 10}, batchSQL.params().get(0));
        assertArrayEquals(new Object[]{"Marry", 11}, batchSQL.params().get(1));
    }

    @Test
    public void translateBean() {
        Student student1 = new Student();
        student1.setName("Jack");
        student1.setAge(10);
        SQL sql = NamedSQLParser.translateBean("select * from table where name=:name and age>:age", student1);
        assertEquals("select * from table where name=? and age>?", sql.clause());
        assertArrayEquals(new Object[]{"Jack", 10}, sql.params());

        Student student2 = new Student();
        student2.setName("Marry");
        student2.setAge(11);
        BatchSQL batchSQL = NamedSQLParser.translateBean("select * from table where name=:name and age>:age",
                Lists.of(student1, student2));
        assertEquals("select * from table where name=? and age>?", batchSQL.clause());
        assertArrayEquals(new Object[]{"Jack", 10}, batchSQL.params().get(0));
        assertArrayEquals(new Object[]{"Marry", 11}, batchSQL.params().get(1));


        SQL sql2 = NamedSQLParser.translateBean(
                "insert into student(name, age, is_male, birth_day) values(:name,:age,:isMale,:birthDay)",
                student1);
        assertEquals("insert into student(name, age, is_male, birth_day) values(?,?,?,?)", sql2.clause());
        assertArrayEquals(new Object[]{"Jack", 10, false, null}, sql2.params());
    }

    @Test
    public void parseClause() {
        ParseResult parseResult = NamedSQLParser.parseClause("select * from table where name=:name and age>:age");
        assertEquals("select * from table where name=? and age>?", parseResult.clause());
        assertEquals(Arrays.asList("name", "age"), parseResult.paramNames());
    }

}