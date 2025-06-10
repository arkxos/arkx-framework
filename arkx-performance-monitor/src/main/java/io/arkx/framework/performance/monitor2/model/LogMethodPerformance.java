package io.arkx.framework.performance.monitor2.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Darkness
 * @date 2013-7-22 下午08:42:12
 * @version V1.0
 */
@Setter
@Getter
public class LogMethodPerformance implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private String className;// 类名
	private String methodName;// 方法名
	private long avgTime;// 平均耗时

    // ======================================
	// =============基础字段 开始================

	private String id;
	private Date createTime;// 创建时间，不用set,hibernate会自动把当前时间写入
	private String creator;// 创建人
	private String creatorName;
	private Date modifyTime;// 修改时间
	private String modifior;// 修改人
	private String modifyName;
	private Integer isUse = 1;// 状态标志：启用/禁用
	private Double recordSort;// 排序号

    // =============基础字段 结束================
	// ======================================
}
