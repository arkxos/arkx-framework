// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.sqlserver;

import javax.sql.DataSource;

import io.arkx.framework.data.db.common.type.ProductTypeEnum;
import io.arkx.framework.data.db.core.annotation.Product;
import io.arkx.framework.data.db.core.features.DefaultProductFeatures;
import io.arkx.framework.data.db.core.features.ProductFeatures;
import io.arkx.framework.data.db.core.provider.AbstractFactoryProvider;
import io.arkx.framework.data.db.core.provider.manage.DefaultTableManageProvider;
import io.arkx.framework.data.db.core.provider.manage.TableManageProvider;
import io.arkx.framework.data.db.core.provider.meta.MetadataProvider;
import io.arkx.framework.data.db.core.provider.sync.AutoCastTableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.sync.TableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.write.AutoCastTableDataWriteProvider;
import io.arkx.framework.data.db.core.provider.write.TableDataWriteProvider;

@Product(ProductTypeEnum.SQLSERVER)
public class SqlserverFactoryProvider extends AbstractFactoryProvider {

	public SqlserverFactoryProvider(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public ProductFeatures getProductFeatures() {
		return new DefaultProductFeatures();
	}

	@Override
	public MetadataProvider createMetadataQueryProvider() {
		return new SqlserverMetadataQueryProvider(this);
	}

	@Override
	public TableDataWriteProvider createTableDataWriteProvider(boolean useInsert) {
		return new AutoCastTableDataWriteProvider(this);
	}

	@Override
	public TableManageProvider createTableManageProvider() {
		return new DefaultTableManageProvider(this);
	}

	@Override
	public TableDataSynchronizeProvider createTableDataSynchronizeProvider() {
		return new AutoCastTableDataSynchronizeProvider(this);
	}

}
