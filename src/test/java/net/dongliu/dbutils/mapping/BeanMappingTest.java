package net.dongliu.dbutils.mapping;

import net.dongliu.dbutils.mock.Student;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class BeanMappingTest {
    @Test
    public void getBeanMapping() throws Exception {
        Object bean = new Student();
        BeanMapping beanMapping = BeanMapping.getBeanMapping(bean.getClass());
        Property age = beanMapping.getProperty("age");
        assertNotNull(age);
        assertEquals(0, age.get(bean));
        age.set(bean, 10);
        assertEquals(10, age.get(bean));

        Property name = beanMapping.getProperty("name");
        assertNotNull(name);
        assertNull(name.get(bean));
        name.set(bean, "Jack");
        assertEquals("Jack", name.get(bean));

        Property isMale = beanMapping.getProperty("isMale");
        assertNotNull(isMale);
        assertEquals(false, isMale.get(bean));

        Property birthDay = beanMapping.getProperty("birthDay");
        assertNotNull(birthDay);
        birthDay.set(bean, LocalDate.of(1999, 1, 2));
        assertEquals(LocalDate.of(1999, 1, 2), birthDay.get(bean));
    }

}