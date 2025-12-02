// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.elasticsearch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.meta.AbstractMetadataProvider;
import io.arkx.framework.data.db.core.schema.ColumnDescription;
import io.arkx.framework.data.db.core.schema.ColumnMetaData;
import io.arkx.framework.data.db.core.schema.IndexDescription;
import io.arkx.framework.data.db.core.schema.TableDescription;

public class ElasticsearchMetadataQueryProvider extends AbstractMetadataProvider {

	protected ElasticsearchMetadataQueryProvider(ProductFactoryProvider factoryProvider) {
		super(factoryProvider);
	}

	@Override
	public String getTableDDL(Connection connection, String schemaName, String tableName) {
		return null;
	}

	@Override
	public String getViewDDL(Connection connection, String schemaName, String tableName) {
		return null;
	}

	@Override
	public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
		return Collections.emptyList();
	}

	@Override
	public List<ColumnDescription> queryTableColumnMeta(Connection connection, String schemaName, String tableName) {
		List<ColumnDescription> ret = new ArrayList<>();
		try (ResultSet rs = connection.getMetaData().getColumns(null, schemaName, tableName, null);) {
			while (rs.next()) {
				ColumnDescription cd = new ColumnDescription();
				cd.setFieldName(rs.getString("COLUMN_NAME"));
				cd.setLabelName(rs.getString("COLUMN_NAME"));
				cd.setFieldType(Integer.parseInt(rs.getString("DATA_TYPE")));
				cd.setFieldTypeName(rs.getString("TYPE_NAME"));
				cd.setFiledTypeClassName(rs.getString("TYPE_NAME"));
				cd.setDisplaySize(Integer.parseInt(rs.getString("COLUMN_SIZE")));
				cd.setPrecisionSize(0);
				cd.setScaleSize(0);
				cd.setAutoIncrement(false);
				cd.setNullable(true);
				cd.setProductType(getProductType());
				ret.add(cd);
			}
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public void testQuerySQL(Connection connection, String sql) {
		try {
			List<String> schemas = querySchemaList(connection);
			connection.getMetaData().getTables(null, schemas.getFirst(), null, null);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> queryTablePrimaryKeys(Connection connection, String schemaName, String tableName) {
		return Collections.emptyList();
	}

	@Override
	public List<IndexDescription> queryTableIndexes(Connection connection, String schemaName, String tableName) {
		return Collections.emptyList();
	}

	@Override
	public String getQuotedSchemaTableCombination(String schemaName, String tableName) {
		return "%s.%s".formatted(schemaName, tableName);
	}

	@Override
	public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc, boolean addCr,
			boolean withRemarks) {
		return null;
	}

	@Override
	public String getPrimaryKeyAsString(List<String> pks) {
		return null;
	}

	@Override
	public List<String> getTableColumnCommentDefinition(TableDescription td, List<ColumnDescription> cds) {
		return Collections.emptyList();
	}

}
