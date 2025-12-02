package org.ark.framework.jaf.tag;

import java.io.IOException;

import org.ark.framework.jaf.expression.Primitives;
import org.ark.framework.jaf.html.HtmlTable;

import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.Html2Util;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.ServletUtil;
import io.arkx.framework.commons.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

/**
 * @class org.ark.framework.jaf.tag.CheckboxTag
 *
 *        <h2>Checkbox标签</h2> <br/>
 *        <img src="images/CheckboxTag_1.png"/> <br/>
 *        &lt;ark:checkbox method="IndexList.getIndexTypes" value="Article"
 *        id="IndexType" />
 * @author Darkness
 * @date 2013-1-31 下午12:40:03
 * @version V1.0
 */
public class CheckboxTag extends BodyTagSupport {

    private static final long serialVersionUID = 1L;

    protected String code;
    protected String id;
    protected String method;
    protected String name;
    protected boolean tableLayout;
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

    public void setPageContext(PageContext pc) {
        super.setPageContext(pc);
        this.id = null;
        this.name = null;
        this.tableLayout = false;
        this.tableWidth = null;
        this.code = null;
        this.method = null;
        this.column = 0;
        this.disabled = null;
        this.value = null;
        this.onChange = null;
        this.defaultCheck = null;
        this.type = "checkbox";
        this.theme = null;
    }

    public int doStartTag() throws JspException {
        try {
            this.pageContext.getOut().print(getHtml());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getHtml() {

        Mapx<String, String> map = ServletUtil.getParameterMap((HttpServletRequest) this.pageContext.getRequest());
        DataTable dt = null;// SelectTag.getCodeData(this.code, this.method, this.options, this.pageContext,
                            // map);
        if ((dt == null) || (dt.getRowCount() == 0)) {
            return "";
        }
        if (StringUtil.isEmpty(this.id)) {
            this.id = "_ARK_NOID_";
        }
        if (StringUtil.isEmpty(this.name)) {
            this.name = this.id;
        }
        this.value = ObjectUtil.toString(SelectTag.getRealValue(this.value, this, this.pageContext));
        if ((StringUtil.isEmpty(this.value)) || (this.value.startsWith("${"))) {
            this.value = null;
        }
        if (this.value != null) {
            this.value = ("," + this.value + ",");
        }
        boolean disabledFlag = Primitives.getBoolean(SelectTag.getRealValue(this.disabled, this, this.pageContext));
        dt.getDataColumn(0).setColumnName("Key");
        dt.getDataColumn(1).setColumnName("Value");
        dt.insertColumn("RowNo");
        dt.insertColumn("Checked");
        dt.insertColumn("OnClick");
        dt.insertColumn("Disabled");
        if (StringUtil.isNotNull(this.defaultCheck)) {
            this.defaultCheck = ("," + this.defaultCheck + ",");
        }
        for (int i = 0; i < dt.getRowCount(); i++) {
            dt.set(i, "RowNo", i);
            String v = "," + dt.getString(i, "Key") + ",";
            dt.set(i, "Checked", "");
            dt.set(i, "OnClick", "");
            dt.set(i, "Disabled", "");
            if (this.value != null) {
                if ((StringUtil.isNotNull(this.value)) && (this.value.indexOf(v) >= 0)) {
                    dt.set(i, "Checked", "checked=\"true\"");
                }
            } else if ((StringUtil.isNotNull(this.defaultCheck)) && (this.defaultCheck.indexOf(v) >= 0)) {
                dt.set(i, "Checked", "checked=\"true\"");
            }

            if (this.onClick != null) {
                dt.set(i, "OnClick", this.onClick);
            }
            if (disabledFlag) {
                dt.set(i, "Disabled", "disabled=\"disabled\"");
            }
        }
        String hasRowNo = "_${RowNo}";
        if (dt.getRowCount() == 1) {
            hasRowNo = "";
        }
        String jsClassName = this.type.replaceFirst("checkbox", "Checkbox").replaceFirst("radio", "Radio");
        String html = "<input type=\"" + this.type + "\" ${Disabled} ${Checked} id=\"" + this.name + hasRowNo
                + "\" name=\"" + this.name + "\" value=\"${Key}\" onclick=\"${OnClick}\"";
        if (StringUtil.isNotEmpty(this.theme)) {
            html = html + " class=\"z-" + this.theme + "-hide\"";
            html = html + "><label for=\"" + this.name + hasRowNo + "\"";
            html = html + " class=\"z-" + this.theme + "-label\"";
            html = html + ">${Value}</label>";
            html = html + "<script>if(Ark." + jsClassName + "){new Ark." + jsClassName + "('" + this.name + hasRowNo
                    + "');}</script>";
        } else {
            html = html + "><label for=\"" + this.name + hasRowNo + "\"";
            html = html + ">${Value}</label>";
        }
        if (this.tableLayout) {
            if (this.column < 1) {
                this.column = 1;
            }
            if (StringUtil.isNotEmpty(this.value)) {
                this.value = ("," + this.value + ",");
            }
            HtmlTable table = Html2Util.dataTableToHtmlTable(dt, html, this.column);
            table.setAttribute("width", this.tableWidth);
            html = table.getOuterHtml();
            return html;
        }
        return Html2Util.replaceWithDataTable(dt, html, false);
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTableLayout() {
        return this.tableLayout;
    }

    public void setTableLayout(boolean tableLayout) {
        this.tableLayout = tableLayout;
    }

    public String getTableWidth() {
        return this.tableWidth;
    }

    public void setTableWidth(String tableWidth) {
        this.tableWidth = tableWidth;
    }

    public int getColumn() {
        return this.column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getOnChange() {
        return this.onChange;
    }

    public void setOnChange(String onChange) {
        this.onChange = onChange;
    }

    public String getOnClick() {
        return this.onClick;
    }

    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDisabled() {
        return this.disabled;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public String getDefaultCheck() {
        return this.defaultCheck;
    }

    public void setDefaultCheck(String defaultCheck) {
        this.defaultCheck = defaultCheck;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTheme() {
        return this.theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getOptions() {
        return this.options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public static long getSerialversionuid() {
        return 1L;
    }

    public String getOnclick() {
        return this.onClick;
    }

    public void setOnclick(String onClick) {
        this.onClick = onClick;
    }
}
