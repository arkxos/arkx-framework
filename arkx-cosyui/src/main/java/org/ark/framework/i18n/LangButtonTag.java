package org.ark.framework.i18n;

import org.ark.framework.jaf.tag.SelectTag;

import io.arkx.framework.Config;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.i18n.LangUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.TagSupport;


public class LangButtonTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	private String target;
	private String value;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.target = null;
	}

	public int doStartTag() throws JspException {
		try {
			if (LangUtil.getSupportedLanguages().size() > 0) {
				StringBuilder sb = new StringBuilder();
				if (ObjectUtil.notEmpty(this.value))
					this.value = ObjectUtil.toString(SelectTag.getRealValue(this.value, this, this.pageContext));
				else {
					this.value = "";
				}
				sb.append("<input type=\"hidden\" value=\"").append(StringUtil.escape(this.value)).append("\" id=\"" + this.target + "_I18N\">");
				sb.append("<img src=\"").append(Config.getContextPath()).append("Icons/i18n.gif\" align=\"absmiddle\" style=\"cursor:pointer\" onclick=\"Ark.Lang.onLangButtonClick('")
						.append(this.target).append("')\"/>");
				sb.append("<script>Ark.Page.onReady(function(){Ark.Node.setValue(\"" + this.target + "\",\"" + LangUtil.decode(this.value) + "\");});</script>");
				this.pageContext.getOut().write(sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}