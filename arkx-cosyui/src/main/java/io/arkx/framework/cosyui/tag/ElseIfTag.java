package io.arkx.framework.cosyui.tag;

import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;

/**
 * else if条件分支标签.<br>
 *
 */
public class ElseIfTag extends IfTag {

	@Override
	public String getTagName() {
		return "elseif";
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		if (ElseTag.isSkip(this, pageContext)) {// 如果if/或其他elseif成立，则本elseif不成立
			return SKIP_BODY;
		}
		return super.doStartTag();
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
