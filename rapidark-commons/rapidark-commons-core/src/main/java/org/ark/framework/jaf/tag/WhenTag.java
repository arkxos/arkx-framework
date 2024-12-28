package org.ark.framework.jaf.tag;

import java.io.IOException;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import jakarta.servlet.jsp.tagext.Tag;

import org.ark.framework.jaf.PlaceHolder;
import org.ark.framework.jaf.PlaceHolderContext;
import org.ark.framework.jaf.expression.Operators;
import org.ark.framework.jaf.expression.Primitives;

import com.rapidark.framework.commons.util.StringUtil;


/**
 * @class org.ark.framework.jaf.tag.WhenTag
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:53:17 
 * @version V1.0
 */
public class WhenTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	private String value;
	private String out;
	private boolean other;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.value = null;
		this.out = null;
		this.other = false;
	}

	public int doStartTag() throws JspException {
		Tag tag = getParent();
		if (!(tag instanceof ChooseTag)) {
			throw new RuntimeException("tag when must in tag choose");
		}
		ChooseTag parent = (ChooseTag) tag;
		Object v1 = parent.getValue();
		Object v2 = this.value;
		if (!this.other) {
			if (this.value == null) {
				throw new RuntimeException("tag when's other and value can't be empty at the same time");
			}
			if (this.value.startsWith("${")) {
				PlaceHolderContext context = PlaceHolderContext.getInstance(this, this.pageContext);
				v2 = context.eval(new PlaceHolder(this.value));
			}
		}
		if (this.other) {
			if (!parent.isMatched()) {
				output(parent);
				return 2;
			}
			return 0;
		}
		if (Primitives.getBoolean(Operators.eq(v1, v2))) {
			output(parent);
			return 2;
		}
		return 0;
	}

	private void output(ChooseTag parent) {
		if (StringUtil.isNotEmpty(this.out)) {
			if (this.out.startsWith("${")) {
				PlaceHolderContext context = PlaceHolderContext.getInstance(this, this.pageContext);
				this.out = String.valueOf(context.eval(new PlaceHolder(this.out)));
			}
			try {
				this.pageContext.getOut().print(this.out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		parent.setMatched(true);
	}

	public int doAfterBody() throws JspException {
		try {
			getPreviousOut().print(getBodyContent().getString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 6;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOut() {
		return this.out;
	}

	public void setOut(String out) {
		this.out = out;
	}

	public boolean isOther() {
		return this.other;
	}

	public void setOther(boolean other) {
		this.other = other;
	}
}