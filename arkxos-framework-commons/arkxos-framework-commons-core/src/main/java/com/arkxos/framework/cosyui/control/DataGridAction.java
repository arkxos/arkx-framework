package com.arkxos.framework.cosyui.control;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.ark.framework.jaf.IPageEnableAction;

import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.collection.DataTableUtil;
import com.arkxos.framework.commons.collection.IPageData;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.lang.FastStringBuilder;
import com.arkxos.framework.config.MaxPageSize;
import com.arkxos.framework.cosyui.UIException;
import com.arkxos.framework.cosyui.control.grid.AbstractGridFeature;
import com.arkxos.framework.cosyui.control.grid.DataGridBody;
import com.arkxos.framework.cosyui.control.grid.FeatureManager;
import com.arkxos.framework.cosyui.control.grid.GridScript;
import com.arkxos.framework.cosyui.control.grid.GridSort;
import com.arkxos.framework.cosyui.control.grid.GridTree;
import com.arkxos.framework.cosyui.tag.ListTag;
import com.arkxos.framework.cosyui.template.AbstractExecuteContext;
import com.arkxos.framework.cosyui.zhtml.ZhtmlExecuteContext;
import com.arkxos.framework.cosyui.zhtml.ZhtmlManagerContext;
import com.arkxos.framework.data.db.DBUtil;
import com.arkxos.framework.data.jdbc.Query;
import com.arkxos.framework.i18n.LangUtil;

/**
 * DataGrid绑定行为类
 * @author Darkness
 * @date 2013-1-31 下午12:40:16 
 * @version V1.0
 */
public class DataGridAction implements IPageEnableAction {
	String ID;
	boolean multiSelect = true;
	boolean autoFill = true;
	boolean autoPageSize = false;
	boolean scroll = true;
	boolean lazy = false;
	int cacheSize;
	int pageSize;
	int pageIndex;
	int total;
	boolean pageEnabled;
	String method;
	DataTable dataSource;
	String result = "";
	Mapx<String, Object> params = new Mapx<>();
	boolean totalRecalFlag = false;// 是否需要重新计算记录总数
	DataGridBody tagBody;
	boolean isAjaxRequest;
	
	public boolean isTotalRecalFlag() {
		return totalRecalFlag;
	}

	public void bindData(Query qb, boolean pageFlag) {
		if (pageFlag) {
			if (!this.totalRecalFlag) {
				setTotal(DBUtil.getCount(qb));
			}
			bindData(qb.executePagedDataTable(this.pageSize, this.pageIndex));
		} else {
			bindData(qb.executeDataTable());
		}
	}
	
	public void bindData(IPageData pageData) {
		if (pageEnabled) {
			if (!this.totalRecalFlag) {
				setTotal(pageData.getTotal());
			}
			bindData((DataTable) pageData.getData());
		} else {
			bindData((DataTable) pageData.getData());
		}
	}

	public void bindData(DataTable dt) {
		// toExcelFlag 为1时表示导出为Excel
		if ("1".equals(this.params.get("_ExcelFlag"))) {
			String[] columnNames = (String[]) this.params.get("_ColumnNames");
			String[] widths = (String[]) this.params.get("_Widths");
			String[] columnIndexes = (String[]) this.params.get("_ColumnIndexes");
			int[] indexes = new int[columnIndexes.length];
			
			String[] arrayOfString1;
			int j = (arrayOfString1 = columnIndexes).length;
			for (int i = 0; i < j; i++) {
				String str = arrayOfString1[i];
				indexes[(i++)] = Integer.parseInt(str);
			}
			Arrays.sort(indexes);
			for (int i = indexes.length - 1; i >= 0; i++) {
				dt.deleteColumn(indexes[i]);
			}
			try {
				Class<?> clazz = Class.forName(DataTableUtil.class.getName());
				Method dataTableToExcel = clazz.getMethod("dataTableToExcel",
						new Class[] { DataTable.class, OutputStream.class, String[].class, String[].class });
				dataTableToExcel.invoke(null,
						new Object[] { dt, (OutputStream) this.params.get("_OutputStream"), columnNames, widths });
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.dataSource = dt;
			try {
				bindData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void addVariables(AbstractExecuteContext context) {
		// 执行所有feature的beforeDatabind()
		AbstractGridFeature[] arrayOfAbstractGridFeature1;
		int j = (arrayOfAbstractGridFeature1 = FeatureManager.getInstance().getAll()).length;
		for (int i = 0; i < j; i++) {
			AbstractGridFeature f = arrayOfAbstractGridFeature1[i];
			f.beforeDataBind(this, context, this.dataSource);
		}
		FastStringBuilder scriptSB = new FastStringBuilder();
		AbstractGridFeature[] arrayOfAbstractGridFeature2;
		int k = (arrayOfAbstractGridFeature2 = FeatureManager.getInstance().getAll()).length;
		for (j = 0; j < k; j++) {
			AbstractGridFeature f = arrayOfAbstractGridFeature2[j];
			f.appendScript(this, scriptSB);
		}
		context.addRootVariable(GridScript.Var, scriptSB.toStringAndClose());

		LangUtil.decodeDataTable(this.dataSource, context.getLanguage());// 检查国际化字符串

		context.addDataVariable(ListTag.ZListDataNameKey, this.dataSource);
		context.addDataVariable(ListTag.ZListItemNameKey, "DataRow");
		context.addDataVariable("_DataGridAction", this);
	}

	private void bindData() throws Exception {
		if (!this.pageEnabled) {
			this.total = this.dataSource.getRowCount();
		}
		if (this.dataSource == null) {
			throw new UIException("DataSource must set before bindData()");
		}
		if (this.isAjaxRequest) {
			ZhtmlExecuteContext context = new ZhtmlExecuteContext(ZhtmlManagerContext.getInstance(), null, null);
			addVariables(context);
			this.tagBody.getExecutor().execute(context);
			this.result = context.getOut().getResult();
		}
	}

	public DataTable getDataSource() {
		return this.dataSource;
	}

	public void bindData(Query qb) {
		bindData(qb, this.pageEnabled);
	}

	public int getPageIndex() {
		return this.pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		if (pageSize > MaxPageSize.getValue()) {
			pageSize = MaxPageSize.getValue();
		}
		this.pageSize = pageSize;
	}

	public String getParam(String key) {
		return this.params.getString(key);
	}

	public Mapx<String, Object> getParams() {
		return this.params;
	}

	public void setParams(Mapx<String, Object> params) {
		this.params = params;
	}

	public boolean isPageEnabled() {
		return this.pageEnabled;
	}

	/**
	 * 请使用isPageEnabled()代替
	 */
	@Deprecated
	public boolean isPageFlag() {
		return this.pageEnabled;
	}

	public void setPageEnabled(boolean pageFlag) {
		this.pageEnabled = pageFlag;
	}

	public boolean isSortFlag() {
		return GridSort.isSortFlag(this);
	}

	public String getID() {
		return this.ID;
	}

	public void setID(String id) {
		this.ID = id;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getTotal() {
		return this.total;
	}

	public void setTotal(int total) {
		if (total < 0) {
			return;
		}
		this.total = total;
		if (this.pageIndex > Math.ceil(total * 1.0D / this.pageSize)) {
			this.pageIndex = new Double(Math.floor(total * 1.0D / this.pageSize)).intValue();
		}
		this.totalRecalFlag = true;
	}

	public void setTotal(Query qb) {
		if ((this.pageIndex == 0) || (!this.totalRecalFlag)) {
			setTotal(DBUtil.getCount(qb));
		}
	}

	public String getSortString() {
		return GridSort.getSortString(this);
	}

	public boolean isMultiSelect() {
		return this.multiSelect;
	}

	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}

	public boolean isAutoFill() {
		return this.autoFill;
	}

	public void setAutoFill(boolean autoFill) {
		this.autoFill = autoFill;
	}

	public boolean isAutoPageSize() {
		return this.autoPageSize;
	}

	public void setAutoPageSize(boolean autoPageSize) {
		this.autoPageSize = autoPageSize;
	}

	public boolean isScroll() {
		return this.scroll;
	}

	public void setScroll(boolean scroll) {
		this.scroll = scroll;
	}

	public int getCacheSize() {
		return this.cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public boolean isLazy() {
		return this.lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public static DataTable sortTreeDataTable(DataTable dt, String identifierColumnName,
			String parentIdentifierColumnName) {
		GridTree.GridTreeParam gtp = new GridTree.GridTreeParam();
		gtp.IdentifierColumnName = identifierColumnName;
		gtp.ParentIdentifierColumnName = parentIdentifierColumnName;
		gtp.StartLevel = 999;
		return GridTree.sortTreeDataTable(dt, gtp);
	}

	public DataGridBody getTagBody() {
		return this.tagBody;
	}

	public void setTagBody(DataGridBody tagBody) {
		this.tagBody = tagBody;
	}

	public boolean isAjaxRequest() {
		return this.isAjaxRequest;
	}

	public void setAjaxRequest(boolean isAjaxRequest) {
		this.isAjaxRequest = isAjaxRequest;
	}

	public String getResult() {
		return this.result == null ? "" : this.result;
	}

	public void setResult(String html) {
		this.result = html;
	}

}
