package org.ark.framework.jaf.controls;

import java.lang.reflect.Method;

import org.ark.framework.jaf.Current;
import org.ark.framework.security.PrivCheck;

import io.arkx.framework.commons.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

/**
 * @class org.ark.framework.jaf.controls.DataListTag
 *
 * @author Darkness
 * @date 2013-1-31 下午12:42:37
 * @version V1.0
 */
public class DataListTag extends BodyTagSupport {
    private static final long serialVersionUID = 1L;
    private String method;
    private String id;
    private int size;
    private boolean page;
    private boolean autoFill = true;

    private boolean autoPageSize = false;
    private String dragHandle;
    private String listNodes;
    private String sortEnd;

    public void setPageContext(PageContext pc) {
        super.setPageContext(pc);
        this.method = null;
        this.id = null;
        this.page = true;
        this.autoFill = true;
        this.autoPageSize = false;
        this.size = 0;
        this.dragHandle = null;
        this.listNodes = null;
        this.sortEnd = null;
    }

    public int doAfterBody() throws JspException {
        BodyContent body = getBodyContent();
        String content = body.getString().trim();
        try {
            if (StringUtil.isEmpty(this.method)) {
                throw new RuntimeException("DataList's method cann't be empty");
            }

            DataListAction dla = new DataListAction();
            dla.setTagBody(content);
            dla.setPage(this.page);
            dla.setAutoFill(this.autoFill);
            dla.setAutoPageSize(this.autoPageSize);
            dla.setMethod(this.method);
            dla.setID(this.id);
            dla.setDragHandle(this.dragHandle);
            dla.setListNodes(this.listNodes);
            dla.setSortEnd(this.sortEnd);

            HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
            dla.setPageSize(this.size);

            if (this.page) {
                dla.setPageIndex(0);
                if (StringUtil.isNotEmpty(dla.getParam("_ARK_PAGEINDEX"))) {
                    dla.setPageIndex(Integer.parseInt(dla.getParam("_ARK_PAGEINDEX")));
                }
                if (dla.getPageIndex() < 0) {
                    dla.setPageIndex(0);
                }
                dla.setPageSize(this.size);
            }

            Method m = Current.prepareMethod(request, response, this.method, new Class[]{DataListAction.class});
            if (!PrivCheck.check(m, request, response)) {
                return 5;
            }

            dla.setParams(Current.getRequest());
            Current.invokeMethod(m, new Object[]{dla});
            this.pageContext.setAttribute(this.id + "_ARK_ACTION", dla);
            getPreviousOut().write(dla.getHtml());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return 6;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isPage() {
        return this.page;
    }

    public void setPage(boolean page) {
        this.page = page;
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

    public String getDragHandle() {
        return this.dragHandle;
    }

    public void setDragHandle(String dragHandle) {
        this.dragHandle = dragHandle;
    }

    public String getListNodes() {
        return this.listNodes;
    }

    public void setListNodes(String listNodes) {
        this.listNodes = listNodes;
    }

    public String getSortEnd() {
        return this.sortEnd;
    }

    public void setSortEnd(String sortEnd) {
        this.sortEnd = sortEnd;
    }
}
