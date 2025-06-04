package io.arkx.framework.data.fasttable;

import java.nio.ByteBuffer;

/**
 * converte record buffer to R
 * @author darkness
 *
 * @param <R>
 */
@FunctionalInterface
public interface RecordFunction<R> {

	/**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    R apply(ByteBuffer record);
    
}
