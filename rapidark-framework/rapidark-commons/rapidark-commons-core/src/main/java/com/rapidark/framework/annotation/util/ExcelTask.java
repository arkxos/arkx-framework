package com.rapidark.framework.annotation.util;

import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.cosyui.control.LongTimeTask;

public interface ExcelTask {

	void setTask(LongTimeTask longTimeTask);

	void execute(String fileName, Mapx<String, Object> params);

}
