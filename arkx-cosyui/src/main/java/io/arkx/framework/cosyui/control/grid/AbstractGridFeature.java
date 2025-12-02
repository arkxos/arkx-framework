package io.arkx.framework.cosyui.control.grid;

import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.lang.FastStringBuilder;
import io.arkx.framework.cosyui.control.DataGridAction;
import io.arkx.framework.cosyui.html.HtmlTD;
import io.arkx.framework.cosyui.html.HtmlTR;
import io.arkx.framework.cosyui.template.AbstractExecuteContext;

/**
 * DataGrid特性虚拟类
 *
 */
public abstract class AbstractGridFeature {

	/**
	 * 重写整个标签体，在编译标签体时调用。
	 */
	public void rewriteBody(DataGridAction dga, DataGridBody body) {
	}

	/**
	 * 逐行重写， 在编译标签体时调用。
	 */
	public void rewriteTR(DataGridAction dga, HtmlTR tr) {
	}

	/**
	 * 逐个单元格重写，在编译标签时调用。第一个HtmlTD参数为首行td，第二个HtmlTD参数为模板行td
	 */
	public void rewriteTD(DataGridAction dga, HtmlTD th, HtmlTD td) {
	}

	/**
	 * 在数据绑定之前调用，可以改变DataTable中的数据，也可以往模板执行上下文中加入变量
	 */
	public void beforeDataBind(DataGridAction dga, AbstractExecuteContext context, DataTable dataSource) {
	}

	/**
	 * 在数据绑定之前准备script
	 */
	public void appendScript(DataGridAction dga, FastStringBuilder sb) {
	}

}
