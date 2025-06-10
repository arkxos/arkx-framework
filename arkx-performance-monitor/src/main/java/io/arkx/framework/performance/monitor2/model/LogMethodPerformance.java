package io.arkx.framework.performance.monitor2.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Darkness
 * @date 2013-7-22 下午08:42:12
 * @version V1.0
 */
public class LogMethodPerformance implements Serializable {

	private String className;// 类名
	private String methodName;// 方法名
	private long avgTime;// 平均耗时

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public long getAvgTime() {
		return avgTime;
	}

	public void setAvgTime(long avgTime) {
		this.avgTime = avgTime;
	}
	
	// ======================================
	// =============基础字段 开始================
	private static final long serialVersionUID = 1L;

	private String id;
	private Date createTime;// 创建时间，不用set,hibernate会自动把当前时间写入
	private String creator;// 创建人
	private String creatorName;
	private Date modifyTime;// 修改时间
	private String modifior;// 修改人
	private String modifyName;
	private Integer isUse = 1;// 状态标志：启用/禁用
	private Double recordSort;// 排序号

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getModifior() {
		return modifior;
	}

	public void setModifior(String modifior) {
		this.modifior = modifior;
	}

	public String getModifyName() {
		return modifyName;
	}

	public void setModifyName(String modifyName) {
		this.modifyName = modifyName;
	}

	public Integer getIsUse() {
		return isUse;
	}

	public void setIsUse(Integer use) {
		isUse = use;
	}

	public Double getRecordSort() {
		return recordSort;
	}

	public void setRecordSort(Double recordSort) {
		this.recordSort = recordSort;
	}

	// =============基础字段 结束================
	// ======================================
}
