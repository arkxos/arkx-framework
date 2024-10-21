package com.rapidark.framework.extend.action;

import java.util.ArrayList;
import java.util.List;

import com.rapidark.framework.FrameworkPlugin;
import com.rapidark.framework.cosyui.tag.ArkTag;
import com.rapidark.framework.cosyui.template.TagAttr;
import com.rapidark.framework.extend.ExtendManager;

/**
 * Zhtml扩展点定义标签　
 * 
 */
public class ExtendTag extends ArkTag {

	private String id;

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int doStartTag() {
		ExtendManager.invoke(id, new Object[] { pageContext });
		return SKIP_BODY;
	}

	@Override
	public String getTagName() {
		return "extend";
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<>();
		list.add(new TagAttr("id", true));
		return list;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.ExtendTag.Name}";
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}
}
