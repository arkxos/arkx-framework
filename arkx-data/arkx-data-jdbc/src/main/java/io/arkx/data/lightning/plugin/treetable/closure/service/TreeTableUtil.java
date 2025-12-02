package io.arkx.data.lightning.plugin.treetable.closure.service;

import org.springframework.data.relational.core.mapping.Table;

import io.arkx.data.lightning.plugin.treetable.closure.entity.BizTableMeta;
import io.arkx.framework.commons.util.StringUtils;
import io.arkx.framework.commons.utils2.StringUtil;
import io.arkx.framework.data.common.entity.IdType;
import io.arkx.framework.data.common.entity.LongId;
import io.arkx.framework.data.common.entity.TreeEntity;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-09-07 18:26
 * @since 1.0
 */
public class TreeTableUtil {

    public static BizTableMeta findBizTableMeta(Class<? extends TreeEntity> clazz) {
        // 2. 闭包表关系插入（由闭包服务完成）
        BizTableMeta meta = new BizTableMeta();

        IdType idType = LongId.class.isAssignableFrom(clazz) ? IdType.LONG : IdType.STRING;
        if (clazz.isAnnotationPresent(Table.class)) {
            Table treeTable = clazz.getAnnotation(Table.class);
            String bizTable = treeTable.name();
            if (StringUtils.isEmpty(bizTable)) {
                bizTable = treeTable.value();
            }
            if (StringUtils.isEmpty(bizTable)) {
                bizTable = StringUtil.camelToUnderline(clazz.getSimpleName());
            }
            meta.setBizTable(bizTable);
            meta.setUseIndependent(false);
            meta.setIdType(idType);
        }

        return meta;
    }

}
