package org.ark.framework.jaf.tag;

import java.lang.reflect.Method;

import org.ark.framework.jaf.Current;
import org.ark.framework.jaf.controls.TreeAction;
import org.ark.framework.jaf.html.HtmlP;
import org.ark.framework.security.PrivCheck;

import io.arkx.framework.WebCurrent;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

/**
 * @class org.ark.framework.jaf.tag.TreeTag
 * <h2>Tree标签</h2> <br/>
 * <img src="images/TreeTag_1.png"/> <br/>
 * &lt;ark:tree id="tree1" method="Database.treeDataBind"> <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;p cid='${tableName}' cname='${tablename}'
 * onClick="onTreeClick(this);"
 * oncontextmenu="showMenu(event,this);">&nbsp;${alias}&lt;/p> <br/>
 * &lt;/ark:tree> <br/>
 * var newQueryString = null; <br/>
 * <br/>
 * function onTreeClick(ele){ <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp; var cid = ele.getAttribute("cid"); <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp; newQueryString = "tablename="+cid; <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp; check(); <br/>
 * } <br/>
 * <br/>
 * function check(){ <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp; TabPage.tryReload(newQueryString); <br/>
 * } <br/>
 * public void treeDataBind(TreeAction ta) { <br/>
 * DataTable dt = new QueryBuilder("SELECT
 * t.tablename,CONCAT(CONCAT(CONCAT(t.alias,'('),t.tablename),')') AS alias,t.categoryid
 * AS parentTable FROM PT_SYS_XTable t ORDER BY t.tablename").executeDataTable(); <br/>
 * ta.setRootText("数据库列表"); <br/>
 * ta.setIdentifierColumnName("tableName"); <br/>
 * ta.setParentIdentifierColumnName("parentTable"); <br/>
 * ta.setBranchIcon("Icons/icon025a1.gif"); <br/>
 * ta.setLeafIcon("Icons/icon025a1.gif"); <br/>
 * <br/>
 * XConnectionConfig dcc = XConnectionPoolManager.getDBConnConfig(); <br/>
 * dt.insertRow(new Object[] { "数据库类型", dcc.getDatabaseType().getName(), "-1" }); <br/>
 * dt.insertRow(new Object[] { "数据库名称", "XPlatform", "数据库类型" }); <br/>
 * <br/>
 * // 所有表挂在“全部物理表”节点下 <br/>
 * dt.insertRow(new Object[] { "AllTables", "全部物理表", "数据库名称" }); <br/>
 * dt.insertRow(new Object[] { "AllNoDefinedTables", "未定义表", "数据库名称" }); <br/>
 * <br/>
 * dt.union(new QueryBuilder( <br/>
 * "select cv.refid as tableName,cv.chinaname as alias,'数据库名称' as parentTable from
 * pt_sys_categoryValue cv join pt_sys_category c on cv.categoryid=c.id and c.constname =
 * 'TABLE_CATEGORYID'") <br/>
 * .executeDataTable()); <br/>
 * ta.bindData(dt); <br/>
 * <br/>
 * for (int i = 1; i < ta.getItemSize(); i++) { <br/>
 * ta.getItem(i).setIcon("Icons/icon024a1.png"); <br/>
 * } <br/>
 * } <br/>
 * <br/>
 * Tree.loadData('tree1',function(){}); <br/>
 * <br/>
 * 选中某节点：Page.onLoad(function(){ <br/>
 * <br/>
 * Tree.select("tree1", "xname", "pt_sys_config", true);// true 表示执行onclick事件 <br/>
 * }); <br/>
 * <br/>
 * <b>自定义节点</b> <br/>
 * <img src="images/TreeTag_2.png"/> <br/>
 * <img src="images/TreeTag_3.png"/>
 * @author Darkness
 * @date 2013-1-31 下午12:44:58
 * @version V1.0
 */
public class TreeTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 *
	 * @property id
	 * @type {String}
	 */
	private String id;

	private String method;

	private String style;

	private boolean lazy;

	private boolean customscrollbar;

	private boolean expand;

	private int level;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.method = null;
		this.id = null;
		this.style = null;
		this.lazy = false;
		this.customscrollbar = false;
		this.expand = false;
	}

	public int doAfterBody() throws JspException {
		BodyContent body = getBodyContent();
		String content = body.getString().trim();
		try {
			if ((this.method == null) || (this.method.equals(""))) {
				throw new RuntimeException("Tree's method can't be empty");
			}

			TreeAction ta = new TreeAction();
			ta.setTagBody(content);
			ta.setMethod(this.method);

			HtmlP p = new HtmlP();
			p.parseHtml(content);
			ta.setTemplate(p);

			ta.setID(this.id);
			ta.setLazy(this.lazy);
			ta.setCustomscrollbar(this.customscrollbar);
			ta.setExpand(this.expand);
			if (this.level <= 0) {
				this.level = 999;
			}
			ta.setLevel(this.level);
			ta.setStyle(this.style);

			HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
			HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();

			Method m = Current.prepareMethod(request, response, this.method, new Class[] { TreeAction.class });
			if (!PrivCheck.check(m, request, response)) {
				return 5;
			}

			ta.setParams(WebCurrent.getRequest());
			Current.invokeMethod(m, new Object[] { ta });

			getPreviousOut().write(ta.getHtml());
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}
		return 6;
	}

	public boolean isLazy() {
		return this.lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public boolean isCustomscrollbar() {
		return this.customscrollbar;
	}

	public void setCustomscrollbar(boolean customscrollbar) {
		this.customscrollbar = customscrollbar;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
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

	public String getStyle() {
		return this.style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public boolean isExpand() {
		return this.expand;
	}

	public void setExpand(boolean expand) {
		this.expand = expand;
	}

}
