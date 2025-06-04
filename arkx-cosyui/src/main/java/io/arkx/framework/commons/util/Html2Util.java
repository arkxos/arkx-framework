package io.arkx.framework.commons.util;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.ark.framework.jaf.PlaceHolder;
import org.ark.framework.jaf.PlaceHolderContext;
import org.ark.framework.jaf.html.HtmlTD;
import org.ark.framework.jaf.html.HtmlTR;
import org.ark.framework.jaf.html.HtmlTable;
import org.ark.framework.jaf.tag.SelectTag;
import org.ark.framework.orm.Schema;

import io.arkx.framework.Constant;
import io.arkx.framework.cache.CacheManager;
import io.arkx.framework.commons.collection.CaseIgnoreMapx;
import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.i18n.LangUtil;



public class Html2Util {
	
	public static Mapx<String, Object> codeToMapx(String CodeType) {
		Mapx<String, Object> map = CacheManager.getMapx("Code", CodeType);
		Mapx<String, Object> map2 = new Mapx<String, Object>();
		int i = -1;
		for (String key : map.keyArray()) {
			if (map.get(key).toString().startsWith("{")) {
				Schema schema = (Schema) map.get(key);
				if (i == -1) {
					i = schema.getColumn("CodeName").getColumnOrder();
				}
				map2.put(key, LangUtil.get(schema.getV(i) + ""));
			} else {
				map2.put(key, LangUtil.get(map.get(key).toString()));
			}
		}
		return map2;
	}

	public static String codeToOptions(String CodeType) {
		return codeToOptions(CodeType, null);
	}

	public static String codeToOptions(String CodeType, Object checkedValue) {
		return mapxToOptions(codeToMapx(CodeType), checkedValue);
	}

	public static String codeToSelectOptions(String CodeType, boolean addBlankOptionFlag) {
		return mapxToOptions(codeToMapx(CodeType), "option", null, addBlankOptionFlag);
	}

	public static String codeToSelectOptions(String CodeType, Object checkedValue) {
		return mapxToOptions(codeToMapx(CodeType), "option", checkedValue, false);
	}

	public static String codeToOptions(String CodeType, boolean addBlankOptionFlag) {
		return mapxToOptions(codeToMapx(CodeType), "span", null, addBlankOptionFlag);
	}

	public static String codeToOptions(String CodeType, Object checkedValue, boolean addBlankOptionFlag) {
		return mapxToOptions(codeToMapx(CodeType), "span", checkedValue, addBlankOptionFlag);
	}

	public static String codeToRadios(String name, String CodeType) {
		return codeToRadios(name, CodeType, null, false);
	}

	public static String codeToRadios(String name, String CodeType, Object checkedValue) {
		return codeToRadios(name, CodeType, checkedValue, false);
	}

	public static String codeToRadios(String name, String CodeType, boolean direction) {
		return codeToRadios(name, CodeType, null, direction);
	}

	public static String codeToRadios(String name, String CodeType, Object checkedValue, boolean direction) {
		return mapxToRadios(name, codeToMapx(CodeType), checkedValue, direction);
	}

	public static String codeToCheckboxes(String name, String CodeType) {
		return mapxToCheckboxes(name, codeToMapx(CodeType));
	}

	public static String codeToCheckboxes(String name, String CodeType, boolean direction) {
		return codeToCheckboxes(name, CodeType, new String[0], direction);
	}

	public static String codeToCheckboxes(String name, String CodeType, DataTable checkedDT) {
		return codeToCheckboxes(name, CodeType, checkedDT, false);
	}

	public static String codeToCheckboxes(String name, String CodeType, DataTable checkedDT, boolean direction) {
		return mapxToCheckboxes(name, codeToMapx(CodeType), checkedDT, null, direction);
	}

	public static String codeToCheckboxes(String name, String CodeType, String[] checkedArray) {
		return codeToCheckboxes(name, CodeType, checkedArray, false);
	}

	public static String codeToCheckboxes(String name, String CodeType, String[] checkedArray, boolean direction) {
		return mapxToCheckboxes(name, codeToMapx(CodeType), checkedArray, null, direction);
	}

	public static String mapxToCheckboxesTable(String name, Mapx<?, ?> map, Object[] checkedArray, Object[] disabledValue, int colNum) {
		if (colNum < 1) {
			colNum = 1;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<table width='100%'>");
		int i = 0;
		for (Iterator localIterator = map.keySet().iterator(); localIterator.hasNext();) {
			Object key = localIterator.next();
			if (i % colNum == 0) {
				sb.append("<tr>");
			}
			Object value = map.get(key);
			sb.append("<td width='").append(100 / colNum).append("%'><input type='checkbox'");
			sb.append(" name='").append(name);
			sb.append("' id='").append(name).append("_").append(i).append("' value='");
			sb.append(value);
			sb.append("'");
			if (disabledValue != null) {
				for (int j = 0; j < disabledValue.length; j++) {
					if (value.equals(disabledValue[j])) {
						sb.append(" disabled='true'");
						break;
					}
				}
			}

			if (checkedArray != null) {
				for (int j = 0; j < checkedArray.length; j++) {
					if (value.equals(checkedArray[j])) {
						sb.append(" checked='true'");
						break;
					}
				}
			}

			sb.append(" >");
			sb.append("<label for='").append(name).append("_").append(i).append("'>");
			sb.append(map.get(value));
			sb.append("</label>&nbsp;</td>");
			if (i % colNum == colNum - 1) {
				sb.append("</tr>");
			}
			i++;
		}
		if (map.size() % colNum != 0) {
			for (i = 0; i < colNum - map.size() % colNum; i++) {
				sb.append("<td>&nbsp;</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	public static String mapxToCheckboxesTable(String name, Mapx<?, ?> map, DataTable checkedDT, Object[] disabledValue, int colNum) {
		String[] checkedArray = new String[checkedDT.getRowCount()];
		for (int i = 0; i < checkedDT.getRowCount(); i++) {
			checkedArray[i] = checkedDT.getString(i, 0);
		}
		return mapxToCheckboxesTable(name, map, checkedArray, disabledValue, colNum);
	}

	public static String mapxToCheckboxesTable(String name, String CodeType, DataTable checkedDT, Object[] disabledValue, int colNum) {
		return mapxToCheckboxesTable(name, codeToMapx(CodeType), checkedDT, disabledValue, colNum);
	}

	public static String codeToCheckboxesTable(String name, String CodeType, DataTable checkedDT, int colNum) {
		return codeToCheckboxesTable(name, CodeType, checkedDT, null, colNum);
	}

	public static String codeToCheckboxesTable(String name, String CodeType, DataTable checkedDT, Object[] disabledValue, int colNum) {
		return mapxToCheckboxesTable(name, CodeType, checkedDT, disabledValue, colNum);
	}

	public static String mapxToCheckboxesTable(String name, String CodeType, Object[] checkedArray, Object[] disabledValue, int colNum) {
		return mapxToCheckboxesTable(name, codeToMapx(CodeType), checkedArray, disabledValue, colNum);
	}

	public static String mapxToCheckboxesTable(String name, String CodeType, Object[] checkedArray, int colNum) {
		return mapxToCheckboxesTable(name, codeToMapx(CodeType), checkedArray, null, colNum);
	}

	public static String codeToCheckboxesTable(String name, String CodeType, int colNum) {
		return mapxToCheckboxesTable(name, CodeType, null, colNum);
	}

	public static String codeToCheckboxesTable(String name, String CodeType, Object[] checkedArray, int colNum) {
		return mapxToCheckboxesTable(name, CodeType, checkedArray, new String[0], colNum);
	}

	public static String mapxToOptions(Map<?, ?> map) {
		return mapxToOptions(map, null);
	}

	public static String mapxToOptions(Map<?, ?> map, Object checkedValue) {
		return mapxToOptions(map, checkedValue, false);
	}

	public static String mapxToOptions(Map<?, ?> map, boolean addBlankOptionFlag) {
		return mapxToOptions(map, null, addBlankOptionFlag);
	}

	public static String mapxToOptions(Map<?, ?> map, Object checkedValue, boolean addBlankOptionFlag) {
		return mapxToOptions(map, "span", checkedValue, addBlankOptionFlag);
	}

	public static String mapxToOptions(Map<?, ?> map, String type, Object checkedValue, boolean addBlankOptionFlag) {
		StringBuilder sb = new StringBuilder();
		if (addBlankOptionFlag) {
			sb.append("<").append(type).append(" value=''></").append(type).append(">");
		}
		if (map == null) {
			return sb.toString();
		}
		Object[] keys = map.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			sb.append("<").append(type).append(" value=\"");
			Object v = keys[i];
			sb.append(v);
			if ((v != null) && (v.equals(checkedValue)))
				sb.append("\" selected='true' >");
			else {
				sb.append("\">");
			}
			sb.append(map.get(v));
			sb.append("</").append(type).append(">");
		}
		return sb.toString();
	}

	public static String mapxToRadios(String name, Map<?, ?> map) {
		return mapxToRadios(name, map, null, false);
	}

	public static String mapxToRadios(String name, Map<?, ?> map, Object checkedValue) {
		return mapxToRadios(name, map, checkedValue, false);
	}

	public static String mapxToRadios(String name, Map<?, ?> map, Object checkedValue, boolean direction) {
		StringBuilder sb = new StringBuilder();
		Object[] keys = map.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			Object value = keys[i];
			sb.append("<input type='radio' name='").append(name);
			sb.append("' id='").append(name).append("_").append(i).append("' value='");
			sb.append(value);
			if (value.equals(checkedValue))
				sb.append("' checked='true' >");
			else {
				sb.append("' />");
			}
			sb.append("<label for='").append(name).append("_").append(i).append("'>");
			sb.append(map.get(value));
			sb.append("</label>&nbsp;");
			if (direction) {
				sb.append("<br>");
			}
		}
		return sb.toString();
	}

	public static String mapxToCheckboxes(String name, Map<?, ?> map) {
		return mapxToCheckboxes(name, map, null);
	}

	public static String mapxToCheckboxes(String name, Map<?, ?> map, Object[] checkedArray) {
		return mapxToCheckboxes(name, map, checkedArray, null);
	}

	public static String mapxToCheckboxes(String name, Map<?, ?> map, DataTable checkedDT, Object[] disabledValue) {
		return mapxToCheckboxes(name, map, checkedDT, disabledValue, false);
	}

	public static String mapxToCheckboxes(String name, Map<?, ?> map, DataTable checkedDT, Object[] disabledValue, boolean direction) {
		String[] checkedArray = new String[checkedDT.getRowCount()];
		for (int i = 0; i < checkedDT.getRowCount(); i++) {
			checkedArray[i] = checkedDT.getString(i, 0);
		}
		return mapxToCheckboxes(name, map, checkedArray, disabledValue, direction);
	}

	public static String mapxToCheckboxes(String name, Map<?, ?> map, Object[] checkedArray, Object[] disabledValue) {
		return mapxToCheckboxes(name, map, checkedArray, disabledValue, false);
	}

	public static String mapxToCheckboxes(String name, Map<?, ?> map, Object[] checkedArray, Object[] disabledValue, boolean direction) {
		StringBuilder sb = new StringBuilder();
		int k = 0;
		for (Iterator localIterator = map.keySet().iterator(); localIterator.hasNext();) {
			Object value = localIterator.next();
			sb.append("<input type='checkbox'");
			sb.append(" name='").append(name);
			sb.append("' id='").append(name).append("_").append(k).append("' value='");
			sb.append(value);
			sb.append("'");
			if (disabledValue != null) {
				for (int j = 0; j < disabledValue.length; j++) {
					if (value.equals(disabledValue[j])) {
						sb.append(" disabled='true'");
						break;
					}
				}
			}

			if (checkedArray != null) {
				for (int j = 0; j < checkedArray.length; j++) {
					if (value.equals(checkedArray[j])) {
						sb.append(" checked='true'");
						break;
					}
				}
			}

			sb.append(" >");
			sb.append("<label for='").append(name).append("_").append(k).append("'>");
			sb.append(map.get(value));
			sb.append("</label>&nbsp;");
			if (direction) {
				sb.append("<br>");
			}
			k++;
		}
		return sb.toString();
	}

	public static String arrayToOptions(String[] array) {
		return arrayToOptions(array, null);
	}

	public static String arrayToOptions(String[] array, Object checkedValue) {
		return arrayToOptions(array, checkedValue, false);
	}

	public static String arrayToOptions(String[] array, boolean addBlankOptionFlag) {
		return arrayToOptions(array, null, addBlankOptionFlag);
	}

	public static String arrayToOptions(String[] array, Object checkedValue, boolean addBlankOptionFlag) {
		StringBuilder sb = new StringBuilder();
		if (addBlankOptionFlag) {
			sb.append("<span value=''></span>");
		}

		for (int i = 0; i < array.length; i++) {
			sb.append("<span value=\"");
			Object v = array[i];
			String value = (String) v;
			String[] arr = value.split(",");
			String name = value;
			if (arr.length > 1) {
				name = arr[0];
				value = arr[1];
			}
			sb.append(value);
			if ((value != null) && (value.equals((String) checkedValue)))
				sb.append("\" selected='true' >");
			else {
				sb.append("\">");
			}
			sb.append(name);
			sb.append("</span>");
		}
		return sb.toString();
	}

	public static String queryToOptions(Query qb) {
		return dataTableToOptions(qb.executeDataTable(), null);
	}

	public static String queryToOptions(Query qb, Object checkedValue) {
		return dataTableToOptions(qb.executeDataTable(), checkedValue);
	}

	public static String queryToOptions(Query qb, boolean addBlankOptionFlag) {
		return dataTableToOptions(qb.executeDataTable(), addBlankOptionFlag);
	}

	public static String queryToOptions(Query qb, Object checkedValue, boolean addBlankOptionFlag) {
		return dataTableToOptions(qb.executeDataTable(), checkedValue, addBlankOptionFlag);
	}

	public static String dataTableToOptions(DataTable dt) {
		return dataTableToOptions(dt, null);
	}

	public static String dataTableToOptions(DataTable dt, Object checkedValue) {
		return dataTableToOptions(dt, checkedValue, false);
	}

	public static String dataTableToOptions(DataTable dt, boolean addBlankOptionFlag) {
		return dataTableToOptions(dt, null, addBlankOptionFlag);
	}

	public static String dataTableToOptions(DataTable dt, Object checkedValue, boolean addBlankOptionFlag) {
		StringBuilder sb = new StringBuilder();
		if (addBlankOptionFlag) {
			sb.append(SelectTag.getOptionHtml("", "", false));
		}
		if (dt == null) {
			return null;
		}
		for (int i = 0; i < dt.getRowCount(); i++) {
			String value = dt.getString(i, 1);
			if (value.equals(checkedValue))
				sb.append(SelectTag.getOptionHtml(dt.getString(i, 0), value, true));
			else {
				sb.append(SelectTag.getOptionHtml(dt.getString(i, 0), value, false));
			}
		}
		return sb.toString();
	}

	public static String dataTableToRadios(String name, DataTable dt) {
		return dataTableToRadios(name, dt, null, false);
	}

	public static String dataTableToRadios(String name, DataTable dt, String checkedValue) {
		return dataTableToRadios(name, dt, checkedValue, false);
	}

	public static String dataTableToRadios(String name, DataTable dt, boolean direction) {
		return dataTableToRadios(name, dt, null, direction);
	}

	public static String dataTableToRadios(String name, DataTable dt, Object checkedValue, boolean direction) {
		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < dt.getRowCount(); k++) {
			String value = dt.getString(k, 1);
			sb.append("<input type='radio' name='").append(name);
			sb.append("' id='").append(name).append("_").append(k).append("' value='");
			sb.append(value);
			if (value.equals(checkedValue))
				sb.append("' checked >");
			else {
				sb.append("' >");
			}
			sb.append("<label for='").append(name).append("_").append(k).append("'>");
			sb.append(dt.getString(k, 0));
			sb.append("</label>&nbsp;");
			if (direction) {
				sb.append("<br>");
			}
		}
		return sb.toString();
	}

	public static String arrayToRadios(String name, String[] array) {
		return arrayToRadios(name, array, null, false);
	}

	public static String arrayToRadios(String name, String[] array, String checkedValue) {
		return arrayToRadios(name, array, checkedValue, false);
	}

	public static String arrayToRadios(String name, String[] array, boolean direction) {
		return arrayToRadios(name, array, null, direction);
	}

	public static String arrayToRadios(String name, String[] array, Object checkedValue, boolean direction) {
		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < array.length; k++) {
			String value = array[k];
			sb.append("<input type='radio' name='").append(name);
			sb.append("' id='").append(name).append("_").append(k).append("' value='");
			sb.append(value);
			if (value.equals(checkedValue))
				sb.append("' checked >");
			else {
				sb.append("' >");
			}
			sb.append("<label for='").append(name).append("_").append(k).append("'>");
			sb.append(value);
			sb.append("</label>");
			if (direction) {
				sb.append("<br>");
			}
		}
		return sb.toString();
	}

	public static String mapxToRadiosTable(String name, Map<?, ?> map, Object checkedValue, int colNum) {
		if (colNum < 1) {
			colNum = 1;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<table width='100%'>");
		int i = 0;
		for (Iterator localIterator = map.keySet().iterator(); localIterator.hasNext();) {
			Object value = localIterator.next();
			if (i % colNum == 0) {
				sb.append("<tr>");
			}
			sb.append("<td width='").append(100 / colNum).append("%'><input type='radio' name='").append(name);
			sb.append("' id='").append(name).append("_").append(i).append("' value='");
			sb.append(value);
			if (value.equals(checkedValue))
				sb.append("' checked >");
			else {
				sb.append("' >");
			}
			sb.append("<label for='").append(name).append("_").append(i).append("'>");
			sb.append(map.get(value));
			sb.append("</label></td>");
			if (i % colNum == colNum - 1) {
				sb.append("</tr>");
			}
			i++;
		}
		if (map.size() % colNum != 0) {
			for (i = 0; i < colNum - map.size() % colNum; i++) {
				sb.append("<td>&nbsp;</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	public static String codeToRadiosTable(String name, String CodeType, Object checkedValue, int colNum) {
		return mapxToRadiosTable(name, codeToMapx(CodeType), checkedValue, colNum);
	}

	public static String codeToRadiosTable(String name, String CodeType, int colNum) {
		return mapxToRadiosTable(name, codeToMapx(CodeType), null, colNum);
	}

	public static String codeToRadiosTable(String name, String CodeType) {
		return mapxToRadiosTable(name, codeToMapx(CodeType), null, 0);
	}

	public static String dataTableToCheckboxes(String name, DataTable dt) {
		return dataTableToCheckboxes(name, dt, false);
	}

	public static String dataTableToCheckboxes(String name, DataTable dt, boolean direction) {
		return dataTableToCheckboxes(name, dt, new String[0], direction);
	}

	public static String dataTableToCheckboxes(String name, DataTable dt, DataTable checkedDT) {
		return dataTableToCheckboxes(name, dt, checkedDT, false);
	}

	public static String dataTableToCheckboxes(String name, DataTable dt, DataTable checkedDT, boolean direction) {
		String[] checkedArray = new String[checkedDT.getRowCount()];
		for (int i = 0; i < checkedDT.getRowCount(); i++) {
			checkedArray[i] = checkedDT.getString(i, 0);
		}
		return dataTableToCheckboxes(name, dt, checkedArray, direction);
	}

	public static String dataTableToCheckboxes(String name, DataTable dt, String[] checkedArray) {
		return dataTableToCheckboxes(name, dt, checkedArray, false);
	}

	public static String dataTableToCheckboxes(String name, DataTable dt, String[] checkedArray, boolean direction) {
		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < dt.getRowCount(); k++) {
			String value = dt.getString(k, 1);
			sb.append("<input type='checkbox' name='").append(name);
			sb.append("' id='").append(name).append("_").append(k).append("' value='");
			sb.append(value);
			boolean flag = false;
			if (checkedArray != null) {
				for (int j = 0; j < checkedArray.length; j++) {
					if (value.equals(checkedArray[j])) {
						sb.append("' checked >");
						flag = true;
						break;
					}
				}
			}

			if (!flag) {
				sb.append("' >");
			}
			sb.append("<label for='").append(name).append("_").append(k).append("'>");
			sb.append(dt.getString(k, 0));
			sb.append("</label>&nbsp;");
			if (direction) {
				sb.append("<br>");
			}
		}
		return sb.toString();
	}

	public static String arrayToCheckboxes(String name, String[] array) {
		return arrayToCheckboxes(name, array, null, null, false);
	}

	public static String arrayToCheckboxes(String name, String[] array, String[] checkedArray) {
		return arrayToCheckboxes(name, array, checkedArray, null, false);
	}

	public static String arrayToCheckboxes(String name, String[] array, String onclick) {
		return arrayToCheckboxes(name, array, null, onclick, false);
	}

	public static String arrayToCheckboxes(String name, String[] array, String[] checkedArray, String onclick, boolean direction) {
		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < array.length; k++) {
			String value = array[k];
			sb.append("<label><input type='checkbox'");
			if (StringUtil.isNotEmpty(onclick)) {
				sb.append("onclick='").append(onclick).append("'");
			}
			sb.append(" name='").append(name);
			sb.append("' id='").append(name).append("_").append(k).append("'value='");
			sb.append(value);
			boolean flag = false;
			for (int j = 0; (checkedArray != null) && (j < checkedArray.length); j++) {
				if (value.equals(checkedArray[j])) {
					sb.append("' checked >");
					flag = true;
					break;
				}
			}
			if (!flag) {
				sb.append("' >");
			}
			sb.append("<label for='").append(name).append("_").append(k).append("'>");
			sb.append(value);
			sb.append("</label>");
			if (direction) {
				sb.append("<br>");
			}
		}
		return sb.toString();
	}

	public static String replacePlaceHolder(String html, HashMap<?, ?> map, boolean blankFlag) {
		return replacePlaceHolder(html, map, blankFlag, false);
	}

	public static String replacePlaceHolder(String html, HashMap<?, ?> map, boolean blankFlag, boolean spaceFlag) {
		Matcher matcher = Constant.PatternField.matcher(html);
		StringBuilder sb = new StringBuilder();
		int lastEndIndex = 0;
		String blank = "";
		if (spaceFlag) {
			blank = "&nbsp;";
		}
		CaseIgnoreMapx map2 = new CaseIgnoreMapx();
		for (Iterator localIterator = map.keySet().iterator(); localIterator.hasNext();) {
			Object key = localIterator.next();
			map2.put(key, map.get(key));
		}
		while (matcher.find(lastEndIndex)) {
			sb.append(html.substring(lastEndIndex, matcher.start()));
			String key = matcher.group(1).toLowerCase();
			if (map2.containsKey(key)) {
				Object o = map2.get(key);
				if ((o == null) || (o.equals("")))
					sb.append(blank);
				else
					sb.append(o);
			} else if (blankFlag) {
				sb.append("");
			} else {
				sb.append(html.substring(matcher.start(), matcher.end()));
			}
			lastEndIndex = matcher.end();
		}
		sb.append(html.substring(lastEndIndex));
		return sb.toString();
	}

	public static String replacePlaceHolder(String html, PlaceHolderContext context, boolean blankFlag, boolean spaceFlag) {
		Matcher matcher = Constant.PatternField.matcher(html);
		StringBuilder sb = new StringBuilder();
		int lastEndIndex = 0;
		String blank = "";
		if (spaceFlag) {
			blank = "&nbsp;";
		}
		while (matcher.find(lastEndIndex)) {
			sb.append(html.substring(lastEndIndex, matcher.start()));
			PlaceHolder holder = new PlaceHolder("${" + matcher.group(1) + "}");
			Object v = context.eval(holder);
			if (v != null) {
				if ((v == null) || (v.equals(""))) {
					sb.append(blank);
				} else if ((v instanceof String)) {
					String str = (String) v;
					str = LangUtil.get(str);
					sb.append(str);
				} else if ((v instanceof Date)) {
					String str = DateUtil.toDateTimeString((Date) v);
					sb.append(str);
				} else {
					sb.append(v);
				}
			} else if (blankFlag)
				sb.append("");
			else {
				sb.append(html.substring(matcher.start(), matcher.end()));
			}
			lastEndIndex = matcher.end();
		}
		sb.append(html.substring(lastEndIndex));
		return sb.toString();
	}

	public static String replaceWithDataTable(DataTable dt, String html) {
		return replaceWithDataTable(dt, html, false, true);
	}

	public static String replaceWithDataTable(DataTable dt, String html, boolean spaceFlag) {
		return replaceWithDataTable(dt, html, false, spaceFlag);
	}

	public static String replaceWithDataTable(DataTable dt, String html, boolean blankFlag, boolean spaceFlag) {
		if ((html == null) || (dt == null)) {
			return "";
		}
		Matcher matcher = Constant.PatternField.matcher(html);
		StringBuilder sb = new StringBuilder();
		int lastEndIndex = 0;
		String space = "";
		if (spaceFlag) {
			space = "&nbsp;";
		}
		ArrayList arr = new ArrayList();
		ArrayList key = new ArrayList();

		while (matcher.find(lastEndIndex)) {
			arr.add(html.substring(lastEndIndex, matcher.start()));
			String str = matcher.group(1);
			key.add(str);
			lastEndIndex = matcher.end();
		}
		arr.add(html.substring(lastEndIndex));

		for (int i = 0; i < dt.getRowCount(); i++) {
			for (int j = 0; j < arr.size(); j++) {
				sb.append((String) arr.get(j));
				if (j != key.size()) {
					String k = ((String) key.get(j)).toString();
					if (dt.containsColumn(k)) {
						String str = dt.getString(i, k);
						if (StringUtil.isEmpty(str)) {
							sb.append(space);
						} else {
							str = LangUtil.get(str);
							sb.append(str);
						}
					} else if (blankFlag) {
						sb.append("");
					} else {
						sb.append("${").append(k).append("}");
					}
				}
			}
		}
		return sb.toString();
	}

	public static String replaceWithDataRow(DataRow dr, String html) {
		return replaceWithDataRow(dr, html, true);
	}

	public static String replaceWithDataRow(DataRow dr, String html, boolean spaceFlag) {
		return replaceWithDataRow(dr, html, false, spaceFlag);
	}

	public static String replaceWithDataRow(DataRow dr, String html, boolean blankFlag, boolean spaceFlag) {
		if ((html == null) || (dr == null)) {
			return "";
		}
		Matcher matcher = Constant.PatternField.matcher(html);
		StringBuilder sb = new StringBuilder();
		int lastEndIndex = 0;
		String space = "";
		if (spaceFlag) {
			space = "&nbsp;";
		}
		ArrayList arr = new ArrayList();
		ArrayList key = new ArrayList();

		while (matcher.find(lastEndIndex)) {
			arr.add(html.substring(lastEndIndex, matcher.start()));
			String str = matcher.group(1);
			key.add(str);
			lastEndIndex = matcher.end();
		}
		arr.add(html.substring(lastEndIndex));

		for (int j = 0; j < arr.size(); j++) {
			sb.append((String) arr.get(j));
			if (j != key.size()) {
				String k = ((String) key.get(j)).toString();
				if (dr.getDataColumn(k) != null) {
					String str = dr.getString(k);
					if (StringUtil.isEmpty(str)) {
						sb.append(space);
					} else {
						str = LangUtil.get(str);
						sb.append(str);
					}
				} else if (blankFlag) {
					sb.append("");
				} else {
					sb.append("${").append(k).append("}");
				}
			}
		}
		return sb.toString();
	}

	public static void htmlTableToExcel(OutputStream os, HtmlTable table, String[] widths, String[] indexes, String[] rows) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("First");
		try {
			HSSFFont fontBold = wb.createFont();
			fontBold.setFontHeightInPoints((short) 10);
			fontBold.setFontName("宋体");
//			fontBold.setBoldweight((short) 700);

			HSSFFont fontNormal = wb.createFont();
			fontNormal.setFontHeightInPoints((short) 10);
			fontNormal.setFontName("宋体");
//			fontNormal.setBoldweight((short) 400);

			HSSFCellStyle styleBorderBold = wb.createCellStyle();
			styleBorderBold.setBorderBottom(BorderStyle.THIN);
			styleBorderBold.setBorderLeft(BorderStyle.THIN);
			styleBorderBold.setBorderRight(BorderStyle.THIN);
			styleBorderBold.setBorderTop(BorderStyle.THIN);
			styleBorderBold.setVerticalAlignment(VerticalAlignment.CENTER);
			styleBorderBold.setAlignment(HorizontalAlignment.CENTER);
			styleBorderBold.setWrapText(true);

			styleBorderBold.setFont(fontBold);

			HSSFCellStyle styleBorderNormal = wb.createCellStyle();
			styleBorderNormal.setBorderBottom(BorderStyle.THIN);
			styleBorderNormal.setBorderLeft(BorderStyle.THIN);
			styleBorderNormal.setBorderRight(BorderStyle.THIN);
			styleBorderNormal.setBorderTop(BorderStyle.THIN);
			styleBorderBold.setVerticalAlignment(VerticalAlignment.CENTER);
			styleBorderNormal.setFont(fontNormal);

			HSSFCellStyle styleBold = wb.createCellStyle();
			styleBold.setFont(fontBold);

			HSSFCellStyle styleNormal = wb.createCellStyle();
			styleNormal.setFont(fontNormal);

			HSSFRow row = sheet.getRow(0);
			if (row == null) {
				row = sheet.createRow(0);
			}
			for (int i = 0; i < indexes.length; i++) {
				HSSFCell cell = row.getCell(i);
				if (cell == null) {
					cell = row.createCell(i);
				}
				cell.setCellType(CellType.STRING);
				cell.setCellStyle(styleBorderBold);

				String html = table.getTR(0).getTD(Integer.parseInt(indexes[i])).getInnerHTML();
				html = html.replaceAll("<.*?>", "");
				html = StringUtil.htmlDecode(html);
				cell.setCellValue(html.trim());
				row.setHeightInPoints(23.0F);
				if ((widths != null) && (widths.length > i)) {
					double w = Double.parseDouble(widths[i]);
					if (w < 100.0D) {
						w = 100.0D;
					}
					sheet.setColumnWidth(i, new Double(w * 35.0D).intValue());
				}
			}

			for (int i = 0; i < indexes.length; i++) {
				int j = Integer.parseInt(indexes[i]);
				if (rows != null)
					for (int k = 0; k < rows.length; k++) {
						int n = Integer.parseInt(rows[k]);
						String ztype = table.getTR(n).getAttribute("ztype");
						if (k == table.getChildren().size() - 1) {
							if ((StringUtil.isNotEmpty(ztype)) && (ztype.equalsIgnoreCase("pagebar"))) {
								break;
							}
							String html = table.getTR(n).getInnerHTML();
							if ((StringUtil.isEmpty(html)) || (html.indexOf("PageBarIndex") > 0)) {
								break;
							}
						}
						row = sheet.getRow(k + 1);
						if (row == null) {
							row = sheet.createRow(k + 1);
							row.setHeightInPoints(18.0F);
						}
						HSSFCell cell = row.getCell(i);
						if (cell == null) {
							cell = row.createCell(i);
						}
						cell.setCellType(CellType.STRING);
						cell.setCellStyle(styleBorderNormal);

						String html = table.getTR(n).getTD(j).getOuterHtml();
						html = html.replaceAll("<.*?>", "");
						html = StringUtil.htmlDecode(html);
						cell.setCellValue(html.trim());
					}
				else {
					for (int k = 1; k < table.getChildren().size(); k++) {
						String ztype = table.getTR(k).getAttribute("ztype");
						if (k == table.getChildren().size() - 1) {
							if ((StringUtil.isNotEmpty(ztype)) && (ztype.equalsIgnoreCase("pagebar"))) {
								break;
							}
							String html = table.getTR(k).getInnerHTML();
							if ((StringUtil.isEmpty(html)) || (html.indexOf("PageBarIndex") > 0)) {
								break;
							}
						}
						row = sheet.getRow(k);
						if (row == null) {
							row = sheet.createRow(k);
							row.setHeightInPoints(18.0F);
						}
						HSSFCell cell = row.getCell(i);
						if (cell == null) {
							cell = row.createCell(i);
						}
						cell.setCellType(CellType.STRING);
						cell.setCellStyle(styleBorderNormal);

						String html = "";
						if (table.getTR(k).getChildren().size() > j) {
							html = table.getTR(k).getTD(j).getOuterHtml();
							html = html.replaceAll("<.*?>", "");
							html = StringUtil.htmlDecode(html);
						}
						cell.setCellValue(html.trim());
					}
				}
			}
			wb.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HtmlTable dataTableToHtmlTable(DataTable dt, String cellHtml, int columnCount) {
		HtmlTable table = new HtmlTable();
		HtmlTR tr = null;
		for (int i = 0; i < dt.getRowCount(); i++) {
			if (i % columnCount == 0) {
				tr = new HtmlTR();
				table.addTR(tr);
			}
			HtmlTD td = new HtmlTD();
			td.InnerHTML = replaceWithDataRow(dt.getDataRow(i), cellHtml);
			tr.addChild(td);
		}
		for (int i = 0; i < dt.getRowCount() % columnCount; i++) {
			HtmlTD td = new HtmlTD();
			td.InnerHTML = "&nbsp;";
			tr.addChild(td);
		}
		return table;
	}
}