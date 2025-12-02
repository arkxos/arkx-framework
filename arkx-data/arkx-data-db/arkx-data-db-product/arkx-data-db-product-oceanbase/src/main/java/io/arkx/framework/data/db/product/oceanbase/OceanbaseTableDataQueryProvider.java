// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.oceanbase;

import io.arkx.framework.data.db.common.type.ProductTypeEnum;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.query.DefaultTableDataQueryProvider;
import io.arkx.framework.data.db.core.provider.query.TableDataQueryProvider;

public class OceanbaseTableDataQueryProvider extends DefaultTableDataQueryProvider implements TableDataQueryProvider {

	private final ProductTypeEnum dialect;

	public OceanbaseTableDataQueryProvider(ProductFactoryProvider factoryProvider, Boolean isMySqlMode) {
		super(factoryProvider);
		this.dialect = isMySqlMode ? ProductTypeEnum.MYSQL : ProductTypeEnum.ORACLE;
	}

	@Override
	protected String quoteName(String name) {
		return this.dialect.quoteName(name);
	}

}
