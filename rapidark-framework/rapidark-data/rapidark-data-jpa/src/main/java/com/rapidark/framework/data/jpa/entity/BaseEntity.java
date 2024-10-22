package com.rapidark.framework.data.jpa.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.rapidark.framework.commons.util.UuidUtil;

import lombok.Data;

/**
 * @author Darkness
 * @date 2019-08-18 13:56:12
 * @version V1.0
 */
@Data
@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@Id
	@Column(length = 22)
	// @GeneratedValue(generator  = "myIdStrategy")
	// @GenericGenerator(name = "myIdStrategy", strategy = "assigned")
	protected String id;

	// @CreatedDate
	private LocalDateTime createTime;

	//    @LastModifiedDate
	private LocalDateTime updateTime;

	//    @CreatedBy
	//    @Column(name = "CREATOR_ID", updatable = false)
	@Column(length = 50)
	private String creatorId = null;// 创建人

	//	@Column(name = "UPDATOR_ID")
	//    @LastModifiedBy
	@Column(length = 50)
	private String updatorId = null;// 修改人

	/**
	 * 使用status字段替换
	 */
	@Deprecated
	@Column(name = "USE_FLAG", length = 1)
	private String useFlag = "Y";// 状态标志：启用/禁用

	/**
	 * 使用status字段替换
	 */
	@Deprecated
	@Column(name = "DELETE_STATUS", length = 1)
	private String deleteStatus = "N";// 删除标示
	
	/**
     * EnumType:  ORDINAL 枚举序数  默认选项（int）。eg:TEACHER 数据库存储的是 0
     *            STRING：枚举名称       (String)。eg:TEACHER 数据库存储的是 "TEACHER"
     */
    @Enumerated(EnumType.ORDINAL)  
	private Status status = Status.ENABLED;

	@Column(name = "SORT_ORDER")
	private long sortOrder = 0;// 排序号

	/**
	 * 给当前实体对象生成一个新的id
	 * 
	 * @method generateNewId
	 * 
	 * @author Darkness
	 * @date 2013-1-31 下午04:52:33
	 * @version V1.0
	 */
	public void generateNewId() {
		this.id = generateId();
	}

	/**
	 * 生成uuid，不包含“-”
	 * 
	 * @static
	 * 
	 * @author Darkness
	 * @date 2013-3-13 下午03:14:17
	 * @version V1.0
	 */
	public static String generateId() {
		return UuidUtil.base58Uuid();// UUID.randomUUID().toString().replaceAll("-", "");
	}
}
