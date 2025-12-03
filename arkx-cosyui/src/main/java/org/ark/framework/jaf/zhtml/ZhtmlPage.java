package org.ark.framework.jaf.zhtml;

import java.io.Writer;

import org.ark.framework.jaf.PlaceHolderContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;

/**
 * @class org.ark.framework.jaf.zhtml.ZhtmlPage
 * @author Darkness
 * @date 2013-1-31 下午12:55:31
 * @version V1.0
 */
public class ZhtmlPage {

    private String fileName;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private PageContext pageContext;

    private ZhtmlTag currentTag;

    public ZhtmlPage(String fileName) {
        this.fileName = fileName;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public PageContext getPageContext() {
        return this.pageContext;
    }

    public void setPageContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }

    public PlaceHolderContext getContext() {
        return PlaceHolderContext.getInstance(this.currentTag == null ? null : this.currentTag.getTagSupport(),
                this.pageContext);
    }

    public Writer getWriter() {
        if (this.currentTag != null) {
            return this.currentTag.getOut();
        }
        return this.pageContext.getOut();
    }

    public ZhtmlTag getCurrentTag() {
        return this.currentTag;
    }

    public void setCurrentTag(ZhtmlTag tag) {
        this.currentTag = tag;
    }

    public String getFileName() {
        return this.fileName;
    }

}
