package org.ark.framework.jaf.zhtml;

import io.arkx.framework.commons.util.FileUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;

/**
 * @class org.ark.framework.jaf.zhtml.ZhtmlTester
 *
 * @author Darkness
 * @date 2013-1-31 下午12:56:10
 * @version V1.0
 */
public class ZhtmlTester {
    private ZhtmlPage page;

    public ZhtmlTester(String fileName) {
        this.page = new ZhtmlPage(fileName);
        HttpServletRequest request = new ZhtmlTesterRequest(this.page);
        HttpServletResponse response = new ZhtmlTesterResponse(this.page);
        PageContext pageContext = new ZhtmlTesterPageContext(this.page);

        this.page.setPageContext(pageContext);
        this.page.setRequest(request);
        this.page.setResponse(response);
    }

    public void run() {
        String source = FileUtil.readText(this.page.getFileName());
        ZhtmlExecutor je = new ZhtmlExecutor(source);
        try {
            je.execute(this.page);
        } catch (ZhtmlRuntimeException e) {
            e.printStackTrace();
        }
    }

    public ZhtmlPage getPage() {
        return this.page;
    }

    public void setPage(ZhtmlPage page) {
        this.page = page;
    }
}
