package com.rapidark.framework.core.castor;

/**
 * 类型转换器虚拟类
 * 
 */
public abstract class AbstractCastor implements ICastor {

	@Override
	public String getExtendItemID() {
		return this.getClass().getName();
	}

	@Override
	public String getExtendItemName() {
		return this.getClass().getName();
	}

}
