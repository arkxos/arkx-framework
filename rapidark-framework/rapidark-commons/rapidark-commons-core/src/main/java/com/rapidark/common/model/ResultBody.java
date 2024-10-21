package com.rapidark.common.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.rapidark.common.constants.ErrorCode;
import com.rapidark.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 所有接口调用返回的统一包装结果类
 */
@Getter
@Setter
@ApiModel(value = "响应结果")
public class ResultBody<T> implements Serializable {
    private static final long serialVersionUID = -6190689122701100762L;

    @ApiModelProperty(value = "业务编码")
    private String bizId;

    /**
     * 响应编码
     */
    @ApiModelProperty(value = "响应编码:0-请求处理成功")
    private int code = 0;

    /**
     * 提示消息
     */
    @ApiModelProperty(value = "提示消息")
    private String message;

    /**
     * 请求路径
     */
    @ApiModelProperty(value = "请求路径")
    private String path;

    /**
     * 响应数据
     */
    @ApiModelProperty(value = "响应数据")
    private T data;

    /**
     * http状态码
     */
    private int httpStatus;

    /**
     * 附加数据
     */
    @ApiModelProperty(value = "附加数据")
    private Map<String, Object> extra;

    /**
     * 响应时间
     */
    @ApiModelProperty(value = "响应时间")
    private long timestamp = System.currentTimeMillis();

    public ResultBody() {
        super();
    }

    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public int getHttpStatus() {
        return httpStatus;
    }

    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public boolean isOk() {
        return this.code == ErrorCode.OK.getCode();
    }


    public static <T> ResultBody<T> ok() {
        return new ResultBody<T>().code(ErrorCode.OK.getCode()).msg(ErrorCode.OK.getMessage());
    }

    public static <T> ResultBody<T> ok(T data) {
        ResultBody<T> result = new ResultBody<>();

        return result.code(ErrorCode.OK.getCode()).msg(ErrorCode.OK.getMessage()).data(data);
    }

    public static ResultBody<String> failed() {
        return new ResultBody<String>().code(ErrorCode.FAIL.getCode()).msg(ErrorCode.FAIL.getMessage());
    }

    public static <T> ResultBody<T> failed(String message) {
        return new ResultBody<T>().code(ErrorCode.FAIL.getCode()).msg(message);
    }

    public static <T> ResultBody<T> failed(T data) {
        return new ResultBody<T>().code(ErrorCode.FAIL.getCode()).msg(ErrorCode.FAIL.getMessage()).data(data);
    }

    public ResultBody<T> bizId(String bizId) {
        this.bizId = bizId;
        return this;
    }

    public ResultBody<T> code(int code) {
        this.code = code;
        return this;
    }

    public ResultBody<T> msg(String message) {
        if (ErrorCode.BAD_REQUEST.getCode() == this.code || ErrorCode.ERROR.getCode() == this.code) {
            this.message = i18n(ErrorCode.getResultEnum(this.code).getMessage(), message) + "("+message+")";
        } else {
            this.message = i18n(ErrorCode.getResultEnum(this.code).getMessage(), message);
        }
        if (!StringUtils.isEmpty(message)) {
            this.message = message;
        }
        return this;
    }

    public ResultBody<T> data(T data) {
        this.data = data;
        return this;
    }

    public ResultBody<T> path(String path) {
        this.path = path;
        return this;
    }

    public ResultBody<T> httpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public ResultBody<T> put(String key, Object value) {
        if (this.extra == null) {
            this.extra = Maps.newHashMap();
        }
        this.extra.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "ResultBody{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", data=" + data +
                ", httpStatus=" + httpStatus +
                ", extra=" + extra +
                ", timestamp=" + timestamp +
                '}';
    }

    /**
     * 错误信息配置
     */
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("error");

    /**
     * 提示信息国际化
     */
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    private static String i18n(String message, String defaultMessage) {
        return resourceBundle.containsKey(message) ? resourceBundle.getString(message) : defaultMessage;
    }
}
