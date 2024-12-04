/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rapidark.cloud.platform.common.core.util;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.rapidark.framework.common.constants.ErrorCode;
import com.rapidark.framework.common.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.ResourceBundle;

import com.rapidark.cloud.platform.common.core.constant.CommonConstants;

/**
 * 所有接口调用返回的统一包装结果类
 *
 * @param <T>
 * @author Darkness
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldNameConstants
@Schema(description = "响应结果")
public class ResponseResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "业务编码")
	private String bizId;

	@Schema(description = "请求路径")
	private String path;

	@Getter
	@Setter
	@Schema(description = "响应编码:0-请求处理成功")
	private int code;

	/**
	 * http状态码
	 */
	private int httpStatus;

	@Getter
	@Setter
	@Schema(description = "提示消息")
	private String msg;

	@Getter
	@Setter
	@Schema(description = "响应数据")
	private T data;

	@Schema(description = "附加数据")
	private Map<String, Object> extra;

	@Schema(description = "响应时间")
	private LocalDateTime timestamp;

	@Schema(description = "耗时")
	private long cost;

	public static <T> ResponseResult<T> ok() {
		return restResult(null, ErrorCode.OK.getCode(), null);
	}

	public static <T> ResponseResult<T> ok(T data) {
		return restResult(data, CommonConstants.SUCCESS, null);
	}

	public static <T> ResponseResult<T> ok(T data, String msg) {
		return restResult(data, CommonConstants.SUCCESS, msg);
	}

	public static <T> ResponseResult<T> failed() {
		return restResult(null, CommonConstants.FAIL, null);
	}

	public static <T> ResponseResult<T> failed(String msg) {
		return restResult(null, CommonConstants.FAIL, msg);
	}

	public static <T> ResponseResult<T> failed(T data) {
		return restResult(data, CommonConstants.FAIL, null);
	}

	public static <T> ResponseResult<T> failed(T data, String msg) {
		return restResult(data, CommonConstants.FAIL, msg);
	}

	public static <T> ResponseResult<T> restResult(T data, int code, String msg) {
		ResponseResult<T> responseResult = new ResponseResult<>();
		responseResult.setCode(code);
		responseResult.setData(data);
		responseResult.setMsg(msg);
		return responseResult;
	}

	public ResponseResult<T> bizId(String bizId) {
		this.bizId = bizId;
		return this;
	}

	public ResponseResult<T> code(int code) {
		this.code = code;
		return this;
	}

	public ResponseResult<T> msg(String message) {
		if (ErrorCode.BAD_REQUEST.getCode() == this.code || ErrorCode.ERROR.getCode() == this.code) {
			this.msg = i18n(ErrorCode.getResultEnum(this.code).getMessage(), message) + "("+message+")";
		} else {
			this.msg = i18n(ErrorCode.getResultEnum(this.code).getMessage(), message);
		}
		if (!StringUtils.isEmpty(message)) {
			this.msg = message;
		}
		return this;
	}


	public ResponseResult<T> data(T data) {
		this.data = data;
		return this;
	}

	public ResponseResult<T> path(String path) {
		this.path = path;
		return this;
	}

	public ResponseResult<T> httpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
		return this;
	}

	public ResponseResult<T> put(String key, Object value) {
		if (this.extra == null) {
			this.extra = Maps.newHashMap();
		}
		this.extra.put(key, value);
		return this;
	}


	public LocalDateTime getTimestamp() {
		if(timestamp == null) {
			timestamp = LocalDateTime.now();
		}
        return timestamp;
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

	@Override
	public String toString() {
		return "ResultBody{" +
				"code=" + code +
				", message='" + msg + '\'' +
				", path='" + path + '\'' +
				", data=" + data +
				", httpStatus=" + httpStatus +
				", extra=" + extra +
				", timestamp=" + timestamp +
				'}';
	}

}
