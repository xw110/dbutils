package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.UncheckedSQLException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Liu Dong
 */
public class ResultSets {

    /**
     * Wrap resultSet as stream. You need consume all items or close this stream manually
     */
    public static <T> Stream<T> asStream(ResultSet resultSet, RowMapper<T> processor, Statement statementToClose,
                                         Connection connectionToClose) {
        ResultSetIterator<T> iterator = new ResultSetIterator<>(resultSet, processor, statementToClose,
                connectionToClose);
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        Stream<T> stream = StreamSupport.stream(spliterator, false);
        return stream.onClose(iterator::close);
    }

    /**
     * Wrap jdbc result set as iterator
     */
    public static class ResultSetIterator<T> implements java.util.Iterator<T>, AutoCloseable {
        private final ResultSet resultSet;
        private final RowMapper<T> processor;
        private final Statement statementToClose;
        private final Connection connectionToClose;
        int row = 0;
        boolean hasNext;
        boolean remain;
        boolean closed;

        public ResultSetIterator(ResultSet resultSet, RowMapper<T> processor, Statement statementToClose,
                                 Connection connectionToClose) {
            this.resultSet = resultSet;
            this.processor = processor;
            this.statementToClose = statementToClose;
            this.connectionToClose = connectionToClose;
        }

        @Override
        public boolean hasNext() {
            if (!remain) {
                inspectNext();
            }
            return hasNext;
        }

        public void inspectNext() {
            try {
                hasNext = resultSet.next();
                if (!hasNext) {
                    close();
                }
                remain = true;
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            row++;
            try {
                T value = processor.convert(resultSet, row);
                remain = false;
                return value;
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        }

        @Override
        public void close() throws UncheckedSQLException {
            if (closed) {
                return;
            }
            try (ResultSet r = resultSet; Statement s = statementToClose; Connection n = connectionToClose) {
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            } finally {
                closed = true;
            }
        }
    }
}
