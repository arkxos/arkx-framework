package io.arkx.framework.commons.crawl.scheduler.component;

import com.google.common.collect.Sets;
import io.arkx.framework.commons.crawl.Request;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
/**
 *  
 * @author Darkness
 * @date 2015-1-9 下午10:46:08
 * @version V1.0
 * @since infinity 1.0
 */
public class HashSetDuplicateRemover implements DuplicateRemover {

    private Set<String> urls = Sets.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public boolean isDuplicate(Request request) {
        return !urls.add(getUrl(request));
    }

    protected String getUrl(Request request) {
        return request.getUrl();
    }

    @Override
    public void resetDuplicateCheck() {
        urls.clear();
    }

    @Override
    public int getTotalRequestsCount() {
        return urls.size();
    }
}
