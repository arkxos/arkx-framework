package com.arkxos.framework.cosyui.control;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.arkxos.framework.FrameworkPlugin;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.cosyui.expression.ExpressionException;
import com.arkxos.framework.cosyui.tag.ArkTag;
import com.arkxos.framework.cosyui.template.TagAttr;
import com.arkxos.framework.cosyui.template.TemplateCompiler;
import com.arkxos.framework.cosyui.template.TemplateExecutor;
import com.arkxos.framework.cosyui.template.command.TagCommand;
import com.arkxos.framework.cosyui.template.exception.TemplateRuntimeException;
import com.arkxos.framework.cosyui.util.TagUtil;
import com.arkxos.framework.i18n.LangUtil;

/**
 * 按钮标签
 * 
 */
public class ButtonTag extends ArkTag {
	private static Pattern PZIcon = Pattern
			.compile(" src\\=\"[^\"]*icons\\/([^\"\\/]+)\\.png\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	public static final Pattern PImg = Pattern.compile("<img .*?src\\=.*?>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	String id;

	String name;

	String onClick;

	String href;

	String target;

	String priv;

	String type;// push linkt radio checkbox menu select

	String theme;

	String className;

	String menu;

	boolean disabled;

	boolean checked;

	@Override
	public String getTagName() {
		return "button";
	}

	@Override
	public void init() throws ExpressionException {
		super.init();
		if(attributes.containsKey("disabled")) {
			if(!TagAttr.BOOL_OPTIONS.containsKey(attributes.get("disabled"))) {
				Object obj = pageContext.evalExpression(attributes.get("disabled"));
				if (obj == null) {
					obj = "false";// null必须转为空串
				}
//				setAttributeInternal("disabled", obj);
//				disabled = Boolean.valueOf(obj.toString());
				attributes.put("disabled", obj.toString());
			}
		}
	}
	
	@Override
	public int doStartTag() throws TemplateRuntimeException {
		if (ObjectUtil.isEmpty(id)) {
			id = TagUtil.getTagID(pageContext, "Button");
			context.addDataVariable("_ButtonID", id);
		}
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public boolean isKeepTagSource() {
		return true;
	}

	@Override
	public void afterCompile(TagCommand tc, TemplateExecutor te) {
		String content = rewrite(attributes, getTagBodySource());
		TemplateCompiler c = new TemplateCompiler(te.getManagerContext());
		c.compileSource(content);
		if (!tc.isHasBody()) {
			tc.setHasBody(true);
		}
		tc.setCommands(c.getExecutor().getCommands());
	}

	static String rewrite(Mapx<String, String> attributes, String content) {
		try {
			Matcher matcher = PImg.matcher(content);
			String img = null;
			String text = null;
			if (matcher.find()) {
				img = content.substring(matcher.start(), matcher.end());
				img = getImgSpirite(img);
				text = content.substring(matcher.end());
			} else {
				text = content.trim();
			}
			String id = attributes.get("id");
			if (StringUtil.isEmpty(id)) {
				id = "${_ButtonID}";
			}
			String html = getHtml(id, attributes.get("name"), attributes.get("type"), attributes.get("theme"), attributes.get("className"),
					attributes.get("menu"), attributes.get("onClick"), attributes.get("href"), attributes.get("target"),
					attributes.get("priv"), img, text, attributes.get("checked"), attributes.get("disabled"));
			return html;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String getImgSpirite(String imgTag) {
		Matcher matcher = PZIcon.matcher(imgTag);
		String fileName;
		if (matcher.find()) {
			fileName = matcher.group(1);
			imgTag = imgTag.replaceAll(fileName, "icon000").replaceFirst("<img", "<img class=\"" + fileName + "\"");
		}
		return imgTag;
	}

	public static String getHtml(String id, String name, String type, String theme, String className, String menu, String onclick,
			String href, String target, String priv, String img, String text, String checked, String disabled) {
		StringBuilder sb = new StringBuilder();
		if (ObjectUtil.isEmpty(type)) {
			type = "push";
		}
		sb.append("<a href='").append(ObjectUtil.notEmpty(href) ? href : "javascript:void(0);").append("'");
		if (ObjectUtil.notEmpty(target)) {
			sb.append(" target='" + target + "'");
		}
		sb.append(" ztype='button'");
		sb.append(" buttontype='" + type + "'");
		sb.append(" id='" + id + "'");
		sb.append(name == null ? "" : " name='" + name + "'");
		if ("checkbox".equals(type)) {
			sb.append(" ${" + checked + "?'checked':''} ");
		}
		if (ObjectUtil.notEmpty(menu)) {
			sb.append(" menu='" + menu + "'");
		}
		sb.append(" class='z-btn");
		if ("menu".equals(type)) {
			sb.append(" z-btn-menu");
		} else if ("split".equals(type) || "select".equals(type)) {
			sb.append(" z-btn-split");
		}
		if ("checkbox".equals(type)) {
			sb.append(" z-btn-checkable");
		}
		if (StringUtil.isNotEmpty(theme)) {
			sb.append(" z-btn-" + theme);
		}
		if (StringUtil.isNotEmpty(disabled)) {
			sb.append(" ${" + disabled + "?'z-btn-disabled':''} ");
			sb.append(" z-btn-disabled");
		}
		if (StringUtil.isNotNull(className)) {
			sb.append(" " + className);
		}
		sb.append("'");
		sb.append(" onselectstart='return false'");

		if (onclick != null) {
			if (StringUtil.isNotEmpty(disabled)) {
				sb.append(" ${" + disabled + "?'_onclick_bak=\"" + onclick + "\";return false;':''} ");
			} else {
				sb.append(" onclick=\"").append(onclick).append(";return false;\"");
			}
		}
		if (priv != null) {
			sb.append(" priv=\"").append(priv).append("\"");
		}
		sb.append(">");
		if (img != null) {
			sb.append(img);
		}
		text = LangUtil.get(text);
		sb.append("<b>").append(text).append("<i></i></b>");
		sb.append("</a>");
		sb.append("<script> new Ark.Button('").append(id).append("');</script>");
		return sb.toString();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<TagAttr>();
		list.add(new TagAttr("id", true));
		list.add(new TagAttr("checked", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("disabled"));
		list.add(new TagAttr("href"));
		list.add(new TagAttr("menu"));
		list.add(new TagAttr("name"));
		list.add(new TagAttr("onclick"));
		list.add(new TagAttr("priv"));
		list.add(new TagAttr("target"));
		list.add(new TagAttr("theme"));
		list.add(new TagAttr("className"));
		list.add(new TagAttr("type"));
		return list;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.UIControl.ButtonTagName}";
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

}
