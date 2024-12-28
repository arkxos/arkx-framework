package org.ark.framework.jaf.controls;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

import org.ark.framework.jaf.TagUtil;

import com.rapidark.framework.commons.util.ObjectUtil;


/**
 * @class org.ark.framework.jaf.controls.ScrollPanelTag
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:44:07 
 * @version V1.0
 */
public class ScrollPanelTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	private String id;
	private String targetId;
	private String theme;
	private String overflow;
	private boolean adaptive;
	public static final Pattern PId = Pattern.compile("^\\s*<div[^<>]+id\\=['\"]([\\w-]+)['\"]", 10);

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.id = null;
		this.theme = null;
		this.overflow = null;
		this.adaptive = false;
	}

	public static void main(String[] args) {
		Matcher matcher = PId.matcher(" <div class='z-overflowPanel'><form id='form1'><div id='z-legend'>");
		matcher.find();
		System.out.println(matcher.groupCount());
		for (int i = 0; i <= matcher.groupCount(); i++)
			System.out.println("group " + i + " : " + matcher.group(i));
	}

	public int doAfterBody() throws JspException {
		BodyContent body = getBodyContent();
		String content = body.getString().trim();
		try {
			Matcher matcher = PId.matcher(content);
			this.targetId = null;
			if ((matcher.find()) && (matcher.group(1) != null)) {
				this.targetId = matcher.group(1);
				if (ObjectUtil.empty(this.id))
					this.id = (this.targetId + "_scrollpanel");
			} else {
				if (ObjectUtil.empty(this.id)) {
					this.targetId = TagUtil.getTagID(this.pageContext, "ScrollPanel");
					this.id = (this.targetId + "_scrollpanel");
				}
				content.replaceFirst("^\\s*<div\\b", "<div id=\"" + this.targetId + "\" ");
			}

			getPreviousOut().print(content);

			getPreviousOut().print("<script>");
			getPreviousOut().print("Ark.Page.onLoad(function(){");
			getPreviousOut().print("if(Ark.ScrollPanel)");
			getPreviousOut().print(" new Ark.ScrollPanel({");
			if (ObjectUtil.notEmpty(this.overflow)) {
				getPreviousOut().print("  overflow:'" + this.overflow + "',");
			}
			getPreviousOut().print("  target:'" + this.targetId + "'");
			getPreviousOut().print(" });");
			getPreviousOut().print("});");
			getPreviousOut().print("</script>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 6;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTheme() {
		return this.theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getOverflow() {
		return this.overflow;
	}

	public void setOverflow(String overflow) {
		this.overflow = overflow;
	}

	public boolean isAdaptive() {
		return this.adaptive;
	}

	public void setAdaptive(boolean adaptive) {
		this.adaptive = adaptive;
	}
}