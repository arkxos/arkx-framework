package org.ark.framework.jaf.tag;

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.data.jdbc.Query;
import jakarta.servlet.jsp.tagext.Tag;
import org.ark.framework.jaf.IPageEnableAction;
import org.ark.framework.orm.SchemaSet;
import org.ark.framework.orm.sql.DBUtil;


/**
 * @class org.ark.framework.jaf.tag.ListAction
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:52:35 
 * @version V1.0
 */
public class ListAction implements IPageEnableAction {

	private DataTable dataSource;
	private Mapx<String, String> params;
	private int pageSize;
	private int pageIndex;
	private boolean page;
	private String ID;
	private String method;
	private ListTag tag;
	private int total;
	private String queryString;

	public void bindData(Query qb) {
		if (this.total == 0) {
			this.total = DBUtil.getCount(qb);
		}
		this.dataSource = (DataTable)qb.executePagedDataTable(this.pageSize, this.pageIndex).getData();
	}

	public void bindData(DataTable dt) {
		this.dataSource = dt;
	}

	public void bindData(SchemaSet<?> set) {
		bindData(set.toDataTable());
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setTotal(Query qb) {
		this.total = DBUtil.getCount(qb);
	}

	public int getTotal() {
		return this.total;
	}

	public DataRow getParentCurrentDataRow() {
		Tag p = this.tag.getParent();
		while (p != null) {
			if(p instanceof ListTag) {
				return ((ListTag) p).getCurrentDataRow();
			}
			p = p.getParent();
		}
		return null;
	}

	public DataTable getParentData() {
		Tag p = this.tag.getParent();
		if ((p instanceof ListTag)) {
			return ((ListTag) p).getData();
		}
		return null;
	}

	public String getParam(String key) {
		return this.params.getString(key);
	}

	public DataTable getDataSource() {
		return this.dataSource;
	}

	public Mapx<String, String> getParams() {
		return this.params;
	}

	public void setParams(Mapx<String, String> params) {
		this.params = params;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageIndex() {
		return this.pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public boolean isPage() {
		return this.page;
	}

	public void setPage(boolean pageEnable) {
		this.page = pageEnable;
	}

	public String getID() {
		return this.ID;
	}

	public void setID(String iD) {
		this.ID = iD;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setTag(ListTag tag) {
		this.tag = tag;
	}

	public String getQueryString() {
		return this.queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public void setPageEnabled(boolean pageEnable) {
		this.page = pageEnable;
	}

	public boolean isPageEnabled() {
		return this.page;
	}
}