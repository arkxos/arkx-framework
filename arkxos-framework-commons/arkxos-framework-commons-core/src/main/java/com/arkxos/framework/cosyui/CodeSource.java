package com.arkxos.framework.cosyui;

import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.collection.Mapx;

/**
 * 代码来源虚拟类
 * 每个项目需要有一个类实现CodeSource,并将类名配置于framework.xml
 */
public abstract class CodeSource {
	public abstract DataTable getCodeData(String codeType, Mapx<String, Object> params);
}
