package org.ark.framework.jaf.html;

/**
 * @class org.ark.framework.jaf.html.HtmlInput
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:49:19 
 * @version V1.0
 */
public class HtmlInput extends HtmlElement {
	public HtmlInput() {
		this.ElementType = "INPUT";
		this.TagName = "input";
	}

	public String getOuterHtml(String prefix) {
		String html = super.getOuterHtml(prefix);
		int index = html.lastIndexOf("</");
		if (index > 0) {
			html = html.substring(0, index).trim();
			html = html.substring(0, html.length() - 1) + " />";
		}
		return html;
	}
}