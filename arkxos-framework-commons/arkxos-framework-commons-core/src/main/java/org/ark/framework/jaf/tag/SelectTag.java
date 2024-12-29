package org.ark.framework.jaf.tag;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import jakarta.servlet.jsp.tagext.Tag;

import org.ark.framework.jaf.Current;
import org.ark.framework.jaf.PlaceHolder;
import org.ark.framework.jaf.PlaceHolderContext;
import org.ark.framework.jaf.html.HtmlSelect;
import org.ark.framework.security.PrivCheck;

import com.rapidark.framework.Account;
import com.rapidark.framework.Config;
import com.rapidark.framework.commons.collection.DataTable;
import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.commons.util.ObjectUtil;
import com.rapidark.framework.commons.util.ServletUtil;
import com.rapidark.framework.commons.util.StringUtil;
import com.rapidark.framework.cosyui.CodeSourceUI;
import com.rapidark.framework.data.db.DataCollection;
import com.rapidark.framework.i18n.LangUtil;


/**
 * @class org.ark.framework.jaf.tag.SelectTag
 * <h2>Select标签</h2>
 * <br/>
 * <img src="images/SelectTag_1.png"/>
 * <br/>&lt;ark:select id="parentInnerCode" condition="$V('branchInnerCode').length!=4" value="${parentInnerCode}" method="Branch.getBranchTable" defaultBlank="true" verify="NotNull" style="width:200px"/>
 * <br/>
 * <br/><b>select下拉页面</b>
 * <br/>&lt;ark:select id="Site" listURL="ContentCore/SiteSelectDialog.zhtml" autowidth="true" 
	<br/>			 										listWidth="250" listHeight="300" style="height:16px;" value="${SiteID}"
<br/>		 										valueText="${SiteName}" onchange="onSiteChange()" />
<br/>
	<br/><b>listUrl中的选择事件</b>
<br/>function onTreeClick(ele){
<br/>&nbsp;&nbsp;&nbsp;&nbsp;	var v =  ele.getAttribute("cid");
<br/>&nbsp;&nbsp;&nbsp;&nbsp;	if(!v){
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;		return;
<br/>&nbsp;&nbsp;&nbsp;&nbsp;	}
<br/>&nbsp;&nbsp;&nbsp;&nbsp; 	var t = ele.innerText;
<br/>&nbsp;&nbsp;&nbsp;&nbsp;	Selector.setReturn(t,v);
<br/>}
 * @author Darkness
 * @date 2013-1-31 下午12:44:19 
 * @version V1.0
 */
public class SelectTag extends BodyTagSupport {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private String onChange;
	private String style;
	private int listWidth;
	private int listHeight;
	private String listURL;
	private String verify;
	private String condition;
	private String value;
	private String valueText;
	private String className;
	private boolean disabled;
	private boolean input;
	private String code;
	private String conditionField;
	private String conditionValue;
	private boolean autowidth;
	private boolean showValue;
	private boolean lazy;
	private boolean defaultblank;
	private String method;
	private int selectedIndex = 0;

	private int optionCount = 0;
	protected String options;
	public static final Pattern POption = Pattern.compile("<(span|option).*?value=(\\\"|\\')(.*?)\\2.*?>(.*?)</(span|option)>", 34);

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.id = null;
		this.name = null;
		this.className = null;
		this.code = null;
		this.method = null;
		this.condition = null;
		this.conditionField = null;
		this.conditionValue = null;
		this.disabled = false;
		this.input = false;
		this.showValue = false;
		this.value = null;
		this.valueText = null;
		this.verify = null;
		this.onChange = null;
		this.style = null;
		this.defaultblank = false;
		this.listWidth = 0;
		this.listHeight = 0;
		this.listURL = null;
		this.lazy = false;
		this.autowidth = false;
		this.selectedIndex = 0;
		this.optionCount = 0;
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

	public int doEndTag() throws JspException {
		if ((this.bodyContent == null) || (StringUtil.isEmpty(this.bodyContent.getString()))) {
			try {
				this.pageContext.getOut().print(getHtml(""));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 6;
	}

	public static Object getRealValue(String value, Tag tag, PageContext pageContext) {
		if ((value != null) && (value.startsWith("${"))) {
			PlaceHolderContext context = PlaceHolderContext.getInstance(tag, pageContext);
			Object v = context.eval(new PlaceHolder(value));
			if (v == null) {
				return value;
			}
			return v;
		}
		return value;
	}

	public String getHtml(String content) {
		content = parseOptions(content);
		String codeData = "";
		if ((StringUtil.isNotEmpty(this.code)) || (StringUtil.isNotEmpty(this.method)) || (StringUtil.isNotEmpty(this.options))) {
			codeData = codeData + getCodeData();
		}
		if (StringUtil.isEmpty(this.id)) {
			this.id = "_ARK_NOID_";
		}
		if (StringUtil.isEmpty(this.name)) {
			this.name = this.id;
		}

		this.value = ObjectUtil.toString(getRealValue(this.value, this, this.pageContext));
		StringBuilder sb = new StringBuilder();
		sb.append("<div id=\"").append(this.id).append("\" selectedIndex=\"").append(this.selectedIndex).append("\" name=\"").append(this.name).append("\" ztype=\"Select\"");
		if (StringUtil.isNotEmpty(this.className)) {
			sb.append("  class=\"").append(this.className);
			sb.append(" zSelect\"");
		} else {
			sb.append(" class=\"zSelect\"");
		}
		if (StringUtil.isNotEmpty(this.style)) {
			sb.append(" style=\"display:inline-block; *zoom: 1;*display: inline;vertical-align:middle;")
					.append("height:auto;width:auto;position:relative;border:none 0;margin:0;padding:0;white-space: nowrap").append("\"");
			sb.append(" styleOriginal=\"").append(this.style).append("\"");
		} else {
			sb.append(" style=\"display:inline-block; *zoom: 1;*display: inline;vertical-align:middle;")
					.append("height:auto;width:auto;position:relative;border:none 0;margin:0;padding:0;white-space: nowrap;").append("\"");
			sb.append(" styleOriginal=\"NULL\"");
		}
		if (StringUtil.isNotEmpty(this.onChange)) {
			sb.append(" onChange=\"").append(this.onChange).append("\"");
		}
		if (this.disabled) {
			sb.append(" disabled=\"").append(this.disabled).append("\"");
		}
		if (StringUtil.isNotEmpty(this.className)) {
			sb.append(" zclass=\"").append(this.className).append("\"");
		}
		if (this.listWidth > 0) {
			sb.append(" listWidth=\"").append(this.listWidth).append("\"");
		}
		if (this.listHeight > 0) {
			sb.append(" listHeight=\"").append(this.listHeight).append("\"");
		}
		if (StringUtil.isNotEmpty(this.listURL)) {
			sb.append(" listURL=\"").append(this.listURL).append("\"");
		}
		if (StringUtil.isNotEmpty(this.verify)) {
			sb.append(" verify=\"").append(this.verify).append("\"");
		}
		if (StringUtil.isNotEmpty(this.condition)) {
			sb.append(" condition=\"").append(this.condition).append("\"");
		}
		if (this.input) {
			sb.append(" input=\"").append(this.input).append("\"");
		}
		if (this.lazy) {
			sb.append(" lazy=\"").append(this.lazy).append("\"");
		}
		if (this.autowidth) {
			sb.append(" autowidth=\"").append(this.autowidth).append("\"");
		}
		if (this.showValue) {
			sb.append(" showValue=\"").append(this.showValue).append("\"");
		}
		if (StringUtil.isNotEmpty(this.code)) {
			sb.append(" code=\"").append(this.code).append("\"");
		}
		if (StringUtil.isNotEmpty(this.method)) {
			sb.append(" method=\"").append(this.method).append("\"");
		}
		sb.append(" defaultblank=\"").append(this.defaultblank).append("\"");
		if (StringUtil.isNotEmpty(this.conditionField)) {
			sb.append(" conditionField=\"").append(this.conditionField).append("\"");
		}
		if (StringUtil.isNotEmpty(this.conditionValue)) {
			sb.append(" conditionValue=\"").append(this.conditionValue).append("\"");
		}
		if (StringUtil.isNotEmpty(this.value)) {
			sb.append(" value=\"").append(this.value).append("\"");
			sb.append(" initValue=\"").append(this.value).append("\"");
		}
		sb.append(">");

		sb.append("<input type=\"text\" id=\"").append(this.id).append("_textField\" ztype=\"select\" autocomplete=\"off\"");
		if (StringUtil.isNotEmpty(this.verify)) {
			sb.append(" verify=\"").append(this.verify).append("\"");
		}
		if (StringUtil.isNotEmpty(this.className)) {
			sb.append("  class=\"").append(this.className).append(" ");
			sb.append(" inputText\"");
		} else {
			sb.append(" class=\"inputText\"");
		}
		if (StringUtil.isNotEmpty(this.condition)) {
			sb.append(" condition=\"").append(this.condition).append("\"");
		}
		if (StringUtil.isNotEmpty(this.style))
			sb.append(" style=\"vertical-align:middle; cursor:default;").append(this.style).append("\"");
		else {
			sb.append(" style=\"vertical-align:middle; cursor:default;\"");
		}
		this.valueText = LangUtil.get(this.valueText);
		sb.append(" value=\"" + (this.valueText == null ? "" : this.valueText) + "\"/>");

		sb.append("<input type=\"hidden\" name=\"" + this.name + "\"");
		if (StringUtil.isNotEmpty(this.value)) {
			sb.append(" value=\"").append(this.value).append("\"");
			sb.append(" initValue=\"").append(this.value).append("\"");
		}
		sb.append(">");
		sb.append("<img class=\"arrowimg\" src=\"").append(Config.getContextPath()).append("Framework/Images/blank.gif\" width=\"18\" height=\"20\" id=\"").append(this.id)
				.append("_arrow\" style=\"position:relative; left:-18px; margin-right:-19px; cursor:pointer; ").append("width:18px; height:20px;vertical-align:middle;\"/>");
		sb.append("<div id=\"").append(this.id).append("_list\" class=\"optgroup\" style=\"text-align:left;display:none;\">");
		sb.append("<div id=\"").append(this.id).append("_ul\" style=\"left:-1px; width:-1px;\">");

		if (this.defaultblank) {
			sb.append(getOption("", ""));
		}
		sb.append(content);

		if (StringUtil.isNotEmpty(codeData)) {
			sb.append(codeData);
		}
		sb.append("</div></div></div>");
		return sb.toString();
	}

	private String getOption(String text, String value) {
		this.optionCount += 1;
		return getOptionHtml(text, value, false);
	}

	public static String getOptionHtml(String text, String value, boolean flag) {
		text = LangUtil.get(text);
		return "<a href=\"javascript:void(0);\" onclick=\"Ark.Selector.onItemClick(this);\" onmouseover=\"Ark.Selector.onItemMouseOver(this)\" " + (flag ? "selected=\"true\"" : "")
				+ " hidefocus value=\"" + value + "\">" + text + "</a>";
	}

	private String parseOptions(String content) {
		if (content.indexOf("<select") >= 0) {
			HtmlSelect select = new HtmlSelect();
			try {
				select.parseHtml(content);
				if (StringUtil.isEmpty(this.id)) {
					this.id = select.getID();
				}
				if (StringUtil.isEmpty(this.className)) {
					this.className = select.getClassName();
				}
				if (StringUtil.isEmpty(this.style)) {
					this.style = select.getStyle();
				}
				if (StringUtil.isEmpty(this.code)) {
					this.code = select.getAttribute("code");
				}
				if (StringUtil.isEmpty(this.code)) {
					this.code = select.getAttribute("code");
				}
				if (StringUtil.isEmpty(this.condition)) {
					this.condition = select.getAttribute("condition");
				}
				if (StringUtil.isEmpty(this.conditionField)) {
					this.conditionField = select.getAttribute("conditionfield");
				}
				if (StringUtil.isEmpty(this.conditionValue)) {
					this.conditionValue = select.getAttribute("conditionvalue");
				}
				if (StringUtil.isEmpty(this.method)) {
					this.method = select.getAttribute("method");
				}
				if ("true".equals(select.getAttribute("disabled"))) {
					this.disabled = true;
				}
				if ("true".equals(select.getAttribute("input"))) {
					this.input = true;
				}
				if ("true".equals(select.getAttribute("defaultblank"))) {
					this.defaultblank = true;
				}
				if ("true".equals(select.getAttribute("showvalue"))) {
					this.showValue = true;
				}
				if ("true".equals(select.getAttribute("lazy"))) {
					this.lazy = true;
				}
				if ("true".equals(select.getAttribute("autowidth")))
					this.autowidth = true;
				try {
					if (Integer.parseInt(select.getAttribute("listwidth")) > 0)
						this.listWidth = Integer.parseInt(select.getAttribute("listwidth"));
				} catch (Exception localException1) {
				}
				try {
					if (Integer.parseInt(select.getAttribute("listheight")) > 0)
						this.listHeight = Integer.parseInt(select.getAttribute("listheight"));
				} catch (Exception localException2) {
				}
				if (StringUtil.isEmpty(this.value)) {
					this.value = select.getAttribute("value");
				}
				if (StringUtil.isEmpty(this.verify)) {
					this.verify = select.getAttribute("verify");
				}
				if (StringUtil.isEmpty(this.onChange)) {
					this.onChange = select.getAttribute("onchange");
				}
				if (StringUtil.isEmpty(this.listURL)) {
					this.listURL = select.getAttribute("listurl");
				}
				if (StringUtil.isEmpty(this.options)) {
					this.options = select.getAttribute("options");
				}
				content = select.getInnerHTML();
			} catch (Exception e) {
				if (StringUtil.isEmpty(this.id)) {
					throw new RuntimeException("Must set a ID value for <z:select> or <select>");
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		Matcher m = POption.matcher(content);
		int lastIndex = 0;
		int i = 0;
		while (m.find(lastIndex)) {
			String tmp = content.substring(lastIndex, m.start());
			if (StringUtil.isNotEmpty(tmp.trim())) {
				sb.append(tmp);
			}
			if (this.showValue)
				sb.append(getOption(m.group(3) + (StringUtil.isNotEmpty(m.group(4)) ? "-" + m.group(4) : ""), m.group(3)));
			else {
				sb.append(getOption(m.group(4), m.group(3)));
			}
			if (m.group().toLowerCase().substring(0, m.group().indexOf(">")).indexOf("selected") > 0) {
				this.selectedIndex = i;
			}
			if (m.group(3).equals(this.value)) {
				this.selectedIndex = i;
			}
			lastIndex = m.end();
			i++;
		}
		if (lastIndex != content.length() - 1) {
			sb.append(content.substring(lastIndex));
		}
		if (i != 0) {
			content = sb.toString();
		}
		return content;
	}

	private String getCodeData() {
		if (this.lazy) {
			return "";
		}
		Mapx<String, String> params = ServletUtil.getParameterMap((HttpServletRequest) this.pageContext.getRequest());
		if (StringUtil.isNotEmpty(this.conditionField)) {
			params.put("ConditionField", this.conditionField);
			params.put("ConditionValue", this.conditionValue);
		} else {
			params.put("ConditionField", "1");
			params.put("ConditionValue", "1");
		}
		DataTable dt = null;//getCodeData(this.code, this.method, this.options, this.pageContext, params);
		StringBuilder sb = new StringBuilder();
		if (dt != null) {
			for (int i = 0; i < dt.getRowCount(); i++) {
				if (dt.getString(i, 0).equals(this.value)) {
					this.selectedIndex = this.optionCount;
				}
				if (!this.showValue)
					sb.append(getOption(dt.getString(i, 1), dt.getString(i, 0)));
				else {
					sb.append(getOption(dt.getString(i, 0) + "-" + dt.getString(i, 1), dt.getString(i, 0)));
				}
			}
			if (dt.getColumnCount() > 2) {
				sb.append("<script>Ark.Page.onLoad(Selector_").append(this.id).append("_Init,10);");
				sb.append("function Selector_").append(this.id).append("_Init(){");
				sb.append(DataCollection.dataTableToJS(dt));
				sb.append("Ark.getDom(\"").append(this.id).append("\").DataSource = new DataTable();");
				sb.append("Ark.getDom(\"").append(this.id).append("\").DataSource.init(_Ark_Cols,_Ark_Values);");
				sb.append("window.attachEvent('onunload', function(){Ark.Selector.destroy('").append(this.id).append("')});");
				sb.append("}\n</script>\n");
			}
		}
		return sb.toString();
	}

	public static DataTable getCodeData(String code, String method, String options, PageContext pageContext, Mapx<String, Object> params) {
		DataTable dt = null;
		if (ObjectUtil.notEmpty(options)) {
			dt = new DataTable();
			dt.insertColumn("Value");
			dt.insertColumn("Key");
			String[] arr = StringUtil.splitEx(options, ",");
			for (String str : arr) {
				String[] arr2 = StringUtil.splitEx(str, ":");
				String k = arr2[0];
				k = StringUtil.replaceEx(k, "\\,", ",");
				k = StringUtil.replaceEx(k, "\\:", ":");
				String v = arr2[1];
				v = StringUtil.replaceEx(v, "\\,", ",");
				v = StringUtil.replaceEx(v, "\\:", ":");
				k = LangUtil.get(k);
				dt.insertRow(new String[] { v, k });
			}
			return dt;
		}
		if ((ObjectUtil.empty(method)) && (code.startsWith("#"))) {
			method = code.substring(1);
		}
		if (ObjectUtil.notEmpty(method))
			try {
				
				Method m = Current.prepareMethod((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), method, null);
				if (!PrivCheck.check(m, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse())) {
					return null;
				}
				Object o = Current.invokeMethod(m, null);
				if (o == null) {
					o = new DataTable();
				}
				dt = (DataTable) o;
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(method + " must return DataTable");
			}
		else {
			dt = CodeSourceUI.getCodeSourceInstance().getCodeData(code, params);
		}
		LangUtil.decodeDataTable(dt, Account.getLanguage());
		return dt;
	}

	public String getCondition() {
		return this.condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getListWidth() {
		return this.listWidth;
	}

	public void setListWidth(int listWidth) {
		this.listWidth = listWidth;
	}

	public String getOnChange() {
		return this.onChange;
	}

	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	public String getStyle() {
		return this.style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getVerify() {
		return this.verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	public int getListHeight() {
		return this.listHeight;
	}

	public void setListHeight(int listHeight) {
		this.listHeight = listHeight;
	}

	public String getListURL() {
		return this.listURL;
	}

	public void setListURL(String listURL) {
		this.listURL = listURL;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean getDisabled() {
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

	public boolean isInput() {
		return this.input;
	}

	public void setInput(boolean input) {
		this.input = input;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getConditionField() {
		return this.conditionField;
	}

	public void setConditionField(String conditionField) {
		this.conditionField = conditionField;
	}

	public String getConditionValue() {
		return this.conditionValue;
	}

	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}

	public boolean getShowValue() {
		return this.showValue;
	}

	public void setShowValue(boolean showValue) {
		this.showValue = showValue;
	}

	public boolean getInput() {
		return this.input;
	}

	public boolean isLazy() {
		return this.lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public boolean isAutowidth() {
		return this.autowidth;
	}

	public void setAutowidth(boolean autowidth) {
		this.autowidth = autowidth;
	}

	public boolean isDefaultblank() {
		return this.defaultblank;
	}

	public void setDefaultblank(boolean defaultblank) {
		this.defaultblank = defaultblank;
	}

	public boolean isDefaultBlank() {
		return this.defaultblank;
	}

	public void setDefaultBlank(boolean defaultblank) {
		this.defaultblank = defaultblank;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getOnchange() {
		return this.onChange;
	}

	public void setOnchange(String onchange) {
		this.onChange = onchange;
	}

	public String getOptions() {
		return this.options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getValueText() {
		return this.valueText;
	}

	public void setValueText(String valueText) {
		this.valueText = valueText;
	}
}