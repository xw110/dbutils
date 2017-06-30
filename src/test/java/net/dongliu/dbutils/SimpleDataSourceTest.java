package net.dongliu.dbutils;

import org.junit.Test;

import javax.sql.DataSource;

import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.Assert.*;

public class SimpleDataSourceTest {

    @Test
    public void testCreate() throws Exception {
        String jdbcUrl = "jdbc:derby:memory:derbyDB;create=true";
        DataSource dataSource = SimpleDataSource.create(jdbcUrl, null, null);
        Connection connection = dataSource.getConnection();
        assertNotNull(connection);
        assertTrue(connection.getAutoCommit());
        connection.close();

        try {
            DriverManager.getConnection("jdbc:derby:memory:derbyDB;drop=true");
        } catch (Exception e) {
        }
    }
}