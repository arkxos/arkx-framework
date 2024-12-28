package com.rapidark.framework.cosyui.zhtml;

import java.util.concurrent.locks.ReentrantLock;

import com.rapidark.framework.cosyui.control.ButtonTag;
import com.rapidark.framework.cosyui.control.CheckboxTag;
import com.rapidark.framework.cosyui.control.ChildTabTag;
import com.rapidark.framework.cosyui.control.DataGridTag;
import com.rapidark.framework.cosyui.control.DataListTag;
import com.rapidark.framework.cosyui.control.MenuTag;
import com.rapidark.framework.cosyui.control.PageBarTag;
import com.rapidark.framework.cosyui.control.PanelHeaderTag;
import com.rapidark.framework.cosyui.control.RadioTag;
import com.rapidark.framework.cosyui.control.ScrollPanelTag;
import com.rapidark.framework.cosyui.control.SelectTag;
import com.rapidark.framework.cosyui.control.SliderTag;
import com.rapidark.framework.cosyui.control.TabTag;
import com.rapidark.framework.cosyui.control.ToolBarTag;
import com.rapidark.framework.cosyui.control.TreeTag;
import com.rapidark.framework.cosyui.control.UploaderTag;
import com.rapidark.framework.cosyui.tag.ActionTag;
import com.rapidark.framework.cosyui.tag.BreakTag;
import com.rapidark.framework.cosyui.tag.ChooseTag;
import com.rapidark.framework.cosyui.tag.ConfigTag;
import com.rapidark.framework.cosyui.tag.ElseIfTag;
import com.rapidark.framework.cosyui.tag.ElseTag;
import com.rapidark.framework.cosyui.tag.EvalTag;
import com.rapidark.framework.cosyui.tag.ForEachTag;
import com.rapidark.framework.cosyui.tag.ForTag;
import com.rapidark.framework.cosyui.tag.IfTag;
import com.rapidark.framework.cosyui.tag.IncludeTag;
import com.rapidark.framework.cosyui.tag.InitTag;
import com.rapidark.framework.cosyui.tag.InvokeTag;
import com.rapidark.framework.cosyui.tag.ListTag;
import com.rapidark.framework.cosyui.tag.ParamTag;
import com.rapidark.framework.cosyui.tag.SetTag;
import com.rapidark.framework.cosyui.tag.SubTag;
import com.rapidark.framework.cosyui.tag.WhenTag;
import com.rapidark.framework.cosyui.template.AbstractTag;
import com.rapidark.framework.extend.AbstractExtendService;
import com.rapidark.framework.extend.action.ExtendTag;
import com.rapidark.framework.i18n.LangButtonTag;
import com.rapidark.framework.i18n.LangTag;
import com.rapidark.framework.security.PrivTag;

/**
 * Zhtml标签扩展服务
 * com.rapidark.framework.cosyui.zhtml.ZhtmlTagService
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
