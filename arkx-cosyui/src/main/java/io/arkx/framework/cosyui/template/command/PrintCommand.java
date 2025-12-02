package io.arkx.framework.cosyui.template.command;

import io.arkx.framework.cosyui.template.AbstractExecuteContext;
import io.arkx.framework.cosyui.template.AbstractTag;
import io.arkx.framework.cosyui.template.ITemplateCommand;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;

/**
 * 原文输出命令
 *
 */
public class PrintCommand implements ITemplateCommand {
    private String str;

    public PrintCommand(String str) {
        this.str = str;
    }

    @Override
    public int execute(AbstractExecuteContext context) throws TemplateRuntimeException {
        context.getOut().write(str);
        return AbstractTag.EVAL_PAGE;
    }

    @Override
    public String toString() {
        return super.toString() + str;
    }

}
