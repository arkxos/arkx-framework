package io.arkx.data.lightning.dict.entity;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-20 17:45
 * @since 1.0
 */
import java.util.Objects;

/**
 * 系统字典实体类
 * 对应表: ak_sys_dictionary
 */
public class AkSysDictionary {
    private String id;           // 主键ID，varchar(36)
    private String parentId;     // 父级ID，varchar(20)
    private String code;         // 字典编码，varchar(50)
    private String name;         // 字典名称，varchar(50)
    private String description;  // 字典描述，varchar(500)
    private String builtin;      // 是否内置，varchar(2)
    private Long createTime;     // 创建时间，bigint
    private Long updateTime;     // 更新时间，bigint

    // 无参构造函数
    public AkSysDictionary() {
    }

    // 全参构造函数
    public AkSysDictionary(String id, String parentId, String code, String name,
                           String description, String builtin, Long createTime, Long updateTime) {
        this.id = id;
        this.parentId = parentId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.builtin = builtin;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // Getter 和 Setter 方法
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBuiltin() { return builtin; }
    public void setBuiltin(String builtin) { this.builtin = builtin; }

    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }

    public Long getUpdateTime() { return updateTime; }
    public void setUpdateTime(Long updateTime) { this.updateTime = updateTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AkSysDictionary that = (AkSysDictionary) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AkSysDictionary{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", builtin='" + builtin + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
