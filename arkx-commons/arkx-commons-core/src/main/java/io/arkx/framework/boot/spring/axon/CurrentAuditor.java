package io.arkx.framework.boot.spring.axon;

import io.arkx.framework.Current;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Darkness
 * @date 2019-08-18 11:40:14
 * @version V1.0
 */
@Data
public class CurrentAuditor implements Auditor {

	public static Auditor get() {
		return (Auditor) Current.get("AXON_AUDITOR");
	}

	public static void set(Auditor auditor) {
		Current.put("AXON_AUDITOR", auditor);
	}
	
	public CurrentAuditor() {
		this.occurredOn = LocalDateTime.now();
	}
	
	private String operatorId;// 操作人id
	private String operatorRealName;
	private String operatorBranchInnerCode;// 操作人部门编码
	private String operatorBranchName;
	private LocalDateTime occurredOn;

}
