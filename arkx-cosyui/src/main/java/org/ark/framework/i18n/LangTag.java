package org.ark.framework.i18n;

import io.arkx.framework.Account;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.i18n.LangMapping;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

import java.io.IOException;


public class LangTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	private String id;
	private String Default;
	private String language;
	private String oldLanguage;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.id = null;
		this.Default = null;
		this.language = null;
		this.oldLanguage = null;
	}

	public int doStartTag() throws JspException {
		try {
			String str = LangMapping.get(this.id);
			if (this.id != null) {
				if (ObjectUtil.empty(str)) {
					str = this.Default;
				}
				if (str == null) {
					str = "@{" + this.id + "}";
				}
				this.pageContext.getOut().write(str);
			}
			if (this.language != null) {
				this.oldLanguage = Account.getLanguage();
				Account.setLanguage(this.language);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 2;
	}

	public int doAfterBody() throws JspException {
		if (this.oldLanguage != null) {
			Account.setLanguage(this.oldLanguage);
		}
		if (this.language != null) {
			BodyContent body = getBodyContent();
			String content = body.getString().trim();
			try {
				getPreviousOut().write(content);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 6;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String var) {
		this.id = var;
	}

	public String getDefault() {
		return this.Default;
	}

	public void setDefault(String default1) {
		this.Default = default1;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}