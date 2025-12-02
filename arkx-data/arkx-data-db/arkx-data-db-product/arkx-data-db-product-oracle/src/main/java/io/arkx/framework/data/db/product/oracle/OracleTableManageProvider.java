// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.oracle;

import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.manage.DefaultTableManageProvider;

public class OracleTableManageProvider extends DefaultTableManageProvider {

	public OracleTableManageProvider(ProductFactoryProvider factoryProvider) {
		super(factoryProvider);
	}

	@Override
	public void dropTable(String schemaName, String tableName) {
		String sql = "DROP TABLE \"%s\".\"%s\" CASCADE CONSTRAINTS".formatted(schemaName, tableName);
		this.executeSql(sql);
	}

}
