package io.arkx.framework.cosyui.template;

/**
 * 模板管理器接口
 */
public interface ITemplateManager {

    TemplateExecutor getExecutor(String file);

    boolean execute(String file, AbstractExecuteContext context);

}
