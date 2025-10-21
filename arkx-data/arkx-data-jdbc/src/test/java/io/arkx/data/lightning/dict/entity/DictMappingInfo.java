package io.arkx.data.lightning.dict.entity;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-20 18:12
 * @since 1.0
 */
import java.util.Objects;

/**
 * 字典映射信息实体类
 * 对应表: dict_mapping_info
 */
public class DictMappingInfo {
    private Long id;                   // ID (主键)
    private Integer newDictId;         // 新字典ID
    private Integer parentNewDictId;   // 父级新字典ID
    private Integer rawDictId;         // 原始字典ID
    private Integer parentRawDictId;   // 父级原始字典ID
    private String newDictName;        // 新字典名称
    private String rawDictName;        // 原始字典名称

    // 无参构造函数
    public DictMappingInfo() {
    }

    // 全参构造函数
    public DictMappingInfo(Long id, Integer newDictId, Integer parentNewDictId,
                           Integer rawDictId, Integer parentRawDictId,
                           String newDictName, String rawDictName) {
        this.id = id;
        this.newDictId = newDictId;
        this.parentNewDictId = parentNewDictId;
        this.rawDictId = rawDictId;
        this.parentRawDictId = parentRawDictId;
        this.newDictName = newDictName;
        this.rawDictName = rawDictName;
    }

    // Getter 和 Setter 方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNewDictId() { return newDictId; }
    public void setNewDictId(Integer newDictId) { this.newDictId = newDictId; }

    public Integer getParentNewDictId() { return parentNewDictId; }
    public void setParentNewDictId(Integer parentNewDictId) { this.parentNewDictId = parentNewDictId; }

    public Integer getRawDictId() { return rawDictId; }
    public void setRawDictId(Integer rawDictId) { this.rawDictId = rawDictId; }

    public Integer getParentRawDictId() { return parentRawDictId; }
    public void setParentRawDictId(Integer parentRawDictId) { this.parentRawDictId = parentRawDictId; }

    public String getNewDictName() { return newDictName; }
    public void setNewDictName(String newDictName) { this.newDictName = newDictName; }

    public String getRawDictName() { return rawDictName; }
    public void setRawDictName(String rawDictName) { this.rawDictName = rawDictName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictMappingInfo that = (DictMappingInfo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DictMappingInfo{" +
                "id=" + id +
                ", newDictId=" + newDictId +
                ", parentNewDictId=" + parentNewDictId +
                ", rawDictId=" + rawDictId +
                ", parentRawDictId=" + parentRawDictId +
                ", newDictName='" + newDictName + '\'' +
                ", rawDictName='" + rawDictName + '\'' +
                '}';
    }
}