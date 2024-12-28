package com.rapidark.framework.boot.spring.axon;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Darkness
 * @date 2019-08-18 11:19:44
 * @version V1.0
 */
@Getter
@Setter
public class BaseEvent implements Auditor {

	private String operatorId;// 操作人id
	private String operatorRealName;
	private String operatorBranchInnerCode;// 操作人部门编码
	private String operatorBranchName;
	private LocalDateTime occurredOn;
	
	public void init(Auditor auditor) {
		this.setOperatorId(auditor.getOperatorId());
		this.setOperatorRealName(auditor.getOperatorRealName());
		this.setOperatorBranchInnerCode(auditor.getOperatorBranchInnerCode());
		this.setOperatorBranchName(auditor.getOperatorBranchName());
		this.setOccurredOn(auditor.getOccurredOn());
	}
}

