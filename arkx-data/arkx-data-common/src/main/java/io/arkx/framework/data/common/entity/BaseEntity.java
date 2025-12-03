package io.arkx.framework.data.common.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Darkness
 * @date 2019-08-18 13:56:12
 * @version V1.0
 */
@Getter
@Setter
@MappedSuperclass
// @EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<ID> implements Persistable<ID>, Identifier<ID>, Serializable {

    /* 分组校验 */
    public @interface Create {

    }

    /* 分组校验 */
    public @interface Update {

    }

    @jakarta.persistence.Id
    @Id
    protected ID id;

    @Column(name = "create_time", updatable = false)
    @Schema(description = "创建时间", hidden = true)
    @CreatedDate
    private LocalDateTime createTime;

    @Column(name = "update_time")
    @Schema(description = "更新时间", hidden = true)
    @LastModifiedDate
    private LocalDateTime updateTime;

    // @CreatedBy
    // @Column(name = "CREATOR_ID", updatable = false)
    @CreatedBy
    @Column(name = "create_by", updatable = false)
    @Schema(description = "创建人", hidden = true)
    private String createBy;// 创建人

    @LastModifiedBy
    @Column(name = "update_by")
    @Schema(description = "更新人", hidden = true)
    private String updateBy;

    /**
     * EnumType: ORDINAL 枚举序数 默认选项（int）。eg:TEACHER 数据库存储的是 0 STRING：枚举名称
     * (String)。eg:TEACHER 数据库存储的是 "TEACHER"
     */
    @Enumerated(EnumType.ORDINAL)
    // @ValueConverter
    private Status status = Status.ACTIVE;

    private int delFlag;

    // 这里我们使用一个瞬态字段`newEntity`来标记
    @Transient
    @jakarta.persistence.Transient
    private boolean newEntity;

    /**
     * 给当前实体对象生成一个新的id
     *
     * @method generateNewId
     *
     * @author Darkness
     * @date 2013-1-31 下午04:52:33
     * @version V1.0
     */
    // public void generateNewId() {
    // this.id = generateId();
    // }

    // 主键访问器（可被覆盖）
    @Override
    public ID getId() {
        return id;
    }

    @Override
    public void setId(ID id) {
        this.id = id;
    }

    public void bindNewId(ID id) {
        this.newEntity = true;
        this.setId(id);
    }

    @Override
    public boolean isNew() {
        if (this.getId() == null) {
            return true;
        }
        return newEntity;
    }

    /**
     * 获取主键(ID)的类型
     *
     * @return 主键类型的Class对象
     */
    @SuppressWarnings("unchecked")
    public Class<ID> findPkClass() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) superClass).getActualTypeArguments();
            if (typeArguments.length > 0) {
                Type pkType = typeArguments[0];
                if (pkType instanceof Class) {
                    return (Class<ID>) pkType;
                } else if (pkType instanceof ParameterizedType) {
                    return (Class<ID>) ((ParameterizedType) pkType).getRawType();
                }
            }
        }
        throw new IllegalStateException("无法确定主键类型: " + getClass().getName());
    }

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
