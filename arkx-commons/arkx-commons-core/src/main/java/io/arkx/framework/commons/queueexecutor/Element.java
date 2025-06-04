package io.arkx.framework.commons.queueexecutor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 
 * @author Darkness
 * @date 2016年11月11日 下午5:20:25
 * @version V1.0
 */
public class Element<T> {

//	public static final int VALIDATE = 1;
//	public static final int INVALIDATE = 0;
//	
	public static final String CYCLE_TRIED_TIMES = "cycle_tried_times";

	private Map<String, Object> extraMap = new HashMap<>();

	int retryCount = 0;
	private ElementStatus status = ElementStatus.New;
	
	private String id;
	
	private T source;
	
	public Element(String id, T source) {
		this.id = id;
		this.source = source;
	}
	
	public T getSource() {
		return source;
	}
	
	public String getId() {
		return this.id;
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
	
	public ElementStatus getStatus() {
		return status;
	}

	public void setStatus(ElementStatus status) {
		this.status = status;
	}

	public void finished() {
		status = ElementStatus.Finished;
	}

	public boolean isFinished() {
		return status == ElementStatus.Finished;
	}

	public void error() {
		this.status = ElementStatus.Error;
	}

	public boolean isError() {
		return this.status == ElementStatus.Error;
	}
	
	public static void main(String[] args) {
		System.out.println(DigestUtils.md5Hex("a"));
	}
}
