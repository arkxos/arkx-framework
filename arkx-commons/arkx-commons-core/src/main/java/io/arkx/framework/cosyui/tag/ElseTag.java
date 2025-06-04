package io.arkx.framework.cosyui.tag;

import java.util.ArrayList;
import java.util.List;

import com.arkxos.framework.FrameworkPlugin;
import io.arkx.framework.cosyui.template.AbstractExecuteContext;
import io.arkx.framework.cosyui.template.AbstractTag;
import io.arkx.framework.cosyui.template.TagAttr;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;

/**
 * Else分支标签
 * 
 */
public class ElseTag extends ArkTag {

	private String out;

	@Override
	public String getTagName() {
		return "else";
	}

	protected static boolean isSkip(AbstractTag current, AbstractExecuteContext pageContext) {
		AbstractTag tag = null;
		if (current.getParent() == null) {
			tag = (AbstractTag) pageContext.getRootVariable(IfTag.IfTagInVariables);
			pageContext.removeRootVariable(IfTag.IfTagInVariables);

		} else {
			tag = (AbstractTag) current.getParent().getVariable(IfTag.IfTagInVariables);
			current.getParent().getVariables().remove(IfTag.IfTagInVariables);
		}
		if (tag == null || tag.getParent() != current.getParent()) {
			return true;
		}
		if (((IfTag) tag).isPass()) {// 如果if成立，则else不成立
			return true;
		}
		return false;
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		if (isSkip(this, pageContext)) {
			return SKIP_BODY;
		}
		if (out != null) {
			pageContext.getOut().write(out);
		}
		return EVAL_BODY_INCLUDE;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<TagAttr>();
		list.add(new TagAttr("out"));
		return list;
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

	@Override
	public String getDescription() {
		return "@{Framework.ZElseTagDescription}";
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.ZElseTagName}";
	}

}
