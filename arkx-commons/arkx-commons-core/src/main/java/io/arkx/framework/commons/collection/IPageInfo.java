package io.arkx.framework.commons.collection;
/**
 * @class org.ark.framework.collection.IPageInfo
 * @author Darkness
 * @date 2012-9-15 上午11:40:12
 * @version V1.0
 */
public interface IPageInfo {

    void setPageEnabled(boolean pageEnabled);

    boolean isPageEnabled();

    int getPageSize();

    int getPageIndex();

    int getTotal();

    void setPageSize(int paramInt);

    void setPageIndex(int paramInt);

    void setTotal(int paramInt);
}
