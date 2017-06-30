package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.UncheckedSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * For query sql execute
 *
 * @author Liu Dong
 */
public class BatchUpdateSQLContext extends BatchSQLContext<BatchUpdateSQLContext> {
    BatchUpdateSQLContext(Connection connection, boolean closeConn) {
        super(connection, closeConn);
    }

    @Override
    protected BatchUpdateSQLContext self() {
        return this;
    }


    /**
     * Execute a batch of SQL INSERT, UPDATE, DELETE queries.
     *
     * @return The number of rows updated per statement.
     */
    public int[] execute() {
        try (Connection c = connection; PreparedStatement stmt = connection.prepareStatement(clause)) {
            for (Object[] param : params()) {
                fillStatement(stmt, param);
                stmt.addBatch();
            }
            return stmt.executeBatch();
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }


}
