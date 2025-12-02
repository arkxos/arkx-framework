package io.arkx.framework.data.jdbc;

import java.util.Date;

import io.arkx.framework.annotation.Column;
import io.arkx.framework.annotation.Ingore;

/**
 * @class org.ark.framework.infrastructure.domainbase.BaseEntity
 * @extends org.ark.framework.infrastructure.domainbase.Entity 实体对象的基类，提供系统默认字段
 * @author Darkness
 * @date 2012-5-18 下午4:38:36
 * @version V1.0
 * @see io.arkx.framework.data.jpa.entity.BaseEntity
 */
// @MappedSuperclass
@Deprecated
public class BaseEntity extends Entity {

	private static final long serialVersionUID = 1L;

	/**
	 * 创建人[字段名称常量]
	 *
	 * @property CreatorId
	 * @type {String}
	 * @static
	 */
	@Ingore
	public static final String CreatorId = "creator_id";// 创建人

	/**
	 * 创建时间[字段名称常量]
	 *
	 * @property CreateTime
	 * @type {String}
	 * @static
	 */
	@Ingore
	public static final String CreateTime = "create_time";// 创建时间

	/**
	 * 修改人[字段名称常量]
	 *
	 * @property UpdatorId
	 * @type {String}
	 * @static
	 */
	@Ingore
	public static final String UpdatorId = "updator_id";// 修改人

	/**
	 * 修改时间[字段名称常量]
	 *
	 * @property UpdateTime
	 * @type {String}
	 * @static
	 */
	@Ingore
	public static final String UpdateTime = "update_time";// 修改时间

	/**
	 * 状态标志：启用/禁用[字段名称常量]
	 *
	 * @property UseFlag
	 * @type {String}
	 * @static
	 */
	@Ingore
	public static final String UseFlag = "USE_FLAG";// 状态标志：启用/禁用

	/**
	 * 删除标示[字段名称常量]
	 *
	 * @property DeleteStatus
	 * @type {String}
	 * @static
	 */
	@Ingore
	public static final String DeleteStatus = "DELETE_STATUS";// 删除标示

	/**
	 * 排序号[字段名称常量]
	 *
	 * @property SortOrder
	 * @type {String}
	 * @static
	 */
	@Ingore
	public static final String SortOrder = "sort_order";// 排序号

	// @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_TIME", updatable = false, length = 20)
	private Date createTime;// 创建时间，不用set,hibernate会自动把当前时间写入

	// @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATE_TIME")
	private Date updateTime;// 修改时间

	@Column(name = "CREATOR_ID", updatable = false)
	private String creatorId = null;// 创建人

	@Column(name = "UPDATOR_ID")
	private String updatorId = null;// 修改人

	@Column(name = "USE_FLAG")
	private String useFlag = "Y";// 状态标志：启用/禁用

	@Column(name = "DELETE_STATUS")
	private String deleteStatus = "N";// 删除标示

	@Column(name = "SORT_ORDER")
	private Long sortOrder = null;// 排序号

	/**
	 * 获取创建时间
	 *
	 * @method getCreateTime
	 * @return {Date}
	 *
	 * @author Darkness
	 * @date 2013-1-31 下午05:20:17
	 * @version V1.0
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * 设置创建时间
	 *
	 * @method setCreateTime
	 * @param {Date} createTime
	 *
	 * @author Darkness
	 * @date 2013-1-31 下午05:20:44
	 * @version V1.0
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取更新时间
	 *
	 * @method getUpdateTime
	 * @return {Date}
	 * @author Darkness
	 * @date 2013-1-31 下午05:21:12
	 * @version V1.0
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * 设置更新时间
	 *
	 * @method setUpdateTime
	 * @param {Date} updateTime
	 *
	 * @author Darkness
	 * @date 2013-1-31 下午05:21:34
	 * @version V1.0
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 获取创建人
	 *
	 * @method getCreatorId
	 * @return {String}
	 *
	 * @author Darkness
	 * @date 2013-1-31 下午05:22:05
	 * @version V1.0
	 */
	public String getCreatorId() {
		return creatorId;
	}

	/**
	 * 设置创建人
	 *
	 * @method setCreatorId
	 * @param {String} creatorId
	 *
	 * @author Darkness
	 * @date 2013-1-31 下午05:22:30
	 * @version V1.0
	 */
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	/**
	 * 获取更新人
	 *
	 * @method getUpdatorId
	 * @return {String}
	 *
	 * @author Darkness
	 * @date 2013-1-31 下午05:22:59
	 * @version V1.0
	 */
	public String getUpdatorId() {
		return updatorId;
	}

	/**
	 * 设置更新人
	 *
	 * @method setUpdatorId
	 * @param {String} updatorId
	 *
	 * @author Darkness
	 * @date 2013-1-31 下午05:23:28
	 * @version V1.0
	 */
	public void setUpdatorId(String updatorId) {
		this.updatorId = updatorId;
	}

	/**
	 * 获取启用状态，值为(Y/N)
	 *
	 * @method getUseFlag
	 * @return {String}
	 *
	 * @author Darkness
	 * @date 2013-1-31 下午05:23:54
	 * @version V1.0
	 */
	public String getUseFlag() {
		return useFlag;
	}

	/**
	 * 设置启用状态
	 *
	 * @method setUseFlag
	 * @param {String} useFlag(Y/N)
	 *
	 * @author Darkness
	 * @date 2013-1-31 下午05:24:27
	 * @version V1.0
	 */
	public void setUseFlag(String useFlag) {
		this.useFlag = useFlag;
	}

	/**
	 * 获取排序号
	 *
	 * @method getSortOrder
	 * @return {long}
	 *
	 * @author Darkness
	 * @date 2013-1-31 下午05:25:25
	 * @version V1.0
	 */
	public long getSortOrder() {
		if (sortOrder == null) {
			return 0;
		}
		return sortOrder;
	}

	/**
	 * 设置排序号
	 *
	 * @method setSortOrder
	 * @param {long} sortOrder
	 *
	 * @author Darkness
	 * @date 2013-1-31 下午05:25:48
	 * @version V1.0
	 */
	public void setSortOrder(long sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * 获取删除状态，值为(Y/N)
	 *
	 * @method getDeleteStatus
	 * @return {String}
	 *
	 * @author Darkness
	 * @date 2013-1-31 下午05:23:54
	 * @version V1.0
	 */
	public String getDeleteStatus() {
		return deleteStatus;
	}

	/**
	 * 设置删除状态
	 *
	 * @method setDeleteStatus
	 * @param {String} deleteStatus(Y/N)
	 *
	 * @author Darkness
	 * @date 2013-1-31 下午05:24:27
	 * @version V1.0
	 */
	public void setDeleteStatus(String deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public String toString() {
		return getClass().getName() + "==> id:" + getId();
	}

}
