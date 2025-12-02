// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.hive;

import java.sql.Connection;
import java.sql.SQLException;

import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.query.DefaultTableDataQueryProvider;

public class HiveTableDataQueryProvider extends DefaultTableDataQueryProvider {

	public HiveTableDataQueryProvider(ProductFactoryProvider factoryProvider) {
		super(factoryProvider);
	}

	@Override
	protected void beforeExecuteQuery(Connection connection, String schema, String table) {
		try {
			HivePrepareUtils.prepare(connection, schema, table);
		}
		catch (SQLException t) {
			throw new RuntimeException(t);
		}
	}

}
