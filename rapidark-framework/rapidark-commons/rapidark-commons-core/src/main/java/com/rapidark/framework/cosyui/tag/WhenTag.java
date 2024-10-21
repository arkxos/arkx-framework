package com.rapidark.framework.cosyui.tag;

import java.util.ArrayList;
import java.util.List;

import com.rapidark.framework.FrameworkPlugin;
import com.rapidark.framework.commons.util.Operators;
import com.rapidark.framework.commons.util.Primitives;
import com.rapidark.framework.commons.util.StringUtil;
import com.rapidark.framework.cosyui.UIException;
import com.rapidark.framework.cosyui.template.AbstractTag;
import com.rapidark.framework.cosyui.template.TagAttr;
import com.rapidark.framework.cosyui.template.exception.TemplateRuntimeException;

/**
 * <ark:when>标签,用于实现Choose/When支持
 * 
 */
public class WhenTag extends ArkTag {

	private String value;

	private String out;

	private boolean other;

	@Override
	public String getTagName() {
		return "when";
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		AbstractTag tag = getParent();
		if (!(tag instanceof ChooseTag)) {
			throw new UIException("tag when must in tag choose");
		}
		ChooseTag parent = (ChooseTag) tag;
		Object v1 = parent.getVariable();
		Object v2 = value;
		if (!other) {
			if (value == null) {
				throw new UIException("tag when's other and value can't be empty at the same time");
			}
		}
		if (other) {
			if (!parent.isMatched()) {
				output(parent);
				return EVAL_BODY_INCLUDE;
			}
			return SKIP_BODY;
		} else if (Primitives.getBoolean(Operators.eq(v1, v2))) {
			output(parent);
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}

	private void output(ChooseTag parent) {
		if (StringUtil.isNotEmpty(out)) {
			pageContext.getOut().write(out);
		}
		parent.setMatched(true);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}

	public boolean isOther() {
		return other;
	}

	public void setOther(boolean other) {
		this.other = other;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<TagAttr>();
		list.add(new TagAttr("value"));
		list.add(new TagAttr("out"));
		list.add(new TagAttr("other", TagAttr.BOOL_OPTIONS));
		return list;
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.WhenTag.Name}";
	}

	@Override
	public String getDescription() {
		return null;
	}

}
