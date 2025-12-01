// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/// //////////////////////////////////////////////////////////
package io.arkx.framework.data.db.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.arkx.framework.commons.collection.tree.TreeNodeData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "raw_dict_info", autoResultMap = true)
public class RawDictInfoEntity implements TreeNodeData<Integer> {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 父级Id
     */
    @TableField("PARENT_ID")
    private Integer parentId;
    /**
     * 字典类型
     */
    @TableField("DICT_TYPE")
    private String dictType;
    /**
     * 字典名称
     */
    @TableField("DICT_NAME")
    private String dictName;

    /**
     * 状态 0：无效  1：有效
     */
    @TableField("STATUS")
    private Integer status;
    /**
     *
     */
    @TableField("DEEP")
    private Integer deep;
    /**
     * 字典值
     */
    @TableField("OLD_VALUE")
    private String oldValue;
    /**
     * 序号
     */
    @TableField("SORT_VALUE")
    private Integer sortValue;
    /**
     *
     */
    @TableField("SYNC_FLAG")
    private String syncFlag;
    /**
     *
     */
    @TableField("INCR_FLAG")
    private String incrFlag;
    /**
     *
     */
    @TableField("INCR_FINISH_TIME")
    private String incrFinishTime;
    /**
     *
     */
    @TableField("DEL_FLAG")
    private String delFlag;
    /**
     *
     */
    @TableField("INCR_SYNC_STATUS")
    private String incrSyncStatus;
    /**
     *
     */
    @TableField("OWERS")
    private String owers;

    /*
     * 如果作为数据字典列表查询，可查询是否已经映射过，方便前端进行区分标记
     */
    @TableField(exist = false)
    private boolean hasMapped;

    @Override
    public String getName() {
        return this.dictName;
    }

    @Override
    public int getSortOrder() {
        if (sortValue == null) {
            return 0;
        }
        return sortValue;
    }

}
