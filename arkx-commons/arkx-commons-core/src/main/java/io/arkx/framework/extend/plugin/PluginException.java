package io.arkx.framework.extend.plugin;

/**
 * @class org.ark.framework.extend.plugin.PluginException
 * 插件异常
 * @private
 * @author Darkness
 * @date 2012-8-7 下午9:23:36
 * @version V1.0
 */
public class PluginException extends Exception {
	private static final long serialVersionUID = 1L;

	private String message;

	public PluginException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
