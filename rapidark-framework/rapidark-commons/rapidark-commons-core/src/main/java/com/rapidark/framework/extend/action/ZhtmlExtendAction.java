package com.rapidark.framework.extend.action;

import java.util.Map.Entry;

import com.rapidark.framework.Current;
import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.commons.util.ObjectUtil;
import com.rapidark.framework.commons.util.ServletUtil;
import com.rapidark.framework.cosyui.template.AbstractExecuteContext;
import com.rapidark.framework.extend.ExtendException;
import com.rapidark.framework.extend.IExtendAction;

/**
 * Zhtml扩展行为虚拟类。 <br>
 * 
 */
public abstract class ZhtmlExtendAction implements IExtendAction {
	@Override
	public Object execute(Object[] args) throws ExtendException {
		AbstractExecuteContext pageContext = (AbstractExecuteContext) args[0];
		ZhtmlContext context = new ZhtmlContext(Current.getRequest());
		execute(context);
		if (!ObjectUtil.isEmpty(context.getOut())) {
			pageContext.getOut().write(context.getOut());
		}
		if (context.getIncludes().size() > 0) {
			for (String file : context.getIncludes()) {
				try {
					AbstractExecuteContext includeContext = pageContext.getIncludeContext();
					if (file.indexOf('?') > 0) {
						Mapx<String, String> map = ServletUtil.getMapFromQueryString(file);
						for (Entry<String, String> e : map.entrySet()) {
							includeContext.addRootVariable(e.getKey(), e.getValue());
						}
					}
					includeContext.getManagerContext().getTemplateManager().execute(file, includeContext);
				} catch (Exception e) {
					e.printStackTrace();
					throw new ExtendException(e.getMessage());
				}
			}
		}
		return null;
	}

	public abstract void execute(ZhtmlContext context) throws ExtendException;
}
