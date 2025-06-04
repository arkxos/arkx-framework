package com.arkxos.framework;

import com.arkxos.framework.core.FrameworkException;

/**
 * 配置文件加载异常
 * 
 */
public class ConfigLoadException extends FrameworkException {

	private static final long serialVersionUID = 1L;

	public ConfigLoadException(String message) {
		super(message);
	}

}
