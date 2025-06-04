package com.arkxos.framework.cosyui;

import java.util.concurrent.locks.ReentrantLock;

import com.arkxos.framework.annotation.Priv;
import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.util.LogUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.config.CodeSourceClass;
import com.arkxos.framework.core.method.IMethodLocator;
import com.arkxos.framework.core.method.MethodLocatorUtil;
import com.arkxos.framework.cosyui.web.UIFacade;
import com.arkxos.framework.security.PrivCheck;

/**
 * 下拉框代码UI类，响应前端JS中的下拉框loadData请求
 */
public class CodeSourceUI extends UIFacade {
	private static CodeSource codeSourceInstance;
	private static ReentrantLock lock = new ReentrantLock();

	@Priv(login = false)
	public void getData(String codeType, String conditionField, String method) {
		if (StringUtil.isEmpty(conditionField)) {
			Request.put("ConditionField", "1");
			Request.put("ConditionValue", "1");
		}
		DataTable dt = null;
		if (StringUtil.isEmpty(method) && codeType.startsWith("#")) {
			method = codeType.substring(1);
		}
		if (StringUtil.isEmpty(method) && codeType.indexOf(".")!=-1) {
			method = codeType;
		}
		if (StringUtil.isNotEmpty(method)) {
			try {
				IMethodLocator m = MethodLocatorUtil.find(method);
				PrivCheck.check(m);
				Object o = m.execute();
				dt = (DataTable) o;
			} catch (Exception e) {
				e.printStackTrace();
				throw new UIException(method + " must return DataTable");
			}
		} else {
			CodeSource cs = getCodeSourceInstance();
			dt = cs.getCodeData(codeType, Request);
		}
		$S("DataTable", dt);
	}

	public static void initCodeSource() {
		if (codeSourceInstance == null) {
			lock.lock();
			try {
				if (codeSourceInstance == null) {
					String className = CodeSourceClass.getValue();
					if (StringUtil.isEmpty(className)) {
						LogUtil.warn("CodeSource class not found");
						return;
					}
					try {
						Class<?> c = Class.forName(className);
						Object o = c.newInstance();
						codeSourceInstance = (CodeSource) o;
					} catch (Exception e) {
						e.printStackTrace();
						throw new UIException("Load CodeSource class failed:" + e.getMessage());
					}
				}
			} finally {
				lock.unlock();
			}
		}
	}

	public static CodeSource getCodeSourceInstance() {
		initCodeSource();
		return codeSourceInstance;
	}

}
