package io.arkx.framework.boot.spring.axon;

import java.time.LocalDateTime;

/**
 * @author Darkness
 * @date 2019-08-18 11:24:19
 * @version V1.0
 */
public interface Auditor {

	String getOperatorId();

	void setOperatorId(String operatorId);

	String getOperatorRealName();

	void setOperatorRealName(String operatorRealName);

	String getOperatorBranchInnerCode();

	void setOperatorBranchInnerCode(String operatorBranchInnerCode);

	String getOperatorBranchName();

	void setOperatorBranchName(String operatorBranchName);

	LocalDateTime getOccurredOn();

	void setOccurredOn(LocalDateTime occurredOn);

}
