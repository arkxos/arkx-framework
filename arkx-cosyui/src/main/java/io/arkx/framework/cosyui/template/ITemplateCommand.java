package io.arkx.framework.cosyui.template;

import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;

/**
 * 模板命令接口
 *
 */
public interface ITemplateCommand {

	int execute(AbstractExecuteContext context) throws TemplateRuntimeException;

}
