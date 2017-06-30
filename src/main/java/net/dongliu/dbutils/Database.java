package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.UncheckedSQLException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Function;

/**
 * Wrap jdbc, to execute SQL queries.
 *
 * @see ResultSetHandler
 */
public class Database extends SQLExecutor {

    /**
     * The DataSource to retrieve connections from.
     */
    private final DataSource dataSource;

    /**
     * Constructor to provide a DataSource.
     */
    private Database(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    /**
     * Create a database from data source
     */
    public static Database create(DataSource dataSource) {
        return new Database(dataSource);
    }

    /**
     * Use internal non-pooled data source
     */
    public static Database create(String jdbcUrl, String user, String password) {
        return new Database(SimpleDataSource.create(jdbcUrl, user, password));
    }

    /**
     * Fetch a Connection object.
     * For transaction, connection is save by thread-local, just retrieve and return
     *
     * @return An initialized Connection.
     */
    private Connection retrieveConnection() {
        Connection connection;
        try {
            connection = this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
        return connection;
    }

    /**
     * Start a transaction, and return transaction context for executing sql.
     *
     * @return a transaction context to commit/rollback context
     */
    public TransactionContext startTransaction() {
        Connection connection = retrieveConnection();
        return new TransactionContext(connection);
    }

    /**
     * Start a transaction, execute sqls in function, and do commit after function finished,
     * or rollback if exception occurred
     */
    public <T> T withTransaction(Function<TransactionContext, T> function) {
        TransactionContext ctx = startTransaction();
        try {
            T result = function.apply(ctx);
            ctx.commit();
            return result;
        } catch (Throwable t) {
            ctx.rollback();
            throw t;
        }
    }

    @Override
    protected ConnectionInfo supplyConnection() {
        Connection connection = this.retrieveConnection();
        return new ConnectionInfo(connection, true);
    }
}
