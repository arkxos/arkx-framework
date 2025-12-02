package io.arkx.framework.cosyui.control;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.arkx.framework.FrameworkPlugin;
import io.arkx.framework.commons.util.StringFormat;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.tag.ArkTag;
import io.arkx.framework.cosyui.template.TagAttr;
import io.arkx.framework.cosyui.template.TemplateCompiler;
import io.arkx.framework.cosyui.template.TemplateExecutor;
import io.arkx.framework.cosyui.template.command.TagCommand;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;

/**
 * 子选项卡标签类
 *
 */
public class ChildTabTag extends ArkTag {

	private static final String imgRegex = "<img .*?src=.*?>";

	public static final Pattern PImg = Pattern.compile(imgRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private static Pattern PZIcon = Pattern.compile(" src\\=\"[^\"]*icons\\/([^\"\\/]+)\\.png\"",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private static int No = 0;

	private String id;

	private String onClick;

	private String beforeClick;

	private String afterClick;

	private String src;

	private boolean selected;

	private boolean disabled;

	private boolean visible = true;

	private boolean lazy = true;

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public String getBeforeClick() {
		return beforeClick;
	}

	public void setBeforeClick(String beforeClick) {
		this.beforeClick = beforeClick;
	}

	public String getAfterClick() {
		return afterClick;
	}

	public void setAfterClick(String afterClick) {
		this.afterClick = afterClick;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<>();
		list.add(new TagAttr("id", true));
		list.add(new TagAttr("selected", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("disabled", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("lazy", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("visible", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("afterClick"));
		list.add(new TagAttr("beforeClick"));
		list.add(new TagAttr("onClick"));
		list.add(new TagAttr("src"));
		return list;
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.UIControl.ChildTabTagName}";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

	@Override
	public String getTagName() {
		return "childtab";
	}

	@Override
	public boolean isKeepTagSource() {
		return true;
	}

	/**
	 * 正则表达式第一次编译时执行，以提高性能
	 */
	@Override
	public void afterCompile(TagCommand tc, TemplateExecutor te) {
		String content = getTagBodySource();
		Matcher matcher = PImg.matcher(content);
		String imgTag = null;
		if (matcher.find()) {
			imgTag = content.substring(matcher.start(), matcher.end());
			imgTag = getImgSpirite(imgTag);
			content = content.replaceFirst(imgRegex, imgTag);
		}
		TemplateCompiler c = new TemplateCompiler(te.getManagerContext());
		c.compileSource(content);
		if (!tc.isHasBody()) {
			tc.setHasBody(true);
		}
		tc.setCommands(c.getExecutor().getCommands());
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		if (StringUtil.isEmpty(id)) {
			id = "" + No++;
		}
		if (onClick == null) {
			onClick = "";
		}
		if (StringUtil.isNotEmpty(onClick) && !onClick.trim().endsWith(";")) {
			onClick = onClick.trim() + ";";
		}
		if (beforeClick == null) {
			beforeClick = "";
		}
		if (StringUtil.isNotEmpty(beforeClick) && !beforeClick.trim().endsWith(";")) {
			beforeClick = beforeClick.trim() + ";";
		}
		if (afterClick == null) {
			afterClick = "";
		}
		if (StringUtil.isNotEmpty(afterClick) && !afterClick.trim().endsWith(";")) {
			afterClick = afterClick.trim() + ";";
		}
		String type = "";
		if (selected) {
			type = "Current";
		}
		else if (disabled) {
			type = "Disabled";
		}
		String vStr = "";
		if (!visible) {
			vStr = "style='display:none'";
		}
		context.getOut().write("<a href='javascript:void(0);' ztype='tab'  hidefocus='true' ");
		if ("Disabled".equalsIgnoreCase(type)) {
			context.getOut().write("id='" + id + "' " + vStr + " data-href='" + src + "' class='z-tab z-tab-disabled'");
		}
		else {
			if (lazy) {
				src = "data-href='" + src + "' data-lazy='true'";
			}
			else {
				src = "data-href='" + src + "' data-lazy='false'";
			}
			StringFormat sf = new StringFormat(
					"id='?' ? class='z-tab' ? onclick=\"??Ark.TabPage.onChildTabClick(this);?return false;\">");
			sf.add(id);
			sf.add(vStr);
			sf.add(src);
			sf.add(beforeClick);
			sf.add(onClick);
			sf.add(afterClick);
			context.getOut().write(sf.toString());
		}
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doAfterBody() throws TemplateRuntimeException {
		try {
			context.getOut().write("</a>");

			@SuppressWarnings("unchecked")
			ArrayList<ChildTabTag> children = (ArrayList<ChildTabTag>) pageContext.getAttribute(TabTag.TabTagKey);
			if (children != null) {
				children.add(this);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChildTabId() {
		return id;
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

}
