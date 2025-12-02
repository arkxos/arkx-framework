// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/// //////////////////////////////////////////////////////////
package io.arkx.framework.data.db.core.schema;

import io.arkx.framework.data.db.common.type.ProductTableEnum;

import lombok.Data;

/**
 * 数据库表描述符信息定义(Table Description)
 *
 * @author tang
 */
@Data
public class TableDescription {

	private String tableName;

	private String schemaName;

	private String remarks;

	private Boolean isMapping = false;

	private ProductTableEnum tableType;

	public String getTableTypeName() {
		return tableType.name();
	}

	public void setTableType(String tableType) {
		if ("PARTITIONED TABLE".equals(tableType)) {
			tableType = "TABLE";
		}
		this.tableType = ProductTableEnum.valueOf(tableType.toUpperCase());
	}

	public boolean isViewTable() {
		return ProductTableEnum.VIEW == tableType;
	}

}
