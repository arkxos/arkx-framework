// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.sqlite;

import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.manage.DefaultTableManageProvider;

public class SqliteTableManageProvider extends DefaultTableManageProvider {

	public SqliteTableManageProvider(ProductFactoryProvider factoryProvider) {
		super(factoryProvider);
	}

	@Override
	public void truncateTableData(String schemaName, String tableName) {
		String sql = "DELETE FROM \"%s\".\"%s\" ".formatted(schemaName, tableName);
		this.executeSql(sql);

		try {
			sql = "DELETE FROM sqlite_sequence WHERE name = '%s' ".formatted(tableName);
			this.executeSql(sql);
		}
		catch (Exception e) {
			// ignore
		}

	}

	@Override
	public void dropTable(String schemaName, String tableName) {
		String sql = "DROP TABLE \"%s\".\"%s\" ".formatted(schemaName, tableName);
		this.executeSql(sql);
	}

}
