package com.rapidark.framework.cosyui.template;

import com.rapidark.framework.cosyui.template.exception.TemplateRuntimeException;

/**
 * 模板命令接口
 * 
 */
public interface ITemplateCommand {
	int execute(AbstractExecuteContext context) throws TemplateRuntimeException;
}
