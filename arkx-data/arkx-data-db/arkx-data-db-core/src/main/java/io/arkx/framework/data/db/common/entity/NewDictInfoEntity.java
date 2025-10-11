package io.arkx.framework.data.db.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "new_dict_info", autoResultMap = true)
public class NewDictInfoEntity {

    /**
     * 主键ID，自动递增
     */
    @TableId(value = "YTH_DICT_ID", type = IdType.AUTO)
    private Integer ythDictId;

    /**
     * 父级Id
     */
    @TableField("PARENT_YTH_DICT_ID")
    private Integer parentNewDictId;
    /**
     * 字典值
     */
    @TableField("DICT_CODE")
    private String dictCode;
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
     * 序号
     */
    @TableField("SORT_VALUE")
    private Integer sortValue;

    /*
     * 如果作为数据字典列表查询，可查询是否已经映射过，方便前端进行区分标记
     */
    @TableField(exist = false)
    private boolean hasMapped;

}

