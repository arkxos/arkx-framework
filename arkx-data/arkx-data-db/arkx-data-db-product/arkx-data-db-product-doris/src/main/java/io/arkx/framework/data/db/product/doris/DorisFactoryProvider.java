// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: wjk (wanglv110@163.com)
// Date : 2024/9/29
// Location: wuhan , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.doris;

import io.arkx.framework.data.db.core.annotation.Product;
import io.arkx.framework.data.db.common.type.ProductTypeEnum;
import io.arkx.framework.data.db.core.features.ProductFeatures;
import io.arkx.framework.data.db.core.provider.AbstractFactoryProvider;
import io.arkx.framework.data.db.core.provider.meta.MetadataProvider;
import io.arkx.framework.data.db.core.provider.sync.AutoCastTableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.sync.TableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.write.AutoCastTableDataWriteProvider;
import io.arkx.framework.data.db.core.provider.write.TableDataWriteProvider;
import javax.sql.DataSource;

@Product(ProductTypeEnum.DORIS)
public class DorisFactoryProvider extends AbstractFactoryProvider {

  public DorisFactoryProvider(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  public ProductFeatures getProductFeatures() {
    return new DorisFeatures();
  }

  @Override
  public MetadataProvider createMetadataQueryProvider() {
    return new DorisMetadataQueryProvider(this);
  }

  @Override
  public TableDataWriteProvider createTableDataWriteProvider(boolean useInsert) {
    return new AutoCastTableDataWriteProvider(this);
  }

  @Override
  public TableDataSynchronizeProvider createTableDataSynchronizeProvider() {
    return new AutoCastTableDataSynchronizeProvider(this);
  }

}
