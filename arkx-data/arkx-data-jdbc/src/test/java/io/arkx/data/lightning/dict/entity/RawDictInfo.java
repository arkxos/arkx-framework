package io.arkx.data.lightning.dict.entity;

import io.arkx.framework.commons.collection.tree.TreeNodeData;
import lombok.Data;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-20 17:53
 * @since 1.0
 */
@Data
public class RawDictInfo implements TreeNodeData<Integer> {

    private Integer id;                    // ID (主键)
    private Integer parentId;             // 父级ID
    private String dictType;              // 字典类型
    private String name;              // 字典名称
    private String status;                // 状态
    private String deep;                  // 深度
    private String petitionVal;           // 请愿值
    private String oldValue;              // 旧值
    private Integer sortValue;            // 排序值
    private String syncFlag;              // 同步标志
    private String incrFlag;              // 增量标志
    private String incrFinishTime;       // 增量完成时间
    private String delFlag;               // 删除标志
    private String incrSyncStatus;        // 增量同步状态
    private String owers;                 // 所有者

}
