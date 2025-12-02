package io.arkx.framework.core;

import io.arkx.framework.commons.collection.Mapx;

import com.alibaba.fastjson.JSON;

/**
 *
 * if(data instanceof DataTable) { this.data =
 * io.arkx.framework.json.JSON.toJSONString((DataTable)data); }
 *
 * @author Darkness
 * @date 2016年12月19日 下午3:21:04
 * @version V1.0
 */
public class JsonResult2<T> {

	public static <T> JsonResult2<T> createErrorResult(String message) {
		return new JsonResult2<T>(false, message, null);
	}

	public static <T> JsonResult2<T> createSuccessResult(String message) {
		return new JsonResult2<T>(message, null);
	}

	public static <T> JsonResult2<T> createSuccessResult(String message, T data) {
		return new JsonResult2<T>(message, data);
	}

	public static <T> JsonResult2<T> createErrorResult(String message, T data) {
		return new JsonResult2<T>(false, message, data);
	}

	private boolean success = true;

	private String statusCode;

	private String message;

	private T data;

	private Mapx<String, Object> extraData = new Mapx<>();

	public JsonResult2() {
	}

	public JsonResult2(String message) {
		this(true, message, null);
	}

	public JsonResult2(String message, T data) {
		this(true, message, data);
	}

	private JsonResult2(boolean success, String message, T data) {
		this.success = success;
		this.message = message;
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getData() {
		return data;
	}

	public JsonResult2<T> setData(T data) {
		this.data = data;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Mapx<String, Object> getExtraData() {
		return extraData;
	}

	public void setExtraData(Mapx<String, Object> extraData) {
		this.extraData = extraData;
	}

	public void putExtraData(String key, Object value) {
		this.extraData.put(key, value);
	}

	public void setErrorMessage(String message) {
		this.message = message;
		this.success = false;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

}
