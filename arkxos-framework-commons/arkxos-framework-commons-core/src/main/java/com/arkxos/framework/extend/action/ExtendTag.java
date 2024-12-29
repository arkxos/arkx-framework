package com.arkxos.framework.extend.action;

import java.util.ArrayList;
import java.util.List;

import com.arkxos.framework.cosyui.tag.ArkTag;
import com.arkxos.framework.cosyui.template.TagAttr;
import com.arkxos.framework.extend.ExtendManager;
import com.rapidark.framework.FrameworkPlugin;

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
