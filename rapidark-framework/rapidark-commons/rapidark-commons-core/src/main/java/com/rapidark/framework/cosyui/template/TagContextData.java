package com.rapidark.framework.cosyui.template;

import com.rapidark.framework.commons.collection.DataRow;
import com.rapidark.framework.cosyui.expression.ITagData;
import com.rapidark.framework.cosyui.tag.IListTag;

/**
 * 标签上下文数据
 * 
 */
public class TagContextData implements ITagData {
	AbstractExecuteContext context;
	AbstractTag tag;
	private boolean found;

	public void init(AbstractExecuteContext context, AbstractTag tag) {
		this.context = context;
		this.tag = tag;
		found = false;
	}

	@Override
	public ITagData getParent() {
		if (tag == null || tag.getParent() == null) {
			return null;
		}
		return tag.getParent().getTagContextData();
	}

	@Override
	public Object getValue(String var) {
		if (tag != null) {
			if (tag instanceof IListTag) {
				DataRow dr = ((IListTag) tag).getCurrentDataRow();
				if (dr != null && dr.getDataColumn(var) != null) {// 如果有字段，则null值也返回
					found = true;// 对于List标签中存在null值的情况，应告诉外层循环结束查找
					return dr.get(var);
				}
			}
			Object v = tag.getVariable(var);
			if (v != null) {
				return v;
			} else {
				found = tag.getVariables().containsKey(var);
				return null;
			}
		}
		return null;
	}

	@Override
	public boolean isFound() {
		return found;
	}
}
