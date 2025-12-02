package io.arkx.framework.cosyui.zhtml;

import java.util.concurrent.locks.ReentrantLock;

import io.arkx.framework.cosyui.control.*;
import io.arkx.framework.cosyui.tag.*;
import io.arkx.framework.cosyui.template.AbstractTag;
import io.arkx.framework.extend.AbstractExtendService;
import io.arkx.framework.extend.action.ExtendTag;
import io.arkx.framework.i18n.LangButtonTag;
import io.arkx.framework.i18n.LangTag;
import io.arkx.framework.security.PrivTag;

/**
 * Zhtml标签扩展服务 zhtml.io.arkx.framework.cosyui.ZhtmlTagService
 */
public class ZhtmlTagService extends AbstractExtendService<AbstractTag> {
    private static ZhtmlTagService instance;
    private static ReentrantLock lock = new ReentrantLock();

    public static ZhtmlTagService getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    ZhtmlTagService tmp = AbstractExtendService.findInstance(ZhtmlTagService.class);
                    tmp.register(new DataGridTag());
                    tmp.register(new DataListTag());
                    tmp.register(new InitTag());
                    tmp.register(new PageBarTag());
                    tmp.register(new ButtonTag());
                    tmp.register(new PanelHeaderTag());
                    tmp.register(new ChildTabTag());
                    tmp.register(new TabTag());
                    tmp.register(new TreeTag());
                    tmp.register(new MenuTag());
                    tmp.register(new SelectTag());
                    tmp.register(new UploaderTag());
                    tmp.register(new ListTag());
                    tmp.register(new RadioTag());
                    tmp.register(new CheckboxTag());
                    tmp.register(new ParamTag());
                    tmp.register(new IncludeTag());
                    tmp.register(new IfTag());
                    tmp.register(new ElseTag());
                    tmp.register(new BreakTag());
                    tmp.register(new ChooseTag());
                    tmp.register(new WhenTag());
                    tmp.register(new ToolBarTag());
                    tmp.register(new ScrollPanelTag());
                    tmp.register(new ActionTag());
                    tmp.register(new ExtendTag());
                    tmp.register(new PrivTag());
                    tmp.register(new EvalTag());
                    tmp.register(new ForTag());
                    tmp.register(new SetTag());
                    tmp.register(new LangTag());
                    tmp.register(new LangButtonTag());
                    tmp.register(new SliderTag());
                    tmp.register(new ForEachTag());
                    tmp.register(new SubTag());
                    tmp.register(new InvokeTag());
                    tmp.register(new ConfigTag());
                    tmp.register(new ElseIfTag());
                    instance = tmp;
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }
}
