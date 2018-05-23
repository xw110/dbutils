package net.dongliu.dbutils;

import net.dongliu.dbutils.exception.TooManyResultException;
import net.dongliu.dbutils.mapper.RowMapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Class for holding sql execute contexts, and row mapper.
 *
 * @author Liu Dong
 */
public abstract class TypedQueryContext<T> extends AbstractQueryContext<TypedQueryContext<T>> {

    private final RowMapper<T> mapper;

    TypedQueryContext(RowMapper<T> mapper) {
        super();
        this.mapper = requireNonNull(mapper);
    }

    /**
     * Get ResultSet with only one row.
     *
     * @return null if row not exist
     * @throws TooManyResultException if hava more than one row
     */
    @Nullable
    public T getOne() throws TooManyResultException {
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
