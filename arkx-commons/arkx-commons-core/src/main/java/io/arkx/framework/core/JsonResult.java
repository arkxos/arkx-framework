package io.arkx.framework.core;

import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Mapx;

import com.alibaba.fastjson.JSON;

/**
 * @author Darkness
 * @date 2016年12月19日 下午3:21:04
 * @version V1.0
 */
public class JsonResult {

    public static JsonResult error(String message) {
        return new JsonResult(false, message, null);
    }

    public static JsonResult createErrorResult(String message) {
        return new JsonResult(false, message, null);
    }

    public static JsonResult success(String message) {
        return new JsonResult(message);
    }

    public static JsonResult success(String message, Object data) {
        return new JsonResult(message, data);
    }

    public static JsonResult createSuccessResult(String message) {
        return new JsonResult(message);
    }

    public static JsonResult createSuccessResult(String message, Object data) {
        return new JsonResult(message, data);
    }

    public static JsonResult createErrorResult(String message, Object data) {
        return new JsonResult(false, message, data);
    }

    private boolean success = true;

    private String statusCode = "20000";

    private String message;

    private Object data;

    private Mapx<String, Object> extraData = new Mapx<>();

    public JsonResult() {
    }

    public JsonResult(String message) {
        this(true, message, null);
    }

    public JsonResult(String message, Object data) {
        this(true, message, data);
    }

    private JsonResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;

        if (data instanceof DataTable) {
            this.data = io.arkx.framework.json.JSON.toJSONString((DataTable) data);
        } else {
            this.data = data;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public JsonResult setData(Object data) {
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
