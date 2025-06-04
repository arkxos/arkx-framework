package com.arkxos.framework.extend;

/**
 * 扩展行为接口<br>
 * @author Darkness
 * @date 2012-8-7 下午9:24:58
 * @version V1.0
 */
public interface IExtendAction {
	/**
	 * 扩展逻辑
	 */
	Object execute(Object[] args) throws ExtendException;

	/**
	 * 是否可用
	 */
	boolean isUsable();
}
