package org.ark.framework.jaf.tag;

import java.io.IOException;

import org.ark.framework.jaf.PlaceHolder;
import org.ark.framework.jaf.PlaceHolderContext;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import jakarta.servlet.jsp.tagext.Tag;

/**
 * @class org.ark.framework.jaf.tag.ElseTag
 *
 * @author Darkness
 * @date 2013-1-31 下午12:51:35
 * @version V1.0
 */
public class ElseTag extends BodyTagSupport {
    private static final long serialVersionUID = 1L;
    private String out;

    public void setPageContext(PageContext pc) {
        super.setPageContext(pc);
        this.out = null;
    }

    public int doStartTag() throws JspException {
        Tag tag = (Tag) this.pageContext.getAttribute("_IF_TAG_FALSE");
        Tag parent = (Tag) this.pageContext.getAttribute("_IF_PARENT_TAG_FALSE");
        if ((tag == null) || (parent != getParent())) {
            return 0;
        }
        this.pageContext.removeAttribute("_IF_TAG_FALSE");
        if (((IfTag) tag).isPass()) {
            return 0;
        }
        if (this.out != null) {
            try {
                PlaceHolderContext context = PlaceHolderContext.getInstance(this, this.pageContext);
                if (this.out.startsWith("${")) {
                    PlaceHolder holder = new PlaceHolder(this.out);
                    this.out = String.valueOf(context.eval(holder));
                }
                this.pageContext.getOut().print(this.out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 2;
    }

    public int doAfterBody() throws JspException {
        BodyContent body = getBodyContent();
        String content = body.getString().trim();
        try {
            getPreviousOut().write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 6;
    }

    public String getOut() {
        return this.out;
    }

    public void setOut(String out) {
        this.out = out;
    }
}
