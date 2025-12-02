package io.arkx.framework.commons.crawl;

import java.util.HashMap;
import java.util.Map;

public class Request {

	public static final String CYCLE_TRIED_TIMES = "cycle_tried_times";

	private Map<String, Object> extraMap = new HashMap<String, Object>();

	private String url;

	int retryCount = 0;

	public Request(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void putExtra(String key, Object value) {
		this.extraMap.put(key, value);
	}

	public Object getExtra(String key) {
		return this.extraMap.get(key);
	}

	public void addRetryCount() {
		retryCount++;
		putExtra(CYCLE_TRIED_TIMES, retryCount);
	}

	public int getRetryCount() {
		return this.retryCount;
	}

}
