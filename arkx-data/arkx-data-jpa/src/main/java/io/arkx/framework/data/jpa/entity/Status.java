package io.arkx.framework.data.jpa.entity;

import lombok.Getter;

/**
 * 数据状态
 * @author Darkness
 * @date 2020-09-15 17:01:16
 * @version V1.0
 */
@Getter
public enum Status {

	DELETED(0), ENABLED(1), DISABLED(2), LOCKED(3), UPDATING(4);

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
