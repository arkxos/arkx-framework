package io.arkx.data.lightning.plugin.treetable.closure.service;

import io.arkx.data.lightning.annotation.TreeTable;
import io.arkx.data.lightning.plugin.treetable.closure.entity.BizTableMeta;
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

		IdType idType =  LongId.class.isAssignableFrom(clazz) ? IdType.LONG : IdType.STRING;
		if (clazz.isAnnotationPresent(TreeTable.class)) {
			TreeTable treeTable = clazz.getAnnotation(TreeTable.class);
			String bizTable = treeTable.businessTableName();

			meta.setBizTable(bizTable);
			meta.setUseIndependent(false);
			meta.setIdType(idType);
		}

		return meta;
	}

}
