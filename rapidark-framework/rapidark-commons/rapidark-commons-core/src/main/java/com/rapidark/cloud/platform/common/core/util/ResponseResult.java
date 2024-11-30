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

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.rapidark.cloud.platform.common.core.constant.CommonConstants;

/**
 * 所有接口调用返回的统一包装结果类
 *
 * @param <T>
 * @author lengleng
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldNameConstants
public class ResponseResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private int code;

	@Getter
	@Setter
	private String msg;

	@Getter
	@Setter
	private T data;

	private LocalDateTime timestamp;

	public static <T> ResponseResult<T> ok() {
		return restResult(null, CommonConstants.SUCCESS, null);
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

	public LocalDateTime getTimestamp() {
		if(timestamp == null) {
			timestamp = LocalDateTime.now();
		}
        return timestamp;
    }

}
