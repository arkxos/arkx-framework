package io.arkx.framework.data.common.entity;

import lombok.Getter;

/**
 * 数据状态
 *
 * @author Darkness
 * @date 2020-09-15 17:01:16
 * @version V1.0
 */
@Getter
public enum Status {

	DELETED(0, "已删除"), ACTIVE(1, "激活"), INACTIVE(2, "停用"), LOCKED(3, "锁定"), PENDING(4, "待处理");

	private final int code;

	private final String label;

	Status(int code, String label) {
		this.code = code;
		this.label = label;
	}

	public static Status fromCode(int code) {
		for (Status type : Status.values()) {
			if (type.getCode() == code) {
				return type;
			}
		}
		return null;
	}

}
