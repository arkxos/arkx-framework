package io.arkx.framework;

import io.arkx.framework.commons.collection.Mapx;

/**
 * @author Nobody
 * @date 2025-06-04 20:35
 * @since 1.0
 */
public class CurrentData {

	public Mapx<String, Object> values = new Mapx<String, Object>();

	public void clear() {
		if (values != null) {
			if (values.getEntryTableLength() < 64) {
				values.clear();
			} else {
				values = new Mapx<>();
			}
		}
	}
}
