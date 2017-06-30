package net.dongliu.dbutils;

import net.dongliu.dbutils.mapper.RowMapper;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Class for holding sql execute contexts, and row mapper.
 *
 * @author Liu Dong
 */
public abstract class TypedQueryContext<T> extends AbstractQueryContext {

    private final RowMapper<T> mapper;

    TypedQueryContext(RowMapper<T> mapper) {
        super();
        this.mapper = requireNonNull(mapper);
    }

    @Override
    public TypedQueryContext<T> keyColumns(String[] keyColumns) {
        super.keyColumns(keyColumns);
        return this;
    }

    @Override
    public TypedQueryContext<T> fetchSize(int fetchSize) {
        super.fetchSize(fetchSize);
        return this;
    }

    /**
     * Get ResultSet with only one row or no row as T.
     *
     * @throws net.dongliu.dbutils.exception.TooManyResultException if hava more than one row
     */
    public T get() {
        return convertTo(mapper);
    }

    /**
     * Get Result as list.
     */
    public List<T> getList() {
        return convertToList(mapper);
    }

    /**
     * Wrap ResultSet as Stream.
     * Need to close this stream if not consumed.
     */
    public Stream<T> asStream() {
        return asStream(mapper);
    }
}
