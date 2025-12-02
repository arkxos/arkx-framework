package io.arkx.framework.commons.collection.tree;

/**
 * @author Nobody
 * @date 2025-05-19 0:00
 * @since 1.0
 */
public interface TreeNodeData<K> {

	K getId();

	K getParentId();

	String getName();

	// String getPath();

	default int getSortOrder() {
		return 0;
	}

}
