package io.arkx.framework.cosyui.template;

import io.arkx.framework.extend.IExtendItem;

/**
 * 模板源代码处理器接口，在模板编译前执行。<br>
 * 可以在解析模板之前先处理模板源代码中的资源路径。
 * 
 */
public interface ITemplateSourceProcessor extends IExtendItem {
	public void process(TemplateParser parser);
}
