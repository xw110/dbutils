package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.UncheckedSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_FORWARD_ONLY;

/**
 * For query sql execute
 *
 * @author Liu Dong
 */
public class QuerySQLContext extends SingleSQLContext<QuerySQLContext> {
    QuerySQLContext(Connection connection, boolean closeConn) {
        super(connection, closeConn);
    }

    /**
     * Executes the SQL query and returns result.
     */
    public SQLResultSet execute() throws UncheckedSQLException {
        PreparedStatement statement;
        try {
            statement = fetchSize == 0 ? connection.prepareStatement(clause) :
                    // mysql need to set those flags to make fetch size work
                    connection.prepareStatement(clause, TYPE_FORWARD_ONLY, CONCUR_READ_ONLY);
        } catch (Throwable e) {
            close(connection, e);
            throw new RuntimeException("should not reach here");
        }

        try {
            fillStatement(statement, params());
            statement.setFetchSize(fetchSize);
            statement.execute();
            ResultSet rs = statement.getResultSet();
            return new SQLResultSet(rs, statement, connection);
        } catch (Throwable e) {
            close(statement, connection, e);
            throw new RuntimeException("should not reach here");
        }
    }

    @Override
    protected QuerySQLContext self() {
        return this;
    }


}
