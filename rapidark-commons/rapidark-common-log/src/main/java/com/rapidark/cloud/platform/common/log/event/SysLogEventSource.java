package com.rapidark.cloud.platform.common.log.event;

import com.rapidark.cloud.platform.admin.api.entity.SysLog;

import lombok.Data;

/**
 * spring event log
 *
 * @author lengleng
 * @date 2023/8/11
 */
@Data
public class SysLogEventSource extends SysLog {

	/**
	 * 参数重写成object
	 */
	private Object body;

}
