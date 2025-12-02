package io.arkx.framework.annotation.util;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.thread.LongTimeTask;

public interface ExcelTask {

	void setTask(LongTimeTask longTimeTask);

	void execute(String fileName, Mapx<String, Object> params);

}
