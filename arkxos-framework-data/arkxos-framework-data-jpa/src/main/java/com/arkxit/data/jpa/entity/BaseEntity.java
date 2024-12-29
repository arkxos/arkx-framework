package com.arkxit.data.jpa.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author Darkness
 * @date 2019-08-18 13:56:12
 * @version V1.0
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<PK extends Serializable> implements Serializable {

	/* 分组校验 */
	public @interface Create {}

	/* 分组校验 */
	public @interface Update {}

	@CreationTimestamp
	@Column(name = "create_time", updatable = false)
	@Schema(description = "创建时间", hidden = true)
	private LocalDateTime createTime;

	@UpdateTimestamp
	@Column(name = "update_time")
	@Schema(description = "更新时间", hidden = true)
	private LocalDateTime updateTime;

	//    @CreatedBy
	//    @Column(name = "CREATOR_ID", updatable = false)
	@CreatedBy
	@Column(name = "create_by", updatable = false)
	@Schema(description = "创建人", hidden = true)
	private String createBy;// 创建人

	@LastModifiedBy
	@Column(name = "update_by")
	@Schema(description = "更新人", hidden = true)
	private String updateBy;

	/**
     * EnumType:  ORDINAL 枚举序数  默认选项（int）。eg:TEACHER 数据库存储的是 0
     *            STRING：枚举名称       (String)。eg:TEACHER 数据库存储的是 "TEACHER"
     */
    @Enumerated(EnumType.ORDINAL)  
	private Status status = Status.ENABLED;

	/**
	 * 给当前实体对象生成一个新的id
	 * 
	 * @method generateNewId
	 * 
	 * @author Darkness
	 * @date 2013-1-31 下午04:52:33
	 * @version V1.0
	 */
//	public void generateNewId() {
//		this.id = generateId();
//	}

	public abstract PK getId();

	public abstract void setId(PK id);

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		Field[] fields = this.getClass().getDeclaredFields();
		try {
			for (Field f : fields) {
				f.setAccessible(true);
				builder.append(f.getName(), f.get(this)).append("\n");
			}
		} catch (Exception e) {
			builder.append("toString builder encounter an error");
		}
		return builder.toString();
	}

}
