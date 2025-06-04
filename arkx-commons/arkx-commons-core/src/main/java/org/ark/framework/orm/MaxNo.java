package org.ark.framework.orm;

import io.arkx.framework.annotation.Column;
import com.arkxos.framework.data.jdbc.Entity;

/**
 * @class org.ark.framework.orm.MaxNo
 * @author Darkness
 * @date 2012-11-25 下午06:01:07
 * @version V1.0
 */
@io.arkx.framework.annotation.Entity(name = "pt_comp__maxno")
public class MaxNo extends Entity {
	
	private static final long serialVersionUID = 1L;
	
	private String noType;
	private String noSubType;

	@Column(name = "NoMaxValue")
	private Long maxValue;
	private Long length;

	public String getNoType() {
		return noType;
	}

	public void setNoType(String noType) {
		this.noType = noType;
	}

	public String getNoSubType() {
		return noSubType;
	}

	public void setNoSubType(String noSubType) {
		this.noSubType = noSubType;
	}

	public Long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Long maxValue) {
		this.maxValue = maxValue;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}
}
