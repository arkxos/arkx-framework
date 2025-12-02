package io.arkx.framework.cosyui.control;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.FrameworkPlugin;
import io.arkx.framework.WebCurrent;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.HtmlUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.expression.ExpressionException;
import io.arkx.framework.cosyui.html.HtmlTable;
import io.arkx.framework.cosyui.tag.ArkTag;
import io.arkx.framework.cosyui.template.TagAttr;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;
import io.arkx.framework.security.exception.PrivException;

/**
 * 多选框标签 。<br>
 * 注意：设置defaultCheck="_ALL"则会选中所有多选框
 *
 */
public class CheckboxTag extends ArkTag {

	private static final long serialVersionUID = 1L;

	protected String code;

	protected String id;

	protected String method;

	protected String rest;

	protected String name;

	protected boolean tableLayout;// 是否用<table>来排列

	protected String tableWidth;

	protected int column;

	protected String onChange;

	protected String onClick;

	protected String value;

	protected String disabled;

	protected String defaultCheck;

	protected String type;

	protected String theme;

	protected String options;

	@Override
	public String getTagName() {
		return "checkbox";
	}

	@Override
	public void init() throws ExpressionException {
		super.init();
		type = "checkbox";
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		pageContext.getOut().write(getHtml());
		return SKIP_BODY;
	}

	public String getHtml() {
		Mapx<String, Object> map = WebCurrent.getRequest();
		try {
			DataTable dt = SelectTag.getCodeData(code, method, rest, options, pageContext, map);
			if (dt == null || dt.getRowCount() == 0) {
				return "";
			}
			if (StringUtil.isEmpty(id)) {
				id = "_ARK_NOID_";
			}
			if (StringUtil.isEmpty(name)) {
				name = id;
			}
			if (StringUtil.isEmpty(value) || value.startsWith("${")) {
				value = null;
			}
			if (value != null) {
				value = "," + value + ",";
			}
			boolean disabledFlag = "true".equals(disabled);
			dt.getDataColumn(0).setColumnName("Key");
			dt.getDataColumn(1).setColumnName("Value");
			dt.insertColumn("RowNo");
			dt.insertColumn("Checked");
			dt.insertColumn("OnClick");
			dt.insertColumn("OnChange");
			dt.insertColumn("Disabled");
			if (StringUtil.isNotNull(defaultCheck)) {
				defaultCheck = "," + defaultCheck + ",";
			}
			for (int i = 0; i < dt.getRowCount(); i++) {
				dt.set(i, "RowNo", "" + i);
				String v = "," + dt.getString(i, "Key") + ",";
				dt.set(i, "Checked", "");
				dt.set(i, "OnClick", "");
				dt.set(i, "OnChange", "");
				dt.set(i, "Disabled", "");
				if (value != null) {
					if (StringUtil.isNotNull(value) && value.indexOf(v) >= 0) {
						dt.set(i, "Checked", "checked=\"true\"");
					}
				}
				else {
					if (StringUtil.isNotNull(defaultCheck) && defaultCheck.indexOf(v) >= 0) {
						dt.set(i, "Checked", "checked=\"true\"");
					}
				}
				if (",_ALL,".equals(defaultCheck)) {// _ALL则选中所有
					dt.set(i, "Checked", "checked=\"true\"");
				}
				if (onClick != null) {
					dt.set(i, "OnClick", onClick);
				}
				if (onChange != null) {
					dt.set(i, "OnChange", onChange);
				}
				if (disabledFlag) {
					dt.set(i, "Disabled", "disabled=\"disabled\"");
				}
			}
			String hasRowNo = "_${RowNo}";
			if (dt.getRowCount() == 1) {// 如果只有一个可选项，则ID和名字相同
				hasRowNo = "";
			}
			String jsClassName = type.replaceFirst("checkbox", "Checkbox").replaceFirst("radio", "Radio");
			String html = "<input type=\"" + type + "\" ${Disabled} ${Checked} id=\"" + name + hasRowNo + "\" name=\""
					+ name + "\" value=\"${Key}\" onclick=\"${OnClick}\"";
			if (onChange != null) {
				html += " onchange=\"${OnChange}\"";
			}
			if (StringUtil.isNotEmpty(theme)) {// 如果只有一个可选项，则ID和名字相同
				html += " class=\"z-" + theme + "-hide\"";
				html += "><label for=\"" + name + hasRowNo + "\"";
				html += " class=\"z-" + theme + "-label\"";
				html += ">${Value}</label>";
				html += "<script>if(Ark." + jsClassName + "){new Ark." + jsClassName + "('" + name + hasRowNo
						+ "');}</script>";
			}
			else {
				html += "><label for=\"" + name + hasRowNo + "\"";
				html += ">${Value}</label>";
			}
			if (tableLayout) {
				if (column < 1) {
					column = 1;
				}
				if (StringUtil.isNotEmpty(value)) {
					value = "," + value + ",";
				}
				HtmlTable table = HtmlUtil.dataTableToHtmlTable(dt, html, column);
				table.addAttribute("width", tableWidth);
				html = table.getOuterHTML();
				return html;
			}
			else {
				return HtmlUtil.replaceWithDataTable(dt, html, false);
			}
		}
		catch (PrivException e) {
			e.printStackTrace();
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isTableLayout() {
		return tableLayout;
	}

	public void setTableLayout(boolean tableLayout) {
		this.tableLayout = tableLayout;
	}

	public String getTableWidth() {
		return tableWidth;
	}

	public void setTableWidth(String tableWidth) {
		this.tableWidth = tableWidth;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public String getOnChange() {
		return onChange;
	}

	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public String getDefaultCheck() {
		return defaultCheck;
	}

	public void setDefaultCheck(String defaultCheck) {
		this.defaultCheck = defaultCheck;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getOnclick() {
		return onClick;
	}

	public void setOnclick(String onClick) {
		this.onClick = onClick;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<TagAttr>();
		list.add(new TagAttr("id", true));
		list.add(new TagAttr("code"));
		list.add(new TagAttr("defaultCheck"));
		list.add(new TagAttr("method"));
		list.add(new TagAttr("name"));
		list.add(new TagAttr("onchange"));
		list.add(new TagAttr("onclick"));
		list.add(new TagAttr("options"));
		list.add(new TagAttr("tableWidth"));
		list.add(new TagAttr("theme"));
		list.add(new TagAttr("type"));
		list.add(new TagAttr("value"));
		list.add(new TagAttr("disabled", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("tableLayout", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("column", DataTypes.INTEGER.code()));
		return list;
	}

	@Override
	public String getDescription() {
		return "@{Framework.ZcheckBoxTagDescription}";
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.ZcheckBoxTagName}";
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

	public String getRest() {
		return rest;
	}

	public void setRest(String rest) {
		this.rest = rest;
	}

}
