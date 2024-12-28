package org.ark.framework.jaf.tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.TagSupport;

import org.ark.framework.jaf.PlaceHolder;
import org.ark.framework.jaf.PlaceHolderContext;

import com.rapidark.framework.i18n.LangUtil;


/**
 * @class org.ark.framework.jaf.tag.ParamTag
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:52:58 
 * @version V1.0
 */
public class ParamTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	private String var;
	private String Default;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.var = null;
		this.Default = null;
	}

	public int doStartTag() throws JspException {
		try {
			PlaceHolderContext context = PlaceHolderContext.getInstance(this, this.pageContext);
			Object value = context.eval(new PlaceHolder(this.var));
			if (value != null) {
				String v = String.valueOf(value);
				v = LangUtil.get(v);
				this.pageContext.getOut().write(v);
			} else if (this.Default != null) {
				this.pageContext.getOut().write(this.Default);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getVar() {
		return this.var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getDefault() {
		return this.Default;
	}

	public void setDefault(String default1) {
		this.Default = default1;
	}
}