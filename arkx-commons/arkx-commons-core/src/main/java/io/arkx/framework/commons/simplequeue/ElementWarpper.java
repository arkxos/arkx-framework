package io.arkx.framework.commons.simplequeue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.arkx.framework.commons.simplequeue.scheduler.PriorityScheduler;

/**
 * Object contains url to crawl.<br>
 * It contains some additional information.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class ElementWarpper implements Serializable {

	private static final long serialVersionUID = 2062192774891352043L;

	public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";

	private String url;

	/**
	 * Store additional information in extras.
	 */
	private Map<String, Object> extras;

	/**
	 * Priority of the request.<br>
	 * The bigger will be processed earlier. <br>
	 *
	 * @see PriorityScheduler
	 */
	private long priority;

	public ElementWarpper() {
	}

	public ElementWarpper(String url) {
		this.url = url;
	}

	public long getPriority() {
		return priority;
	}

	/**
	 * Set the priority of request for sorting.<br>
	 * Need a scheduler supporting priority.<br>
	 *
	 * @see PriorityScheduler
	 * @param priority priority
	 * @return this
	 */
	public ElementWarpper setPriority(long priority) {
		this.priority = priority;
		return this;
	}

	public Object getExtra(String key) {
		if (extras == null) {
			return null;
		}
		return extras.get(key);
	}

	public ElementWarpper putExtra(String key, Object value) {
		if (extras == null) {
			extras = new HashMap<String, Object>();
		}
		extras.put(key, value);
		return this;
	}

	public String get() {
		return url;
	}

	public Map<String, Object> getExtras() {
		return extras;
	}

	public ElementWarpper setExtras(Map<String, Object> extras) {
		this.extras = extras;
		return this;
	}

	public ElementWarpper setUrl(String url) {
		this.url = url;
		return this;
	}

	@Override
	public int hashCode() {
		int result = url != null ? url.hashCode() : 0;
		result = 31 * result;
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ElementWarpper request = (ElementWarpper) o;

		if (url != null ? !url.equals(request.url) : request.url != null)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Request{" + "url='" + url + '\'' + ", extras=" + extras + ", priority=" + priority + '}';
	}

}
