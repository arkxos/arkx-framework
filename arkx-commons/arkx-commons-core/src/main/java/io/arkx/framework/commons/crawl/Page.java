package io.arkx.framework.commons.crawl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

/**
 *
 * @author Darkness
 * @date 2015-1-9 下午2:34:37
 * @version V1.0
 */
public class Page {

    private Map<String, Object> fieldDatas = new HashMap<String, Object>();
    private List<Request> targetRequests = new ArrayList<>();
    Request request;

    public Page() {
    }

    public Page(Document html) {
        this.setHtml(html);
    }

    public Page(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return this.request;
    }

    public void setHtml(Document html) {
        this.html = html;
    }

    private Document html;

    public Document html() {
        return html;
    }

    public void putField(String fieldName, Object fieldValue) {
        fieldDatas.put(fieldName, fieldValue);
    }

    public Object getField(String fieldName) {
        return fieldDatas.get(fieldName);
    }

    public void addTargetRequests(List<Request> links) {
        targetRequests.addAll(links);
    }

    public List<Request> getTargetRequests() {
        return this.targetRequests;
    }

    public void addTargetRequest(Request request) {
        this.targetRequests.add(request);
    }

}
