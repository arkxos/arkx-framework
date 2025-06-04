package org.ark.framework.jaf.controls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ark.framework.jaf.TagUtil;

import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.i18n.LangUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;


/**
 * @class org.ark.framework.jaf.controls.PanelHeaderTag
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:43:42 
 * @version V1.0
 */
public class PanelHeaderTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	private String id;
	private String onClick;
	private boolean collapsible;
	private boolean collapsed;
	public static final Pattern PImg = Pattern.compile("^<img .*?src\\=.*?>", 34);

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.id = null;
		this.onClick = null;
		this.collapsible = true;
		this.collapsed = false;
	}

	public int doAfterBody() throws JspException {
		String content = getBodyContent().getString().trim();
		try {
			Matcher matcher = PImg.matcher(content);
			String img = null;
			String text = null;
			if (ObjectUtil.empty(this.id)) {
				this.id = TagUtil.getTagID(this.pageContext, "PanelHeader");
			}
			if (matcher.find()) {
				img = content.substring(matcher.start(), matcher.end());
				text = content.substring(matcher.end());
				getPreviousOut().print(getHtml(this.id, img, text, this.collapsible, this.collapsed));
			} else {
				text = content;
				getPreviousOut().print(getHtml(this.id, text, this.collapsible, this.collapsed));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 6;
	}

	public static String getHtml(String id, String img, String text, boolean collapsible, boolean collapsed) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"z-panel-header\" id=\"").append(id).append("\">").append("<div class=\"z-panel-header-ct\">");
		if (collapsible) {
			sb.append("<a class=\"z-tool-toggle\" href=\"#;\">&nbsp;</a>");
		}
		text = LangUtil.get(text);
		sb.append("<b class=\"z-panel-header-text\">").append(text).append("</b>");
		sb.append("</div>").append("</div>");
		sb.append("<script>$(function(){new PanelHeader({el:'").append(id).append("',collapsed:").append(collapsed).append("});});</script>");
		return sb.toString();
	}

	public static String getHtml(String id, String html, boolean collapsible, boolean collapsed) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"z-panel-header\" id=\"").append(id).append("\">").append("<div class=\"z-panel-header-ct\">");
		if (collapsible) {
			sb.append("<a class=\"z-tool-toggle\" href=\"#;\">&nbsp;</a>");
		}
		html = LangUtil.get(html);
		sb.append("<b class=\"z-panel-header-html\">").append(html).append("</b>");
		sb.append("</div>").append("</div>");
		sb.append("<script>$(function(){new PanelHeader({el:'").append(id).append("',collapsed:").append(collapsed).append("});});</script>");
		return sb.toString();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOnClick() {
		return this.onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public boolean isCollapsible() {
		return this.collapsible;
	}

	public void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
	}

	public boolean isCollapsed() {
		return this.collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}
}