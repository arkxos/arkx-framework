package io.arkx.framework.data.db.core.provider.transform;

import io.arkx.framework.data.db.core.util.ColumHandler;

public class ColumTypeProcessor {

	private final ColumHandler handler;

	public ColumTypeProcessor(ColumHandler handler) {
		this.handler = handler;
	}

	public void process(Object[] originalResult, int index) {
		handler.handle(originalResult, index);
	}

}
