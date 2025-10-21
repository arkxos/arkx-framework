package io.arkx.data.lightning.dict.entity;

import io.arkx.framework.commons.collection.tree.TreeNodeData;
import lombok.Data;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-20 17:16
 * @since 1.0
 */
@Data
public class NewDictInfo implements TreeNodeData<Integer> {

    private Integer id;
    private Integer parentId;
    private String code;
    private String name;
    private int sortValue;

}
