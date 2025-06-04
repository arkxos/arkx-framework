package io.arkx.framework.cosyui.tag;

import java.util.ArrayList;
import java.util.List;

import com.arkxos.framework.FrameworkPlugin;
import io.arkx.framework.cosyui.template.TagAttr;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;
import io.arkx.framework.i18n.LangUtil;

/**
 * 参数求值标签，一般不需要再使用，直接在zhtml中使用表达式即可。
 * 
 */
public class ParamTag extends ArkTag {
	private String var;

	private String Default;

	@Override
	public String getTagName() {
		return "param";
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		var = context.eval(var);
		if (var != null) {
			String v = String.valueOf(var);
			v = LangUtil.get(v);
			pageContext.getOut().write(v);
		} else if (Default != null) {
			pageContext.getOut().write(Default);
		}
		return SKIP_BODY;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getDefault() {
		return Default;
	}

	public void setDefault(String default1) {
		Default = default1;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<>();
		list.add(new TagAttr("var"));
		list.add(new TagAttr("default"));
		return list;
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.Tag.ParamTagName}";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

}
