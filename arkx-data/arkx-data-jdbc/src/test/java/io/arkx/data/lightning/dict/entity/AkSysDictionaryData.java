package io.arkx.data.lightning.dict.entity;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-20 17:45
 * @since 1.0
 */
import lombok.Data;

import java.util.Objects;

/**
 * 系统字典数据实体类
 * 对应表: ak_sys_dictionary_data
 */
@Data
public class AkSysDictionaryData {
    private String id;                   // 主键ID
    private String dictId;              // 字典ID
    private String dictName;            // 字典名称
    private String dictDataParentId;     // 字典数据父级ID
    private String dictParent;          // 字典父级
    private String dictKey;             // 字典键
    private String dictValue;           // 字典值
    private String color;               // 颜色
    private Integer ordinal;            // 排序号
    private Integer dictVersion;        // 字典版本（非空）
    private String preDictKey;          // 前一个字典键
    private String preDictValue;        // 前一个字典值
    private Long createTime;           // 创建时间
    private Long updateTime;           // 更新时间
    private String preDictDataId;       // 前一个字典数据ID
    private Integer virtualFlag = 0;        // 虚拟标志（非空）

}
