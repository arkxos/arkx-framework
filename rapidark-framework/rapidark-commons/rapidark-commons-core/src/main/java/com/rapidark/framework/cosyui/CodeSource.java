package com.rapidark.framework.cosyui;

import com.rapidark.framework.commons.collection.DataTable;
import com.rapidark.framework.commons.collection.Mapx;

/**
 * 代码来源虚拟类
 * 每个项目需要有一个类实现CodeSource,并将类名配置于framework.xml
 */
public abstract class CodeSource {
	public abstract DataTable getCodeData(String codeType, Mapx<String, Object> params);
}
