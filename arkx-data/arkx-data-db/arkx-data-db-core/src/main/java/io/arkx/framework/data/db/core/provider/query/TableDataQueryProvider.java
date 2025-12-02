// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.core.provider.query;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.arkx.framework.data.db.common.entity.IncrementPoint;
import io.arkx.framework.data.db.common.entity.ResultSetWrapper;
import io.arkx.framework.data.db.common.type.ProductTypeEnum;
import io.arkx.framework.data.db.core.schema.ColumnValue;
import io.arkx.framework.data.db.core.schema.SchemaTableData;

/**
 * 表数据查询
 */
public interface TableDataQueryProvider {

	/**
	 * 获取数据库类型
	 * @return ProductTypeEnum
	 */
	ProductTypeEnum getProductType();

	String quoteSchemaTableName(String schemaName, String tableName);

	/**
	 * 获取读取(fetch)数据的批次大小
	 * @return 批次大小
	 */
	int getQueryFetchSize();

	/**
	 * 设置读取(fetch)数据的批次大小
	 * @param size 批次大小
	 */
	void setQueryFetchSize(int size);

	/**
	 * 获取指定schema下表的结果集
	 * @param schemaName 模式名称
	 * @param tableName 表名称
	 * @param fields 字段列表
	 * @return 结果集包装对象
	 */
	default ResultSetWrapper queryTableData(String schemaName, String tableName, List<String> fields) {
		return queryTableData(schemaName, tableName, "", fields, IncrementPoint.EMPTY, Collections.emptyList());
	}

	default ResultSetWrapper queryTableData(String schemaName, String tableName, String shardDbCode,
			List<String> fields) {
		return queryTableData(schemaName, tableName, shardDbCode, fields, IncrementPoint.EMPTY,
				Collections.emptyList());
	}

	/**
	 * 获取指定schema下表的结果集
	 * @param schemaName 模式名称
	 * @param tableName 表名称
	 * @param fields 字段列表
	 * @param point 增量点
	 * @return 结果集包装对象
	 */
	default ResultSetWrapper queryTableData(String schemaName, String tableName, String shardDbCode,
			List<String> fields, IncrementPoint point) {
		return queryTableData(schemaName, tableName, shardDbCode, fields, point, Collections.emptyList());
	}

	/**
	 * 获取指定schema下表的按主键有序的结果集
	 * @param schemaName 模式名称
	 * @param tableName 表名称
	 * @param fields 字段列表
	 * @param orders 排序字段列表
	 * @return 结果集包装对象
	 */
	default ResultSetWrapper queryTableData(String schemaName, String tableName, String shardDbCode,
			List<String> fields, List<String> orders) {
		return queryTableData(schemaName, tableName, shardDbCode, fields, IncrementPoint.EMPTY, orders);
	}

	/**
	 * 获取指定schema下表的按主键有序的结果集
	 * @param schemaName 模式名称
	 * @param tableName 表名称
	 * @param fields 字段列表
	 * @param point 增量点
	 * @param orders 排序字段列表
	 * @return 结果集包装对象
	 */
	ResultSetWrapper queryTableData(String schemaName, String tableName, String shardDbCode, List<String> fields,
			IncrementPoint point, List<String> orders);

	/**
	 * 获取指定模式表内的数据
	 * @param connection JDBC连接
	 * @param schemaName 模式名称
	 * @param tableName 表名称
	 * @param rowCount 记录的行数
	 * @return 数据内容
	 */
	SchemaTableData queryTableData(Connection connection, String schemaName, String tableName, int rowCount);

	List<Map<String, Object>> executeQueryBySql(Connection connection, String schema, String sql);

	int executeSql(Connection connection, String schema, String sql);

	/**
	 * 查询增量字段数据的最大值
	 * @param connection JDBC连接
	 * @param schemaName 模式名称
	 * @param tableName 表名称
	 * @param filedName 字段名称
	 * @return 最大值(可能为null)
	 */
	default ColumnValue queryFieldMaxValue(Connection connection, String schemaName, String tableName,
			String filedName) {
		return null;
	}

}
