package org.ark.framework.jaf.controls;

import java.util.ArrayList;

import org.ark.framework.jaf.Current;

import io.arkx.framework.Config;
import io.arkx.framework.commons.util.StringFormat;
import io.arkx.framework.commons.util.StringUtil;


/**
 * 
 * @author Darkness
 * @date 2012-11-19 下午03:18:36
 * @version V1.0
 */
public class ChildTab {
	
	private String id;
	private String onClick;
	private String afterClick;
	private String src;
	private boolean selected;
	private boolean disabled;
	private boolean visible;
	private boolean lazy;
	private static int No = 0;
	private String displayType="iframe";
	private String img;
	private String title;

	private String content;

	public ChildTab(String content) {
		this.content = content;
	}

	@SuppressWarnings("unchecked")
	public String getHtml() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<a href='javascript:void(0);' ztype='tab'  hidefocus='true' ");
		if ("Disabled".equalsIgnoreCase(getType())) {
			sb.append("id='").append(this.getId()).append("' ").append(getVisiableString()).append(" targetURL='").append(Config.getContextPath() + this.getSrc()).append("' class='z-tab z-tab-disabled'");
		} else {
			if (this.lazy)
				this.src = ("src='javascript:void(0);' targetURL=\"" + Config.getContextPath() + this.getSrc() + "\"");
			else {
				this.src = ("src=\"" + this.getSrc() + "\" targetURL=\"" + Config.getContextPath() + this.getSrc() + "\"");
			}
			StringFormat sf = new StringFormat("id='?' ? class='z-tab?' ? onclick=\"?;Ark.TabPage.onChildTabClick(this);?\">");
			sf.add(this.getId());
			sf.add(getVisiableString());
			sf.add(getType().equals("Current") ? " z-tab-current" : "");
			sf.add(this.getSrc());
			sf.add(this.getOnClick());
			sf.add(this.getAfterClick());
			sb.append(sf.toString());
		}
		if("iframe".equals(displayType)) {
			sb.append(content);
		} else {
			if(!StringUtil.isEmpty(img)) {
				sb.append("<img src='"+img+"' />");
			}
			sb.append("<b>"+title+"</b>");
		}
		sb.append("</a>");

		ArrayList<String[]> children = (ArrayList<String[]>) Current.getVariable(Tab.TabTagKey);
		if (children != null) {
			children.add(new String[] { this.id, this.src, String.valueOf(this.selected), displayType, content });
		}

		return sb.toString();
	}
	
	private Object getVisiableString() {
		
		if(this.visible) {
			return "";
		}
		return "style='display:none'";
	}

	private String getType() {
		String type = "";
		if (this.selected) {
			type = "Current";
		} else if (this.disabled) {
			type = "Disabled";
		}
		return type;
	}

	public String getId() {
		if (StringUtil.isEmpty(this.id)) {
			this.id = (No++) + "";
		}
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOnClick() {
		if (this.onClick == null) {
			this.onClick = "";
		}
		if ((StringUtil.isNotEmpty(this.onClick)) && (!this.onClick.trim().endsWith(";"))) {
			this.onClick = (this.onClick.trim() + ";");
		}
		return this.onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public String getAfterClick() {
		if (this.afterClick == null) {
			this.afterClick = "";
		}
		return this.afterClick;
	}

	public void setAfterClick(String afterClick) {
		this.afterClick = afterClick;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getSrc() {
		return this.src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isLazy() {
		return this.lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public String getDisplayType() {
		return displayType;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

}
