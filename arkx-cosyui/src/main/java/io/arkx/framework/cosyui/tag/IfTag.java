package io.arkx.framework.cosyui.tag;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.FrameworkPlugin;
import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.commons.util.Primitives;
import io.arkx.framework.cosyui.template.TagAttr;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;

/**
 * <ark:if condition="${Type}=='B'"></ark:if>
 * <ark:if condition="${contains(privileges, 'carLogisticsRequest')}">
 * if条件分支标签.<br>
 * 
 */
public class IfTag extends ArkTag {

//	public static final String IfTagInAttribute = "_ARK_IF_TAG";
	public static final String IfTagInVariables = "_ARK_IF_TAG";

	protected String condition;

	protected String out;

	@Override
	public String getTagName() {
		return "if";
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		if (parent == null) {
			pageContext.addRootVariable(IfTagInVariables, this);
		} else {
			parent.getVariables().put(IfTagInVariables, this);
		}
		if (Primitives.getBoolean(condition)) {
			if (out != null) {
				pageContext.getOut().write(out);
			}
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}

	public boolean isPass() {
		return Primitives.getBoolean(condition);
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getTest() {// 兼容jstl的写法
		return condition;
	}

	public void setTest(String condition) {
		this.condition = condition;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<>();
		list.add(new TagAttr("condition", DataTypes.STRING.code(), "@{Framework.IfTag.Condition}"));
		list.add(new TagAttr("out", DataTypes.STRING.code(), "@{Framework.IfTag.Output}"));
		return list;
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

	@Override
	public String getDescription() {
		return "@{Framework.ZIfTagDescription}";
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.ZIfTagName}";
	}

}
