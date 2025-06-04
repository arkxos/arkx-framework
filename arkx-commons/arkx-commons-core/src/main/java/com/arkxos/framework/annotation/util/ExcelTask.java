package com.arkxos.framework.annotation.util;

import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.cosyui.control.LongTimeTask;

public interface ExcelTask {

	void setTask(LongTimeTask longTimeTask);

	void execute(String fileName, Mapx<String, Object> params);

}
