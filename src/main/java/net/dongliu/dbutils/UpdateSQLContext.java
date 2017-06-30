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
public class UpdateSQLContext extends SingleSQLContext<UpdateSQLContext> {
    UpdateSQLContext(Connection connection, boolean closeConn) {
        super(connection, closeConn);
    }

    @Override
    protected UpdateSQLContext self() {
        return this;
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement.
     *
     * @return The number of rows updated.
     */
    public int execute() {
        try (Connection c = connection; PreparedStatement stmt = connection.prepareStatement(clause)) {
            fillStatement(stmt, params());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }
}
