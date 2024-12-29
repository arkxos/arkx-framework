package com.arkxos.framework.cosyui.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.collection.DataTypes;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.lang.FastStringBuilder;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.core.JsonResult;
import com.arkxos.framework.core.method.IMethodLocator;
import com.arkxos.framework.core.method.MethodLocatorUtil;
import com.arkxos.framework.cosyui.CodeSourceUI;
import com.arkxos.framework.cosyui.UIException;
import com.arkxos.framework.cosyui.html.HtmlElement;
import com.arkxos.framework.cosyui.html.HtmlNode;
import com.arkxos.framework.cosyui.html.HtmlParser;
import com.arkxos.framework.cosyui.tag.ArkTag;
import com.arkxos.framework.cosyui.tag.RestUtil;
import com.arkxos.framework.cosyui.template.AbstractExecuteContext;
import com.arkxos.framework.cosyui.template.TagAttr;
import com.arkxos.framework.cosyui.template.TemplateCompiler;
import com.arkxos.framework.cosyui.template.TemplateExecutor;
import com.arkxos.framework.cosyui.template.command.TagCommand;
import com.arkxos.framework.cosyui.template.exception.TemplateRuntimeException;
import com.arkxos.framework.cosyui.util.TagUtil;
import com.arkxos.framework.cosyui.web.RequestData;
import com.arkxos.framework.data.db.DataCollection;
import com.arkxos.framework.i18n.LangUtil;
import com.arkxos.framework.security.PrivCheck;
import com.rapidark.framework.Config;
import com.rapidark.framework.Current;
import com.rapidark.framework.FrameworkPlugin;

/**
 * 下拉框标签
 * 
 */
public class SelectTag extends ArkTag {
	public static final String Var = "Select_ScriptData";

	String id;

	String name;

	String onChange;

	String style;

	int listWidth;

	int listHeight;

	String listURL;

	String verify;

	String condition;

	String value;

	String valueText;// 设置有listURL时需要通过此属性来设置初始化时显示的文本

	String className;

	boolean disabled;

	boolean input;

	String code;

	String conditionField;

	String conditionValue;

	boolean autowidth;

	boolean showValue;

	boolean lazy;

	boolean defaultblank;

	String method;
	String rest;
	Object data;
	
	private int selectedIndex = -1;

	private int optionCount = 0;
	private boolean nativeRender = false;
	String options;

	protected void appendAttribute(FastStringBuilder sb, String attrName, String value) {
		if (StringUtil.isNotEmpty(value)) {
			sb.append(attrName).append("=\"").append(value).append("\" ");
		}
	}
	
	@Override
	public String getTagName() {
		return "select";
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {// 准备环境变量
		if (ObjectUtil.isEmpty(id)) {
			id = TagUtil.getTagID(pageContext, "Combox");
			variables.put("ID", id);
		}
		if ((StringUtil.isNotEmpty(this.code)) 
				|| (StringUtil.isNotEmpty(this.method)) 
				|| (StringUtil.isNotEmpty(this.rest)) 
				|| (StringUtil.isNotEmpty(this.options)) 
				|| (ObjectUtil.notEmpty(this.data))) {
			FastStringBuilder sb = new FastStringBuilder();
			if (this.nativeRender) {
				outNativeSelect(sb);
				this.context.getOut().write(sb.toStringAndClose());
			} else {
				getScript4Data(sb);
				variables.put(Var, sb.toStringAndClose());
			}
		}
		return EVAL_PAGE;
	}

	protected void outNativeSelect(FastStringBuilder sb) {
		sb.append("<select ");
		appendAttribute(sb, "name", this.name);
		appendAttribute(sb, "id", this.id);
		appendAttribute(sb, "class", this.className);
		appendAttribute(sb, "style", this.style);
		sb.append("> ");
		Mapx<String, Object> params = Current.getRequest();
		if (StringUtil.isNotEmpty(this.conditionField)) {
			params.put("ConditionField", this.conditionField);
			params.put("ConditionValue", this.conditionValue);
		} else {
			params.put("ConditionField", "1");
			params.put("ConditionValue", "1");
		}
		if (this.defaultblank) {
			sb.append("<option value=\"\"> </option>");
		}
		if (ObjectUtil.empty(this.data)) {
			this.data = getCodeData(this.code, this.method, this.rest, this.options, this.pageContext, params);
		}
		String opText;
		if ((this.data instanceof DataTable)) {
			DataTable dt = (DataTable) this.data;
			for (int i = 0; i < dt.getRowCount(); i++) {
				opText = dt.getString(i, 1);
				String opValue = dt.getString(i, 0);
				sb.append("<option ");
				appendAttribute(sb, "value", opValue);
				if ((StringUtil.isNotEmpty(this.value)) && (dt.getString(i, 0).equals(this.value))) {
					appendAttribute(sb, "selected", "true");
				}
				sb.append('>');
				sb.append(opText);
				sb.append("</option>");
			}
		} else if ((this.data instanceof Map)) {
			Map<Object, Object> map = (Map) this.data;
			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				String itemKey = entry.getKey() == null ? "" : entry.getKey().toString();
				String itemValue = entry.getValue() == null ? "" : entry.getValue().toString();
				sb.append("<option ");
				appendAttribute(sb, "value", itemKey);
				if ((StringUtil.isNotEmpty(this.value)) && (itemKey.equals(this.value))) {
					appendAttribute(sb, "selected", "true");
				}
				sb.append('>');
				sb.append(itemValue);
				sb.append("</option>");
			}
		}
		sb.append("</select>");
	}
	public static void getOptionHtml(FastStringBuilder sb, String text, String value, boolean flag) {
		text = LangUtil.get(text);
		sb.append("<a href=\"javascript:void(0);\" onclick=\"Ark.Selector.onItemClick(this);\"")
				.append(" onmouseover=\"Ark.Selector.onItemMouseOver(this)\" ").append(flag ? "selected=\"true\"" : "")
				.append(" hidefocus value=\"").append(value).append("\">").append(text).append("</a>");
	}

	private void getScript4Data(FastStringBuilder sb) {
		if (lazy) {// 延迟加载，前台页面通过JS加载
			return;
		}
		Mapx<String, Object> params = Current.getRequest();
		if (StringUtil.isNotEmpty(conditionField)) {
			params.put("ConditionField", conditionField);
			params.put("ConditionValue", conditionValue);
		} else {
			params.put("ConditionField", "1");
			params.put("ConditionValue", "1");
		}
		DataTable dt = null;
	    if (ObjectUtil.empty(this.data)) {
	      dt = getCodeData(this.code, this.method, this.rest, this.options, this.pageContext, params);
	    } else if ((this.data instanceof Map)) {
	      dt = Mapx.toDataTable((Map)this.data);
	    } else if ((this.data instanceof DataTable)) {
	      dt = (DataTable)this.data;
	    }
		if (dt != null) {
			for (int i = 0; i < dt.getRowCount(); i++) {
				if (StringUtil.isNotEmpty(value)) {// 2013-10-17 如果有设置固定值，则更新selectedIndex
					if (dt.getString(i, 0).equals(value)) {
						selectedIndex = optionCount;
					}
				} else if (selectedIndex != -1) {// 2013-10-17 如果没有设值但有设selectedIndex，则更新value
					if (i == selectedIndex) {
						value = dt.getString(i, 0);
					}
				}
				if (!showValue) {
					sb.append("_data.push(['").append(dt.getString(i, 0)).append("','").append(dt.getString(i, 1)).append("']);");
				} else {
					sb.append("_data.push(['").append(dt.getString(i, 0)).append("','").append(dt.getString(i, 0)).append("-")
							.append(dt.getString(i, 1)).append("']);");
				}
			}
			if (dt.getColumnCount() > 2) {// 需要添加脚本
				sb.append(DataCollection.dataTableToJS(dt));
				sb.append("_DataSource = new DataTable();");
				sb.append("_DataSource.init(_Ark_Cols,_Ark_Values);");
			}
		}
	}

	/**
	 * <ark:checkbox> <ark:radio>也用到了这个方法
	 */
	public static DataTable getCodeData(String code, String method, String rest, String options, AbstractExecuteContext context,
			Mapx<String, Object> params) {
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
				dt.insertRow(v, k);
			}
			return dt;
		}
		if (ObjectUtil.empty(method) && ObjectUtil.empty(rest) && code.startsWith("#")) {
			method = code.substring(1);
		}
		if (ObjectUtil.notEmpty(method)) {
			try {
				IMethodLocator m = MethodLocatorUtil.find(method);
				PrivCheck.check(m);
				Object o = m.execute();
				if (o == null) {
					o = new DataTable();
				}
				dt = (DataTable) o;
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new UIException(method + " must return DataTable");
			}
		} else if (ObjectUtil.notEmpty(rest)) {
			try {
				RequestData requestData = Current.getRequest();
				JsonResult jsonResult = RestUtil.post(rest, requestData, DataTable.class);
				if(!jsonResult.isSuccess()) {
					throw new TemplateRuntimeException(jsonResult.getMessage());
				}
				DataTable dataTable = (DataTable)jsonResult.getData();
				dt = dataTable;
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new UIException(rest + " must return DataTable");
			}
		} else {
			dt = CodeSourceUI.getCodeSourceInstance().getCodeData(code, params);
		}
		LangUtil.decodeDataTable(dt, context.getLanguage()); // 检查国际化字符串
		return dt;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<>();
		list.add(new TagAttr("id", true));
		list.add(new TagAttr("autoWidth", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("defaultBlank", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("disabled", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("input", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("lazy", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("showValue", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("listHeight", DataTypes.INTEGER.code()));
		list.add(new TagAttr("listWidth", DataTypes.INTEGER.code()));
		list.add(new TagAttr("optionCount", DataTypes.INTEGER.code()));
		list.add(new TagAttr("selectedIndex", DataTypes.INTEGER.code()));
		list.add(new TagAttr("className"));
		list.add(new TagAttr("code"));
		list.add(new TagAttr("condition"));
		list.add(new TagAttr("conditionField"));
		list.add(new TagAttr("conditionValue"));
		list.add(new TagAttr("listURL"));
		list.add(new TagAttr("method"));
		list.add(new TagAttr("rest"));
		list.add(new TagAttr("name"));
		list.add(new TagAttr("onChange"));
		list.add(new TagAttr("options"));
		list.add(new TagAttr("style"));
		list.add(new TagAttr("value"));
		list.add(new TagAttr("valueText"));
		list.add(new TagAttr("verify"));
		list.add(new TagAttr("data", 13));
	    list.add(new TagAttr("nativeRender", TagAttr.BOOL_OPTIONS));
		return list;
	}

	@Override
	public void afterCompile(TagCommand tc, TemplateExecutor te) {
		if (!this.nativeRender) {
			String content = rewrite(getTagSource());
			TemplateCompiler c = new TemplateCompiler(te.getManagerContext());
			c.compileSource(content);
			if (!tc.isHasBody()) {
				tc.setHasBody(true);
			}
			tc.setCommands(c.getExecutor().getCommands());
		}
	}

	private boolean isAttributeSet(String k) {
		return attributes.containsKey(k);
	}

	private String getAttributeOriginalValue(String k) {
		return attributes.get(k);
	}

	public String rewrite(String content) {
		HtmlElement select = parseSelect(content);
		if (!isAttributeSet("id")) {
			setAttribute("id", "${ID}");
		}
		if (!isAttributeSet("name")) {
			setAttribute("name", getAttributeOriginalValue("id"));
		}
		Object id = getAttributeOriginalValue("id");
		// 处理value，使下拉框默认有选中值
		FastStringBuilder sb = new FastStringBuilder();
		sb.append("<div id=\"").append(id).append("_outer\" ztype=\"select\"");
		Object className = getAttributeOriginalValue("className");
		if (className != null) {
			sb.append(" class=\"").append(className);
			sb.append(" z-combox\"");
			sb.append(" _class=\"").append(className).append("\"");
		} else {
			sb.append(" class=\"z-combox\"");
		}
		sb.append(" style=\"display:inline-block; *zoom: 1;*display: inline;vertical-align:middle;").append(
				"height:auto;width:auto;position:relative;border:none 0;margin:0;padding:0;white-space: nowrap;");
		if (isAttributeSet("style")) {
			sb.append("\" _style=\"").append(getAttributeOriginalValue("style"));
		}
		sb.append("\">");

		// 以下为隐藏文本框的属性
		sb.append("<input type=\"text\" ztype=\"select\" id=\"").append(id).append("\" name=\"" + getAttributeOriginalValue("name") + "\"")
				.append(" tabindex=\"-1\" autocomplete=\"off\" class=\"inputText\"");
		if (isAttributeSet("style")) {
			sb.append(" style=\"").append(getAttributeOriginalValue("style")).append(";position:absolute;z-index:-1;\"");
		} else {
			sb.append(" style=\"position:absolute;z-index:-1;\"");
		}

		if (isAttributeSet("onChange")) {
			sb.append(" onchange=\"").append(getAttributeOriginalValue("onChange")).append("\"");
		}
		if (isAttributeSet("disabled")) {
			sb.append(" disabled=\"").append(getAttributeOriginalValue("disabled")).append("\"");
		}
		if (isAttributeSet("listWidth")) {
			sb.append(" listwidth=\"").append(getAttributeOriginalValue("listWidth")).append("\"");
		}
		if (isAttributeSet("listHeight")) {
			sb.append(" listHeight=\"").append(getAttributeOriginalValue("listHeight")).append("\"");
		}
		if (isAttributeSet("listURL")) {
			sb.append(" listurl=\"").append(Config.getContextPath() + getAttributeOriginalValue("listURL")).append("\"");
		}
		if (isAttributeSet("verify")) {
			sb.append(" verify=\"").append(getAttributeOriginalValue("verify")).append("\"");
		}
		if (isAttributeSet("condition")) {
			sb.append(" condition=\"").append(getAttributeOriginalValue("condition")).append("\"");
		}
		if (isAttributeSet("input")) {
			sb.append(" input=\"").append(getAttributeOriginalValue("input")).append("\"");
		}
		if (isAttributeSet("lazy")) {
			sb.append(" lazy=\"").append(getAttributeOriginalValue("lazy")).append("\"");
		}
		if (isAttributeSet("autowidth")) {
			sb.append(" autowidth=\"").append(getAttributeOriginalValue("autowidth")).append("\"");
		}
		if (isAttributeSet("showValue")) {
			sb.append(" showvalue=\"").append(getAttributeOriginalValue("showValue")).append("\"");
		}
		if (isAttributeSet("code")) {
			sb.append(" code=\"").append(getAttributeOriginalValue("code")).append("\"");
		}
		if (isAttributeSet("method")) {
			sb.append(" method=\"").append(getAttributeOriginalValue("method")).append("\"");
		}
		if (isAttributeSet("rest")) {
			sb.append(" rest=\"").append(getAttributeOriginalValue("rest")).append("\"");
		}
		if (isAttributeSet("conditionField")) {
			sb.append(" conditionField=\"").append(getAttributeOriginalValue("conditionField")).append("\"");
		}
		if (isAttributeSet("conditionValue")) {
			sb.append(" conditionValue=\"").append(getAttributeOriginalValue("conditionValue")).append("\"");
		}
		if (isAttributeSet("value")) {
			sb.append(" value=\"").append(getAttributeOriginalValue("value")).append("\"");
			sb.append(" startvalue=\"").append(getAttributeOriginalValue("value")).append("\"");
		}
		sb.append("/>");

		// 以下为文本框的属性
		sb.append("<input type=\"text\" id=\"").append(id).append("_textField\" autocomplete=\"off\"");

		sb.append(" class=\"inputText\"");
		if (isAttributeSet("condition")) {
			sb.append(" condition=\"").append(getAttributeOriginalValue("condition")).append("\"");
		}
		if (isAttributeSet("style")) {
			sb.append(" style=\"vertical-align:middle; cursor:default;").append(getAttributeOriginalValue("style")).append("\"");
		} else {
			sb.append(" style=\"vertical-align:middle; cursor:default;\"");
		}
		if (isAttributeSet("valueText")) {
			sb.append(" value=\"" + getAttributeOriginalValue("valueText") + "\"");
		}
		sb.append(" />");
		sb.append("<a id=\"")
				.append(id)
				.append("_spinner\" class=\"z-combox-spinner\" style=\"position:relative; left:-17px; margin-right:-15px; cursor:pointer; width:13px; height:15px;vertical-align:middle;\"><b></b></a>");
		sb.append("<div id=\"").append(id).append("_list\" class=\"optgroup\" style=\"text-align:left;display:none;\">");
		sb.append("<div id=\"").append(id).append("_ul\" style=\"left:-1px; width:-1px;\">");

		sb.append("<script>");
		sb.append("Combox_").append(id).append("_Init=function(){");
		sb.append("var _el=Ark.getDom('").append(id).append("');if(_el._components){return true;}");
		sb.append("var  _data=[];");
		sb.append("var  _DataSource;");

		if (defaultblank) {
			sb.append("_data.push(['','']);");
		}

		if (select != null) {
			int i = 0;
			for (HtmlNode node : select.iterator()) {
				if (node.getType() == HtmlNode.ELEMENT) {
					HtmlElement ele = (HtmlElement) node;
					if (ele.getTagName().equalsIgnoreCase("option") || ele.getTagName().equalsIgnoreCase("span")) {
						String text = ele.getInnerHTML();
						String value = ele.attributeValue("value");
						if (ele.getAttributes().containsKey("selected")) {
							selectedIndex = i;
						}
						if (showValue) {
							sb.append("_data.push(['").append(value).append("','").append(value)
									.append(StringUtil.isNotEmpty(text) ? "-" + text.replaceAll("(\n|\r)", "").trim() : "").append("']);");
						} else {
							sb.append("_data.push(['").append(value).append("','").append(text.replaceAll("(\n|\r)", "").trim())
									.append("']);");
						}
						i++;
					}
				}
			}
		}

		if (isAttributeSet("code") || isAttributeSet("method") || isAttributeSet("rest") || isAttributeSet("options")) {
			sb.append("${(" + Var + ")}");
		}
		sb.append("var combox_").append(id).append(" = new Ark.Selector({el:_el,data:_data,");
		if (isAttributeSet("listURL")) {
			sb.append("url:'").append(getAttributeOriginalValue("listURL")).append("',");
		}
		if (isAttributeSet("value")) {
			sb.append("value:'").append(getAttributeOriginalValue("value").replaceAll("'", "\\\\'")).append("',");
		}
		if (selectedIndex != -1) {
			sb.append("selectedIndex:").append(selectedIndex).append(",");
		}
		sb.append("DataSource:_DataSource");
		sb.append("});");

		sb.append("};");
		sb.append("if(Ark.Page.isReady){").append("Combox_").append(id).append("_Init();}else{Ark.Page.onReady(Combox_").append(id)
				.append("_Init);}");
		sb.append("</script>");

		sb.append("</div></div></div>");
		return sb.toStringAndClose();
	}

	private void checkAttribute(HtmlElement select, String attr) {
		if (!attributes.containsKey(attr) && select.attributeValue(attr) != null) {
			setAttribute(attr, select.attributeValue(attr));
		}
	}

	/**
	 * 解析ark:select内的select标签.<br>
	 * 注意：select标签的属性中不允许有表达式
	 */
	private HtmlElement parseSelect(String content) {
		HtmlElement select = null;
		HtmlParser parser = new HtmlParser(content);
		parser.parse();
		List<HtmlElement> list = parser.getDocument().getTopElementsByTagName("select");
		if (ObjectUtil.notEmpty(list)) {// 旧的用法直接输出options了
			select = list.get(0);
			checkAttribute(select, "id");
			checkAttribute(select, "className");
			checkAttribute(select, "style");
			checkAttribute(select, "code");
			checkAttribute(select, "condition");
			checkAttribute(select, "conditionField");
			checkAttribute(select, "conditionValue");
			checkAttribute(select, "disabled");
			checkAttribute(select, "input");
			checkAttribute(select, "disabled");
			checkAttribute(select, "defaultblank");
			checkAttribute(select, "showvalue");
			checkAttribute(select, "lazy");
			checkAttribute(select, "autowidth");
			checkAttribute(select, "listwidth");
			checkAttribute(select, "listheight");
			checkAttribute(select, "value");
			checkAttribute(select, "verify");
			checkAttribute(select, "onChange");
			checkAttribute(select, "listURL");
			checkAttribute(select, "options");
			checkAttribute(select, "verify");
			checkAttribute(select, "selectedIndex");
		}
		return select;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getListWidth() {
		return listWidth;
	}

	public void setListWidth(int listWidth) {
		this.listWidth = listWidth;
	}

	public String getOnChange() {
		return onChange;
	}

	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	public int getListHeight() {
		return listHeight;
	}

	public void setListHeight(int listHeight) {
		this.listHeight = listHeight;
	}

	public String getListURL() {
		return listURL;
	}

	public void setListURL(String listURL) {
		this.listURL = listURL;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInput() {
		return input;
	}

	public void setInput(boolean input) {
		this.input = input;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getConditionField() {
		return conditionField;
	}

	public void setConditionField(String conditionField) {
		this.conditionField = conditionField;
	}

	public String getConditionValue() {
		return conditionValue;
	}

	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}

	public boolean getShowValue() {
		return showValue;
	}

	public void setShowValue(boolean showValue) {
		this.showValue = showValue;
	}

	public boolean getInput() {
		return input;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public boolean isAutowidth() {
		return autowidth;
	}

	public void setAutowidth(boolean autowidth) {
		this.autowidth = autowidth;
	}

	public boolean isDefaultblank() {
		return defaultblank;
	}

	public void setDefaultblank(boolean defaultblank) {
		this.defaultblank = defaultblank;
	}

	public boolean isDefaultBlank() {
		return defaultblank;
	}

	public void setDefaultBlank(boolean defaultblank) {
		this.defaultblank = defaultblank;
	}

	public Object getData() {
		return this.data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getOnchange() {
		return onChange;
	}

	public void setOnchange(String onchange) {
		onChange = onchange;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getValueText() {
		return valueText;
	}

	public void setValueText(String valueText) {
		this.valueText = valueText;
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.UIControl.SelectTagName}";
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
	public boolean isKeepTagSource() {
		return true;// 本标签要求编译后依然持有源代码
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public boolean isNativeRender() {
		return this.nativeRender;
	}

	public void setNativeRender(boolean nativeRender) {
		this.nativeRender = nativeRender;
	}
	public String getRest() {
		return rest;
	}
	public void setRest(String rest) {
		this.rest = rest;
	}
}
