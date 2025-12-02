package io.arkx.framework.cosyui.template;

import java.util.List;

import io.arkx.framework.cosyui.expression.IEvaluator;
import io.arkx.framework.cosyui.expression.IFunctionMapper;

/**
 * 模板管理上下文接口，用于提供标签/模板类型/修饰符的注册入口，并管理模板路径。
 *
 */
public interface ITemplateManagerContext {

    /**
     * 返回所有源代码处理器
     */
    List<ITemplateSourceProcessor> getSourceProcessors();

    /**
     * 返回所有标签
     */
    List<? extends AbstractTag> getTags();

    /**
     * 返回指定ID的标签
     */
    AbstractTag getTag(String prefix, String tagName);

    /**
     * 返回指定标签的新实例
     */
    AbstractTag createNewTagInstance(String prefix, String tagName);

    /**
     * @return 获取模板管理器
     */
    ITemplateManager getTemplateManager();

    /**
     * 返回IFunctionMapper实例
     */
    abstract IFunctionMapper getFunctionMapper();

    /**
     * 返回表达式求值器实例
     */
    abstract IEvaluator getEvaluator();
}
