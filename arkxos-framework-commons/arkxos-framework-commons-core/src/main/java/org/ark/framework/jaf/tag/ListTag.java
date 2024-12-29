package org.ark.framework.jaf.tag;

import java.io.IOException;
import java.lang.reflect.Method;

import org.ark.framework.jaf.Current;
import org.ark.framework.security.PrivCheck;

import com.arkxos.framework.Constant;
import com.arkxos.framework.commons.collection.DataColumn;
import com.arkxos.framework.commons.collection.DataRow;
import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.collection.DataTypes;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.Html2Util;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.ServletUtil;
import com.arkxos.framework.commons.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTag;
import jakarta.servlet.jsp.tagext.BodyTagSupport;


/**
 * @class org.ark.framework.jaf.tag.ListTag
 * <h2>列表标签</h2>
 * <br/><b>基本用法：</b>
 * <br/>&lt;ark:list id="list2" method="BlockPriv.bindPrivItemList">
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;td style="padding-left:8px;">
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;input type="checkbox" value="${Item}" onclick="onColumnSelect(this)" />
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${Name}
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/td>
 * <br/>&lt;/ark:list>
 * <br/>
 * <br/>public void bindPrivItemList(ListAction la) {
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;DataTable dt = new DataTable();
   <br/>&nbsp;&nbsp;&nbsp;&nbsp;dt.insertColumn( "Item");
   <br/>&nbsp;&nbsp;&nbsp;&nbsp;dt.insertColumn( "Name");
   <br/>&nbsp;&nbsp;&nbsp;&nbsp;dt.insertColumn( "Checked");
   <br/>&nbsp;&nbsp;&nbsp;&nbsp;dt.insertColumn( "Disabled");
   <br/>      
   <br/>&nbsp;&nbsp;&nbsp;&nbsp;dt.insertRow( new Object[] { item.getID(), LangUtil.get(item.getName()), checked, disabled });
   <br/>
   <br/>&nbsp;&nbsp;&nbsp;&nbsp;la.bindData(dt);
<br/>}
<br/>
<br/><b>循环体内嵌入其他标签的使用方式：</b>
<br/>&lt;ark:list method="ContractCalculation.findParticipants">
<br/>&nbsp;&nbsp;&nbsp;&nbsp;	&lt;tr>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		&lt;td align="right">${name}：</td>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		&lt;td>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;			&lt;ark:select id="participantValue_${_RowNo}" method="ContractCalculation.findParticipantValues"
<br/>&nbsp;&nbsp;&nbsp;&nbsp;				defaultblank="true" value="${id}" style="width: 200px" listWidth="200" />
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		&lt;/td>
<br/>	&lt;/tr>
<br/>	&lt;/ark:list>
<br/>	
<br/>	public void findParticipants(ListAction listAction) {
<br/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		// 契约模块id
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		String contractModuleId = listAction.getParam("contractModuleId");
<br/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		// 参与者
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		List<Participant> participants = contractModuleService.getParticipants(contractModuleId);
<br/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		DataTable dataTable = new DataTable("id", "name");
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		for (Participant participant : participants) {
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;			dataTable.insertRow(new Object[] { participant.getId(), participant.getName() });
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		}
<br/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		listAction.bindData(dataTable);
<br/>	}
<br/>
<br/>public DataTable findParticipantValues() {
<br/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		// 当前循环体参数
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		String participantId = Request.getString("currentRow.id");
<br/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		DataTable result = new DataTable("id", "name");
<br/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		Criteria criteria = new Criteria(ParticipantValue.class);
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		criteria.add(Restrictions.eq(ParticipantValue.ParticipantId, participantId));
<br/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		List<ParticipantValue> participantValues = criteria.findEntities();
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		if (participantValues != null) {
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;			for (ParticipantValue participantValue : participantValues) {
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;				result.insertRow(new Object[] { participantValue.getId(), participantValue.getValue() });
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;			}
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		}
<br/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		return result;
<br/>	}
<br/>
<br/><b>List一行多列示例：</b>
<br/><arkk:list id="list2" method="UseOffReport.bindDateList">
<br/>&nbsp;&nbsp;&nbsp;&nbsp;	&lt;ark:if condition="${needStartTr}">&lt;tr>&lt;/ark:if>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;		&lt;td valign="top" class="text" align="center">
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;	  	 	&lt;div id="chartdiv_month${date}" align="center">FusionCharts. &lt;/div>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;		&lt;/td>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;	&lt;ark:if condition="${needEndTr}">&lt;/tr>&lt;/ark:if>
<br/></ark:list>
<br/>public void bindDateList(ListAction la) {
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		String[] months = new String[] { "09-01", "09-02", "09-03", "09-04", "09-05", "09-06", "09-07", "09-08", "09-09" };
<br/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		DataTable dt = new DataTable();
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		dt.insertColumn("date");
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		dt.insertColumn("needStartTr");
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		dt.insertColumn("needEndTr");
<br/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		int i = 0;
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		for (String month : months) {
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;			boolean needStartTr = (i)%3==0;
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;			boolean needEndTr = (i+1)%3==0;
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;			i++;
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;			dt.insertRow(new Object[] { month , needStartTr, needEndTr});
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		}
<br/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;		la.bindData(dt);
<br/>	}
 * @author Darkness
 * @date 2013-1-31 下午12:52:49
 * @version V1.0
 */
public class ListTag extends BodyTagSupport implements IListTag {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 后台方法
	 * @property method
	 * @type {String}
	 */
	private String method;
	
	/**
	 * 是否分页
	 * @property page
	 * @type {boolean}
	 */
	private boolean page;
	
	/**
	 * 每页显示多少条记录
	 * @property size
	 * @type {int}
	 */
	private int size;
	private int rowIndex;
	public DataRow currentRow;
	
	/**
	 * 唯一id标识
	 * @property id
	 * @type {String}
	 */
	private String id;
	private DataTable dataTable;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.method = null;
		this.id = null;
		this.page = true;
		this.size = 0;
		this.rowIndex = 0;
	}

	public int doStartTag() throws JspException {
		try {
			HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
			HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
			if (ObjectUtil.empty(this.method)) {
				return 0;
			}
			Method m = Current.prepareMethod(request, response, this.method, new Class[] { ListAction.class });
			if (!PrivCheck.check(m, request, response)) {
				return 5;
			}

			Mapx<String, String> params = ServletUtil.getParameterMap(request);
			ListAction la = new ListAction();
			la.setParams(params);
			la.setPage(this.page);
			la.setMethod(this.method);
			la.setID(this.id);
			la.setPageSize(this.size);
			la.setTag(this);
			la.setQueryString(request.getQueryString());
			if (this.page) {
				la.setPageIndex(0);
				if (StringUtil.isNotEmpty(la.getParam("PageIndex"))) {
					la.setPageIndex(Integer.parseInt(la.getParam("PageIndex")) - 1);
				}
				if (la.getPageIndex() < 0) {
					la.setPageIndex(0);
				}
			}
			Current.invokeMethod(m, new Object[] { la });
			this.dataTable = la.getDataSource();
			this.pageContext.setAttribute(this.id + Constant.ActionInPageContext, la);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((this.dataTable != null) && (this.dataTable.getRowCount() > 0)) {
			if (this.dataTable.getDataColumn("_RowNo") == null) {
				this.dataTable.insertColumn(new DataColumn("_RowNo", DataTypes.INTEGER));
			}
			for (int i = 0; i < this.dataTable.getRowCount(); i++) {
				this.dataTable.set(i, "_RowNo", Integer.valueOf(i + 1));
			}

			this.currentRow = this.dataTable.getDataRow(this.rowIndex++);

			pageContext.getRequest().setAttribute("currentRow", currentRow);

			return 2;
		}
		return 0;
	}

	public int doAfterBody() throws JspException {
		BodyContent body = getBodyContent();
		String content = body.getString().trim();
		try {
			getPreviousOut().write(Html2Util.replaceWithDataRow(this.currentRow, content, false, false));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (this.dataTable.getRowCount() > this.rowIndex) {
			this.currentRow = this.dataTable.getDataRow(this.rowIndex++);

			pageContext.getRequest().setAttribute("currentRow", currentRow);

			body.clearBody();
			return BodyTag.EVAL_BODY_BUFFERED;
		}
		return 0;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public boolean isPage() {
		return this.page;
	}

	public void setPage(boolean page) {
		this.page = page;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DataRow getCurrentDataRow() {
		return this.currentRow;
	}

	public DataTable getData() {
		return this.dataTable;
	}
}