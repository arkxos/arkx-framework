//package org.ark.framework.jaf.controls.datagrid;
//
//import java.util.ArrayList;
//import java.util.regex.Matcher;
//
//import org.ark.framework.Constant;
//import org.ark.framework.jaf.html.HtmlTR;
//import org.ark.framework.security.VerifyRule;
//import util.io.arkx.framework.commons.StringUtil;
//
//import com.arkxos.framework.framework.collection.Mapx;
//
//
///**   
// * @class org.ark.framework.jaf.controls.datagrid.DataGrid
// * @author Darkness
// * @date 2013-1-9 下午04:05:40 
// * @version V1.0   
// */
//public class DataGrid {
//	
//	private DataGridHeader header = new DataGridHeader();
//	
//	private HtmlTR pageBar = null;
//	
//	private boolean isSimplePageBar = false;
//	
//	private HtmlTR editTemplate = null;
//	
//	private HtmlTR template = null;
//	
//	private String templateHtml;
//	
//	private String style1;
//	private String style2;
//	private String class1;
//	private String class2;
//	
//	private boolean treeFlag;
//	private int treeStartLevel;
//	private boolean treeLazy;
//	
//	private boolean sortFlag;
//	private StringBuilder sortString = new StringBuilder();
//	
//	private ArrayList<String> a1 = new ArrayList<String>();
//	private ArrayList<String> a2 = new ArrayList<String>();
//	
//	public HtmlTR getPageBar() {
//		return pageBar;
//	}
//
//	public void setPageBar(HtmlTR pageBar) {
//		this.pageBar = pageBar;
//	}
//	
//	public void setSimplePageBar(HtmlTR pageBar) {
//		this.pageBar = pageBar;
//		isSimplePageBar = true;
//	}
//
//	public boolean isSimplePageBar() {
//		return isSimplePageBar;
//	}
//
//	public void setSimplePageBar(boolean isSimplePageBar) {
//		this.isSimplePageBar = isSimplePageBar;
//	}
//
//	public HtmlTR getEditTemplate() {
//		return editTemplate;
//	}
//
//	public void setEditTemplate(HtmlTR editTemplate) {
//		this.editTemplate = editTemplate;
//		this.editTemplate.setAttribute("style", "display:none");
//	}
//
//	public HtmlTR getTemplate() {
//		return template;
//	}
//
//	public void setTemplate(HtmlTR template) {
//		this.template = template;
//		
//		setStyle1(template.getAttribute("style1"));
//		setStyle2(template.getAttribute("style2"));
//		setClass1(template.getAttribute("class1"));
//		setClass2(template.getAttribute("class2"));
//		
//		template.removeAttribute("style1");
//		template.removeAttribute("style2");
//		template.removeAttribute("class1");
//		template.removeAttribute("class2");
//	}
//
//	public String getStyle1() {
//		return style1;
//	}
//
//	public void setStyle1(String style1) {
//		this.style1 = style1;
//	}
//
//	public String getStyle2() {
//		return style2;
//	}
//
//	public void setStyle2(String style2) {
//		this.style2 = style2;
//	}
//
//	public String getClass1() {
//		return class1;
//	}
//
//	public void setClass1(String class1) {
//		this.class1 = class1;
//	}
//
//	public String getClass2() {
//		return class2;
//	}
//
//	public void setClass2(String class2) {
//		this.class2 = class2;
//	}
//
//	public ArrayList<String> getA1() {
//		return a1;
//	}
//
//	public void setA1(ArrayList<String> a1) {
//		this.a1 = a1;
//	}
//
//	public ArrayList<String> getA2() {
//		return a2;
//	}
//
//	public void setA2(ArrayList<String> a2) {
//		this.a2 = a2;
//	}
//
//	public boolean isTreeFlag() {
//		return treeFlag;
//	}
//
//	public void setTreeFlag(boolean treeFlag) {
//		this.treeFlag = treeFlag;
//	}
//
//	public int getTreeStartLevel() {
//		return treeStartLevel;
//	}
//
//	public void setTreeStartLevel(int treeStartLevel) {
//		this.treeStartLevel = treeStartLevel;
//	}
//
//	public boolean isTreeLazy() {
//		return treeLazy;
//	}
//
//	public void setTreeLazy(boolean treeLazy) {
//		this.treeLazy = treeLazy;
//	}
//
//	public void setTreeLazy(String attribute) {
//		treeLazy = "true".equals(attribute);
//	}
//
//	public boolean isSortFlag() {
//		return sortFlag;
//	}
//
//	public void setSortFlag(boolean sortFlag) {
//		this.sortFlag = sortFlag;
//	}
//
//	public StringBuilder getSortString() {
//		return sortString;
//	}
//
//	public void setSortString(StringBuilder sortString) {
//		this.sortString = sortString;
//	}
//
//	public void initSortString(String str) {
//		
//		boolean emptyFlag = true;
//		Mapx<Object, Object> sortMap = new Mapx<Object, Object>();
//		if (StringUtil.isNotEmpty(str) && VerifyRule.verify(str, VerifyRule.F_String)) {
//			if (Constant.Null.equals(str)) {
//				str = "";
//			}
//			getSortString().append(str);
//			String[] arr = str.split("\\,");
//			for (int i = 0; i < arr.length; i++) {
//				if (arr[i].indexOf(' ') > 0) {
//					String[] arr2 = arr[i].split("\\s");
//					sortMap.put(arr2[0].trim().toLowerCase(), arr2[1].trim());
//				} else {
//					sortMap.put(arr[i].trim().toLowerCase(), "");
//				}
//			}
//			emptyFlag = false;
//		}
//		
//		header.initHeadSort(this, emptyFlag, sortMap);
//	}
//	
//	public void initFields() {
//		
//		templateHtml = getTemplate().getOuterHtml();
//		if (templateHtml == null) {
//			throw new RuntimeException("Template row not found");
//		}
//		
//		Matcher m = Constant.PatternField.matcher(templateHtml);
//		int lastEndIndex = 0;
//
//		while (m.find(lastEndIndex)) {
//			getA1().add(templateHtml.substring(lastEndIndex, m.start()));
//			getA2().add(m.group(1));
//			lastEndIndex = m.end();
//		}
//		getA1().add(templateHtml.substring(lastEndIndex));
//	}
//
//	public String getTemplateHtml() {
//		return templateHtml;
//	}
//
//	public void setTemplateHtml(String templateHtml) {
//		this.templateHtml = templateHtml;
//	}
//
//	public DataGridHeader getHeader() {
//		return header;
//	}
//
//	public void setHeader(DataGridHeader header) {
//		this.header = header;
//	}
//
//	public void setHead(HtmlTR tr) {
//		header.addHead(tr);
//	}
//}
