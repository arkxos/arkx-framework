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

import io.arkx.framework.data.db.core.annotation.Product;
import io.arkx.framework.data.db.common.type.ProductTypeEnum;
import io.arkx.framework.data.db.core.features.ProductFeatures;
import io.arkx.framework.data.db.core.provider.AbstractFactoryProvider;
import io.arkx.framework.data.db.core.provider.meta.MetadataProvider;
import io.arkx.framework.data.db.core.provider.query.TableDataQueryProvider;
import javax.sql.DataSource;

@Product(ProductTypeEnum.HIVE)
public class HiveFactoryProvider extends AbstractFactoryProvider {

  public HiveFactoryProvider(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  public ProductFeatures getProductFeatures() {
    return new HiveFeatures();
  }

  @Override
  public MetadataProvider createMetadataQueryProvider() {
    return new HiveMetadataQueryProvider(this);
  }

  @Override
  public TableDataQueryProvider createTableDataQueryProvider() {
    return new HiveTableDataQueryProvider(this);
  }

//  @Override
//  public TableManageProvider createTableManageProvider() {
//    throw new UnsupportedOperationException("Unsupported hive to manage tale!");
//  }
//
//  @Override
//  public TableDataWriteProvider createTableDataWriteProvider(boolean useInsert) {
//    throw new UnsupportedOperationException("Unsupported hive to write tale data!");
//  }
//
//  @Override
//  public TableDataSynchronizeProvider createTableDataSynchronizeProvider() {
//    throw new UnsupportedOperationException("Unsupported hive to sync tale data!");
//  }
}
