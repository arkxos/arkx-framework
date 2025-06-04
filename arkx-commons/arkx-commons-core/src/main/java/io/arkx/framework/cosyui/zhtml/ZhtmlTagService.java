package io.arkx.framework.cosyui.zhtml;

import java.util.concurrent.locks.ReentrantLock;

import io.arkx.framework.cosyui.control.ButtonTag;
import io.arkx.framework.cosyui.control.CheckboxTag;
import io.arkx.framework.cosyui.control.ChildTabTag;
import io.arkx.framework.cosyui.control.DataGridTag;
import io.arkx.framework.cosyui.control.DataListTag;
import io.arkx.framework.cosyui.control.MenuTag;
import io.arkx.framework.cosyui.control.PageBarTag;
import io.arkx.framework.cosyui.control.PanelHeaderTag;
import io.arkx.framework.cosyui.control.RadioTag;
import io.arkx.framework.cosyui.control.ScrollPanelTag;
import io.arkx.framework.cosyui.control.SelectTag;
import io.arkx.framework.cosyui.control.SliderTag;
import io.arkx.framework.cosyui.control.TabTag;
import io.arkx.framework.cosyui.control.ToolBarTag;
import io.arkx.framework.cosyui.control.TreeTag;
import io.arkx.framework.cosyui.control.UploaderTag;
import io.arkx.framework.cosyui.tag.ActionTag;
import io.arkx.framework.cosyui.tag.BreakTag;
import io.arkx.framework.cosyui.tag.ChooseTag;
import io.arkx.framework.cosyui.tag.ConfigTag;
import io.arkx.framework.cosyui.tag.ElseIfTag;
import io.arkx.framework.cosyui.tag.ElseTag;
import io.arkx.framework.cosyui.tag.EvalTag;
import io.arkx.framework.cosyui.tag.ForEachTag;
import io.arkx.framework.cosyui.tag.ForTag;
import io.arkx.framework.cosyui.tag.IfTag;
import io.arkx.framework.cosyui.tag.IncludeTag;
import io.arkx.framework.cosyui.tag.InitTag;
import io.arkx.framework.cosyui.tag.InvokeTag;
import io.arkx.framework.cosyui.tag.ListTag;
import io.arkx.framework.cosyui.tag.ParamTag;
import io.arkx.framework.cosyui.tag.SetTag;
import io.arkx.framework.cosyui.tag.SubTag;
import io.arkx.framework.cosyui.tag.WhenTag;
import io.arkx.framework.cosyui.template.AbstractTag;
import io.arkx.framework.extend.AbstractExtendService;
import io.arkx.framework.extend.action.ExtendTag;
import com.arkxos.framework.i18n.LangButtonTag;
import com.arkxos.framework.i18n.LangTag;
import com.arkxos.framework.security.PrivTag;

/**
 * Zhtml标签扩展服务
 * zhtml.io.arkx.framework.cosyui.ZhtmlTagService
 */
public class ZhtmlTagService extends AbstractExtendService<AbstractTag> {
	private static ZhtmlTagService instance;
	private static ReentrantLock lock = new ReentrantLock();

	public static ZhtmlTagService getInstance() {
		if (instance == null) {
			lock.lock();
			try {
				if (instance == null) {
					ZhtmlTagService tmp = findInstance(ZhtmlTagService.class);
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
