package com.arkxos.framework.cosyui.tag;

import java.util.ArrayList;
import java.util.List;

import com.arkxos.framework.commons.collection.DataTypes;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.cosyui.template.TagAttr;
import com.arkxos.framework.cosyui.template.TemplateExecutor;
import com.arkxos.framework.cosyui.template.command.TagCommand;
import com.arkxos.framework.cosyui.template.exception.TemplateRuntimeException;
import com.rapidark.framework.FrameworkPlugin;

/**
 * 子过程定义标签，定义好的模板过程可以使用ark:invoke调用，并可以递归调用。<br>
 * 
 */
public class SubTag extends ArkTag {
	public static final String KEY = "_ARK_SUBTAG_MAP";

	private String name;

	@Override
	public String getTagName() {
		return "sub";
	}

	@Override
	public void afterCompile(TagCommand command, TemplateExecutor executor) {
		@SuppressWarnings("unchecked")
		Mapx<String, TagCommand> subtagMap = (Mapx<String, TagCommand>) executor.getAttributes().get(KEY);
		if (subtagMap == null) {
			subtagMap = new Mapx<String, TagCommand>();
			executor.getAttributes().put(KEY, subtagMap);
		}
		subtagMap.put(name, command);
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		return SKIP_BODY;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<TagAttr>();
		list.add(new TagAttr("name", true, DataTypes.STRING.code(), "@{Framework.ZSubTag.Name}"));
		return list;
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

	@Override
	public String getDescription() {
		return "@{Framework.ZSubTag.Desc}";
	}

	@Override
	public String getExtendItemName() {
		if (pageContext == null) {
			return "@{Framework.ZSubTagName}";
		} else {
			return name;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

}
