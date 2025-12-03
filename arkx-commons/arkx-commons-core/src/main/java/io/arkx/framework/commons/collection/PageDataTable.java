package io.arkx.framework.commons.collection;

/**
 * @class org.ark.framework.collection.PageDataTable
 * @author Darkness
 * @date 2012-9-15 下午12:11:41
 * @version V1.0
 */
public class PageDataTable implements IPageData {

    private boolean pageEnable = true;

    private int pageSize;

    private int pageIndex;

    private int total;

    private DataTable dataTable;

    public int getPageSize() {
        return pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getTotal() {
        if (pageEnable)
            return total;
        return dataTable.getRowCount();
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setData(Object data) {
        dataTable = (DataTable) data;
    }

    public DataTable getData() {
        return dataTable;
    }

    public void setPageEnabled(boolean pageEnable) {
        this.pageEnable = pageEnable;
    }

    public boolean isPageEnabled() {
        return this.pageEnable;
    }

}
