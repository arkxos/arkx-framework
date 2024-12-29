package com.arkxos.framework.commons.simplequeue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Object storing extracted result and urls to fetch.<br>
 * Not thread safe.<br>
 * Main method：                                               <br>
 * {@link #getUrl()} get url of current page                   <br>
 * {@link #getHtml()}  get content of current page                 <br>
 * {@link #putField(String, Object)}  save extracted result            <br>
 * {@link #getResultItems()} get extract results to be used in {@link com.arkxos.framework.commons.simplequeue.pipeline.Pipeline}<br>
 * {@link #addTargetRequests(java.util.List)} {@link #addTargetRequest(String)} add urls to fetch                 <br>
 *
 * @author code4crafter@gmail.com <br>
 * @see com.arkxos.framework.commons.simplequeue.ElementProcessor
 * @see com.queue.processor.PageProcessor
 * @since 0.1.0
 */
public class ExecuteResult {

    private ElementWarpper request;

    private ResultItems resultItems = new ResultItems();

    private boolean downloadSuccess = true;

    private List<ElementWarpper> targetRequests = new ArrayList<>();
    
    public ExecuteResult() {
    }

    public static ExecuteResult fail(){
        ExecuteResult page = new ExecuteResult();
        page.setExecuteSuccess(false);
        return page;
    }
    
    public static ExecuteResult success(){
        ExecuteResult page = new ExecuteResult();
        page.setExecuteSuccess(true);
        return page;
    }

    public ExecuteResult setSkip(boolean skip) {
        resultItems.setSkip(skip);
        return this;

    }

    /**
     * store extract results
     *
     * @param key key
     * @param field field
     */
    public void putField(String key, Object field) {
        resultItems.put(key, field);
    }

    public List<ElementWarpper> getTargetRequests() {
        return targetRequests;
    }

    /**
     * add urls to fetch
     *
     * @param requests requests
     */
    public void addTargetRequests(List<String> requests) {
        for (String s : requests) {
            if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
                continue;
            }
            targetRequests.add(new ElementWarpper(s));
        }
    }

    /**
     * add urls to fetch
     *
     * @param requests requests
     * @param priority priority
     */
    public void addTargetRequests(List<String> requests, long priority) {
        for (String s : requests) {
            if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
                continue;
            }
            targetRequests.add(new ElementWarpper(s).setPriority(priority));
        }
    }

    /**
     * add url to fetch
     *
     * @param requestString requestString
     */
    public void addTargetRequest(String requestString) {
        if (StringUtils.isBlank(requestString) || requestString.equals("#")) {
            return;
        }
        targetRequests.add(new ElementWarpper(requestString));
    }

    /**
     * add requests to fetch
     *
     * @param request request
     */
    public void addTargetRequest(ElementWarpper request) {
        targetRequests.add(request);
    }

    /**
     * get request of current page
     *
     * @return request
     */
    public ElementWarpper getRequest() {
        return request;
    }

    public void setRequest(ElementWarpper request) {
        this.request = request;
        this.resultItems.setRequest(request);
    }

    public ResultItems getResultItems() {
        return resultItems;
    }

    public boolean isExecuteSuccess() {
        return downloadSuccess;
    }

    public void setExecuteSuccess(boolean downloadSuccess) {
        this.downloadSuccess = downloadSuccess;
    }

    @Override
    public String toString() {
        return "Page{" +
                ", resultItems=" + resultItems +
                ", success=" + downloadSuccess +
                ", targetRequests=" + targetRequests +
                '}';
    }
}
