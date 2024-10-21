package com.rapidark.framework.commons.simplequeue.scheduler.component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.rapidark.framework.commons.simplequeue.ElementWarpper;
import com.rapidark.framework.commons.simplequeue.Task;

/**
 * @author code4crafer@gmail.com
 */
public class HashSetDuplicateRemover implements DuplicateRemover {

    private Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public boolean isDuplicate(ElementWarpper request, Task task) {
        return !urls.add(getUrl(request));
    }

    protected String getUrl(ElementWarpper request) {
        return request.get();
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        urls.clear();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return urls.size();
    }
}
