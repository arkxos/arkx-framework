package org.ark.framework.jaf.tag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ark.framework.jaf.TagUtil;
import org.ark.framework.jaf.html.HtmlElement;

import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.i18n.LangUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;


/**
 * @class org.ark.framework.jaf.tag.MenuTag
 * <h2>菜单标签</h2>
 * <br/>
 * <img src="images/MenuTag_1.png"/>
 * @author Darkness
 * @date 2013-1-31 下午12:43:18 
 * @version V1.0
 */
public class MenuTag extends BodyTagSupport {
	
	private static final long serialVersionUID = 1L;

	public static final Pattern PItem = Pattern.compile("<(li|a)(.*?)onclick=(\\\"|\\')(.*?)\\2.*?>(.*?)</(li|a)>", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);

	/**
	 * id
	 * @property id
	 * @type {String}
	 */
	private String id;
	
	/**
	 * 菜单项点击执行函数
	 * @property onitemclick
	 * @type {Function}
	 */
	private String onitemclick;
	private String type;
	
	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.id = null;
		this.onitemclick = null;
		this.type = null;
	}

	public int doAfterBody() throws JspException {
		String content = getBodyContent().getString();
		try {
			getPreviousOut().print(getHtml(content));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 6;
	}

	public String getHtml(String content) {
		String items = parseItems(content);
		StringBuilder sb = new StringBuilder();
		if (ObjectUtil.empty(this.id)) {
			this.id = TagUtil.getTagID(this.pageContext, "Menu");
		}
		sb.append("<div id=\"" + this.id + "\" class=\"z-menu z-hidden");
		if ("flat".equals(this.type)) {
			sb.append(" z-menu-flat");
		}
		sb.append("\">");
		sb.append(items);
		sb.append("</div>");
		sb.append("<script>");
		sb.append("new Ark.DropMenu('" + this.id + "');");
		sb.append("</script>");
		return sb.toString();
	}

	private String parseItems(String content) {
		StringBuilder sb = new StringBuilder();
		Matcher m = PItem.matcher(content);
		int lastIndex = 0;
		int i = 0;

		while (m.find(lastIndex)) {
			String tmp = content.substring(lastIndex, m.start());
			StringUtil.isNotEmpty(tmp.trim());

			String attrs = m.group(1);
			String id = HtmlElement.parseAttr(attrs).getString("id");
			String innerText = m.group(5);
			innerText = LangUtil.get(innerText);
			String attr_onClick = m.group(4);
			sb.append("<a id=\"" + id + "\" href=\"javascript:void(0);\" class=\"z-menu-item\" onclick=\"" + attr_onClick + ";return false;\"" + " hidefocus >" + innerText + "</a>");
			lastIndex = m.end();
			i++;
		}
		content.length();

		if (i != 0) {
			content = sb.toString();
		}
		return content;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOnitemclick() {
		return this.onitemclick;
	}

	public void setOnitemclick(String onitemclick) {
		this.onitemclick = onitemclick;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
}