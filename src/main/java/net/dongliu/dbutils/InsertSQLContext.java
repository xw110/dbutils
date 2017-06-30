package net.dongliu.dbutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * For query sql execute
 *
 * @author Liu Dong
 */
public class InsertSQLContext extends SingleSQLContext<InsertSQLContext> {
    InsertSQLContext(Connection connection, boolean closeConn) {
        super(connection, closeConn);
    }

    @Override
    protected InsertSQLContext self() {
        return this;
    }

    public SQLResultSet execute() {
        PreparedStatement statement;
        try {
            statement = keyColumns == null ? connection.prepareStatement(clause, Statement.RETURN_GENERATED_KEYS)
                    : connection.prepareStatement(clause, keyColumns);
        } catch (Throwable t) {
            close(connection, t);
            throw new RuntimeException("should not reach here");
        }
        try {
            this.fillStatement(statement, params());
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            return new SQLResultSet(rs, statement, connection);
        } catch (Throwable e) {
            close(statement, connection, e);
            throw new RuntimeException("should not reach here");
        }
    }
}
