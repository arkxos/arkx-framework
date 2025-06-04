package com.arkxos.framework.extend.action;

import java.util.Map.Entry;

import com.arkxos.framework.Current;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.ServletUtil;
import io.arkx.framework.cosyui.template.AbstractExecuteContext;
import com.arkxos.framework.extend.ExtendException;
import com.arkxos.framework.extend.IExtendAction;

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
