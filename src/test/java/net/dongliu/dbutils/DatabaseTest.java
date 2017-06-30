package net.dongliu.dbutils;

import net.dongliu.dbutils.mock.Student;
import org.junit.Test;

import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class DatabaseTest {

    @Test
    public void testDatabase() {
        String jdbcUrl = "jdbc:derby:memory:derbyDB;create=true";
        Database database = Database.create(jdbcUrl, null, null);
        database.update("create table student(" +
                "id bigint not null GENERATED ALWAYS AS IDENTITY CONSTRAINT PEOPLE_PK PRIMARY KEY, " +
                "name varchar(50) not null," +
                "age int not null," +
                "is_male boolean not null," +
                "birth_day date not null" +
                ")");

        // update(insert)
        int count = database.update("insert into student(name, age, is_male, birth_day) values(?,?,?,?)",
                "Jack", 10, true, LocalDate.of(1999, 1, 2));
        assertEquals(1, count);

        Record record = database.query("select * from student fetch first 1 rows only").get();
        assertArrayEquals(new Object[]{1L, "Jack", 10, true, java.sql.Date.valueOf(LocalDate.of(1999, 1, 2))},
                record.getValues());

        record = database.query("select * from student fetch first 1 rows only").get();
        assertNotNull(record);
        assertEquals("Jack", record.getString("name"));

        Student student = database.query("select id, name, age, is_male, birth_day from student where id=?", 1L)
                .map(Student.class).get();
        assertNotNull(student);
        Student s = new Student(1, "Jack", 10, true, LocalDate.of(1999, 1, 2));
        assertEquals(s, student);

        List<Student> students = database.query("select * from student").map(Student.class).getList();
        assertEquals(Collections.singletonList(s), students);

        // update(delete)
        int deleted = database.update("delete from student");
        assertEquals(1, deleted);

        // insert
        Long id = database.insertNamed(
                "insert into student(name, age, is_male, birth_day) values(:name,:age,:isMale,:birthDay)",
                student).map((p, rs) -> rs.getLong(1)).get();
        assertNotNull(id);
        assertEquals(2, id.longValue());

        try {
            DriverManager.getConnection("jdbc:derby:memory:derbyDB;drop=true");
        } catch (Exception e) {
        }
    }
}