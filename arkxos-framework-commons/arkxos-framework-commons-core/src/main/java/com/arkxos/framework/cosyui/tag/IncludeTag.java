package com.arkxos.framework.cosyui.tag;

import java.util.ArrayList;
import java.util.List;

import com.arkxos.framework.commons.collection.DataTypes;
import com.arkxos.framework.cosyui.template.AbstractExecuteContext;
import com.arkxos.framework.cosyui.template.TagAttr;
import com.arkxos.framework.cosyui.template.exception.TemplateRuntimeException;
import com.rapidark.framework.FrameworkPlugin;

/**
 * 模板包含标签，在zhtml页面中包含文件，file相对于应用根目录
 * 
 */
public class IncludeTag extends ArkTag {
	String file;

	@Override
	public String getTagName() {
		return "include";
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		AbstractExecuteContext includeContext = pageContext.getIncludeContext();
		includeContext.getManagerContext().getTemplateManager().execute(file, includeContext);
		return SKIP_BODY;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<TagAttr>();
		list.add(new TagAttr("file", DataTypes.STRING.code(), "@{Framework.IncludeTag.File}"));
		return list;
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

	@Override
	public String getDescription() {
		return "@{Framework.IncludeTag.Desc}";
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.IncludeTag.Name}";
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
