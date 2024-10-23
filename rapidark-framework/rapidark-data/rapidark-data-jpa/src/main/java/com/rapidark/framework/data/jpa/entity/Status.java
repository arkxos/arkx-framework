package com.rapidark.framework.data.jpa.entity;

import lombok.Getter;

/**
 * 数据状态
 * @author Darkness
 * @date 2020-09-15 17:01:16
 * @version V1.0
 */
@Getter
public enum Status {

	ENABLED(1), DISABLED(2), DELETED(3), LOCKED(4);

	private int code;

	Status(int code) {
		this.code = code;
	}

    public static Status codeOf(int code) {
		for (Status type : Status.values()) {
			if (type.getCode() == code) {
				return type;
			}
		}
		return null;
	}
}
