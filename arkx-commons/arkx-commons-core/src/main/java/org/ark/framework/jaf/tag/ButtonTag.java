package org.ark.framework.jaf.tag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ark.framework.jaf.TagUtil;

import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import com.arkxos.framework.i18n.LangUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;


/**
 * @class org.ark.framework.jaf.tag.ButtonTag
 * <h2>按钮标签</h2>
 * <br/>
 * <img src="images/button_1.png"/>
 * @author Darkness
 * @date 2013-1-31 下午12:39:45 
 * @version V1.0
 */
public class ButtonTag extends BodyTagSupport {
	
	private static final long serialVersionUID = 1L;

	public static final Pattern PImg = Pattern.compile("<img .*?src\\=.*?>", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);

	/**
	 * id
	 * @property id
	 * @type {String}
	 */
	private String id;
	
	/**
	 * 名称
	 * @property name
	 * @type {String}
	 */
	private String name;
	
	/**
	 * 按钮点击执行函数
	 * @property onClick
	 * @type {Function}
	 */
	private String onClick;
	private String href;
	private String target;
	
	/**
	 * 按钮权限编码
	 * @property priv
	 * @type {String}
	 */
	private String priv;
	private String type;
	private String theme;
	private String menu;
	
	/**
	 * 是否禁用
	 * @property disabled
	 * @type {boolean}
	 */
	private boolean disabled;
	private boolean checked;
	
	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.id = null;
		this.name = null;
		this.onClick = null;
		this.href = null;
		this.target = null;
		this.priv = null;
		this.type = null;
		this.theme = "flat";// 主题默认flat
		this.menu = null;
		this.checked = false;
		this.disabled = false;
	}

	public int doAfterBody() throws JspException {
		String content = getBodyContent().getString();
		try {
			Matcher matcher = PImg.matcher(content);
			String img = null;
			String text = null;
			if (matcher.find()) {
				img = content.substring(matcher.start(), matcher.end());
				text = content.substring(matcher.end());
			}
			if (ObjectUtil.empty(this.id)) {
				this.id = TagUtil.getTagID(this.pageContext, "Button");
			}
			getPreviousOut().print(getHtml(this.id, this.name, this.type, this.theme, this.menu, this.onClick, this.href, this.target, this.priv, img, text, this.checked, this.disabled));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 6;
	}

	public static String getHtml(String onclick, String priv, String img, String text) {
		return getHtml(null, null, null, null, null, onclick, null, null, priv, img, text, false, false);
	}

	public static String getHtml(String name, String theme, String onclick, String priv, String img, String text, boolean disabled) {
		return getHtml(null, name, null, theme, null, onclick, null, null, priv, img, text, false, disabled);
	}

	public static String getHtml(String id, String name, String type, String theme, String menu, String onclick, String href, String target, String priv, String img, String text, boolean checked,
			boolean disabled) {
		StringBuilder sb = new StringBuilder();
		if (ObjectUtil.empty(type)) {
			type = "push";
		}
		sb.append("<a href='").append(ObjectUtil.notEmpty(href) ? href : "javascript:void(0);").append("'");
		if (ObjectUtil.notEmpty(target)) {
			sb.append(" target='" + target + "'");
		}
		sb.append(" ztype='button'");
		sb.append(" buttontype='" + type + "'");
		sb.append(" id='" + id + "'");
		sb.append(" name='" + name + "'");
		if ("checkbox".equals(type)) {
			sb.append(!checked ? " " : " checked='true'");
		}
		if (ObjectUtil.notEmpty(menu)) {
			sb.append(" menu='" + menu + "'");
		}
		sb.append(" class='z-btn");
		if ("menu".equals(type))
			sb.append(" z-btn-menu");
		else if (("split".equals(type)) || ("select".equals(type))) {
			sb.append(" z-btn-split");
		}
		if ("checkbox".equals(type)) {
			sb.append(" z-btn-checkable");
		}
		if (StringUtil.isNotEmpty(theme)) {
			sb.append(" z-btn-" + theme);
		}
		if (disabled) {
			sb.append(" z-btn-disabled");
		}
		sb.append("'");
		sb.append(" onselectstart='return false'");

		if (onclick != null) {
			if (disabled)
				sb.append(" _onclick_bak=\"").append(onclick).append(";return false;\"");
			else {
				sb.append(" onclick=\"").append(onclick).append(";return false;\"");
			}
		}
		if (priv != null) {
			sb.append(" priv=\"").append(priv).append("\"");
		}
		sb.append(">");
		sb.append(img);
		text = LangUtil.get(text);
		sb.append("<b>").append(text).append("<i></i></b>");
		sb.append("</a>");
		sb.append("<script> new Ark.Button('").append(id).append("');</script>");
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

	public String getHref() {
		return this.href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getPriv() {
		return this.priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	public boolean isChecked() {
		return this.checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMenu() {
		return this.menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public String getTheme() {
		return this.theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}
}