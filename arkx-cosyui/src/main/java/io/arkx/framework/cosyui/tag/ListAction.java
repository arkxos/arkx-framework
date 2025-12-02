package io.arkx.framework.cosyui.tag;

import org.ark.framework.jaf.IPageEnableAction;

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.config.MaxPageSize;
import io.arkx.framework.cosyui.template.AbstractTag;
import io.arkx.framework.data.jdbc.Query;

/**
 * 列表数据绑定行为类
 *
 */
public class ListAction implements IPageEnableAction {

	DataTable dataSource;

	Mapx<String, Object> params;

	int pageSize;

	int pageIndex;

	boolean page;

	String ID;

	String method;

	String rest;

	ListTag tag;

	int total;

	String queryString;// 用于构建分页链接

	public void bindData(Query qb) {// NO_UCD
		if (total == 0) {
			total = qb.getCount();
		}
		dataSource = qb.executePagedDataTable(pageSize, pageIndex).getData();
	}

	public void bindData(DataTable dt) {
		if (dt == null) {
			return;
		}
		dataSource = dt;
	}

	@Override
	public void setTotal(int total) {
		this.total = total;
	}

	// @Override
	// public void setTotal(QueryBuilder qb) {
	// total = DBUtil.getCount(qb);
	// }

	@Override
	public int getTotal() {
		return total;
	}

	/**
	 * 得到上级循环的当前行
	 */
	public DataRow getParentCurrentDataRow() {
		AbstractTag p = tag.getParent();
		if (p instanceof ListTag) {
			return ((ListTag) p).getCurrentDataRow();
		}
		return null;
	}

	public DataTable getParentData() {
		AbstractTag p = tag.getParent();
		if (p instanceof ListTag) {
			return ((ListTag) p).getData();
		}
		return null;
	}

	public String getParam(String key) {
		return params.getString(key);
	}

	public DataTable getDataSource() {
		return dataSource;
	}

	public Mapx<String, Object> getParams() {
		return params;
	}

	public void setParams(Mapx<String, Object> params) {
		this.params = params;
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}

	@Override
	public void setPageSize(int pageSize) {
		if (pageSize > MaxPageSize.getValue()) {
			pageSize = MaxPageSize.getValue();
		}
		this.pageSize = pageSize;
	}

	@Override
	public int getPageIndex() {
		return pageIndex;
	}

	@Override
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public boolean isPage() {
		return page;
	}

	public void setPage(boolean pageEnable) {
		page = pageEnable;
	}

	@Override
	public void setPageEnabled(boolean pageEnabled) {
		page = pageEnabled;
	}

	@Override
	public boolean isPageEnabled() {
		return page;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setTag(ListTag tag) {
		this.tag = tag;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getRest() {
		return rest;
	}

	public void setRest(String rest) {
		this.rest = rest;
	}

}
