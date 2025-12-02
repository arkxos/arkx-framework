package io.arkx.framework.data.db.sdk.api;

/**
 * @author Nobody
 * @version 1.0
 * @date 2025-10-03 21:05
 * @since 1.0
 */
public interface ColumnValueTransformer {

	void transform(String columnName, Object[] originalResult, int i);

}
