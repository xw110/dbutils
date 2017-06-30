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
public class BatchInsertSQLContext extends BatchSQLContext<BatchInsertSQLContext> {
    BatchInsertSQLContext(Connection connection, boolean autoClose) {
        super(connection, autoClose);
    }

    @Override
    protected BatchInsertSQLContext self() {
        return this;
    }

    /**
     * Execute a batch of INSERT operations, and return keys.
     *
     * @return The key column result set of inserted row. will try retrieve auto generated columns
     */
    public SQLResultSet execute() {
        PreparedStatement statement;
        try {
            statement = keyColumns == null ? connection.prepareStatement(clause, Statement.RETURN_GENERATED_KEYS)
                    : connection.prepareStatement(clause, keyColumns);
        } catch (Throwable e) {
            close(connection, e);
            throw new RuntimeException("should not reach here");
        }
        try {
            for (Object[] param : params()) {
                this.fillStatement(statement, param);
                statement.addBatch();
            }
            statement.executeBatch();
            ResultSet rs = statement.getGeneratedKeys();
            return new SQLResultSet(rs, statement, connection);
        } catch (Throwable e) {
            close(statement, connection, e);
            throw new RuntimeException("should not reach here");
        }

    }
}
