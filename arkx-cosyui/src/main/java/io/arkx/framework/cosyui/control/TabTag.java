package io.arkx.framework.cosyui.control;

import io.arkx.framework.FrameworkPlugin;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.tag.ArkTag;
import io.arkx.framework.cosyui.template.TagAttr;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;

import java.util.ArrayList;
import java.util.List;

/**
 * 选项卡标签　
 * 
 */
public class TabTag extends ArkTag {
	public static final String TabTagKey = "_ARK_TABTAGKEY";
	public static int count = 0;
	
	private boolean lazy = true;
	private boolean cachedom = false;
	private String id;
	
	@Override
	public String getTagName() {
		return "tab";
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		if(StringUtil.isEmpty(id)) {
			this.id = "tab" + count++;
		}
		return id;
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		pageContext.setAttribute(TabTagKey, new ArrayList<>());
		context.getOut().write("<table id=" + getId() + " width=\"100%\" height=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" ztype=\"tab_table\" class=\"js_layoutTable\">");
		context.getOut().write("<tr><td height=\"37\" ztype=\"tab_thead\" valign=\"top\" style=\"_position:relative\">");
		context.getOut().write("<div class=\"z-tabpanel\"><div class=\"z-tabpanel-ct\"><div class=\"z-tabpanel-overflow\"><div class=\"z-tabpanel-nowrap\"");
		if (!lazy || cachedom) {
			context.getOut().write(" data-cache-dom=\"true\"");
		}
		context.getOut().write(">");
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doAfterBody() throws TemplateRuntimeException {
		try {
			context.getOut().write("</div></div></div></div>");
			context.getOut().write("</td></tr>");
			String selectedID = "";

			@SuppressWarnings("unchecked")
			ArrayList<ChildTabTag> children = (ArrayList<ChildTabTag>) pageContext.getAttribute(TabTagKey);
			if (ObjectUtil.notEmpty(children)) {
				for (ChildTabTag child : children) {
					if (child.isSelected()) {
						selectedID = child.getChildTabId();
						break;
					}
				}
				if (ObjectUtil.isEmpty(selectedID)) {
					selectedID = children.get(0).getChildTabId();
				}

			}
			if (ObjectUtil.notEmpty(children)) {
				context.getOut().write("<tr><td height=\"*\" ztype=\"tab_tbody\" valign=\"top\">");
				context.getOut().write("<div");
				if (!lazy || cachedom) {
					context.getOut().write(" data-cache-dom=\"true\"");
				}
				context.getOut().write("><div data-role=\"page\" data-external-page=\"false\">&nbsp;</div></div>");
				context.getOut().write("<script>");
				context.getOut().write("Ark.Page.onReady(function(){Ark.TabPage.init(");
				if (lazy) {
					context.getOut().write("true, ");
				} else {
					context.getOut().write("false, ");
				}
				context.getOut().write("\"" + selectedID + "\");},110);");
				context.getOut().write("</script>");
				context.getOut().write("</td></tr>");
			}
			context.getOut().write("</table>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public boolean isCachedom() {
		return cachedom;
	}

	public void setCachedom(boolean cachedom) {
		this.cachedom = cachedom;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<>();
		list.add(new TagAttr("id"));
		list.add(new TagAttr("lazy", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("cachedom", TagAttr.BOOL_OPTIONS));
		return list;
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.UIControl.TabTagName}";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

}
