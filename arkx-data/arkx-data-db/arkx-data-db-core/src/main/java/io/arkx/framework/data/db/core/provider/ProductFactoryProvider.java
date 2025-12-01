// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.core.provider;

import io.arkx.framework.data.db.common.type.ProductTypeEnum;
import io.arkx.framework.data.db.core.features.ProductFeatures;
import io.arkx.framework.data.db.core.provider.manage.DefaultTableManageProvider;
import io.arkx.framework.data.db.core.provider.manage.TableManageProvider;
import io.arkx.framework.data.db.core.provider.meta.MetadataProvider;
import io.arkx.framework.data.db.core.provider.query.DefaultTableDataQueryProvider;
import io.arkx.framework.data.db.core.provider.query.TableDataQueryProvider;
import io.arkx.framework.data.db.core.provider.sync.DefaultTableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.sync.TableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.transform.MappedTransformProvider;
import io.arkx.framework.data.db.core.provider.transform.RecordTransformProvider;
import io.arkx.framework.data.db.core.provider.write.DefaultTableDataWriteProvider;
import io.arkx.framework.data.db.core.provider.write.TableDataWriteProvider;

import javax.sql.DataSource;

public interface ProductFactoryProvider {

  /**
   * 获取数据库类型
   *
   * @return ProductTypeEnum
   */
  ProductTypeEnum getProductType();

  /**
   * 获取数据源
   *
   * @return DataSource
   */
  DataSource getDataSource();

  /**
   * 获取数据库特征
   *
   * @return ProductFeatures
   */
  ProductFeatures getProductFeatures();

  /**
   * 获取元数据查询Provider
   *
   * @return MetadataQueryProvider
   */
  MetadataProvider createMetadataQueryProvider();

  /**
   * 获取表数据查询Provider
   *
   * @return TableDataQueryProvider
   */
  default TableDataQueryProvider createTableDataQueryProvider() {
    return new DefaultTableDataQueryProvider(this);
  }

  /**
   * 获取记录转换Provider
   *
   * @return RecordTransformProvider
   */
  default RecordTransformProvider createRecordTransformProvider() {
    return new MappedTransformProvider(this);
  }

  /**
   * 获取表批量写入Provider
   *
   * @param useInsert 是否使用insert写入(只对PG有效)
   * @return TableWriteProvider
   */
  default TableDataWriteProvider createTableDataWriteProvider(boolean useInsert) {
    return new DefaultTableDataWriteProvider(this);
  }

  /**
   * 获取表操作Provider
   *
   * @return TableManageProvider
   */
  default TableManageProvider createTableManageProvider() {
    return new DefaultTableManageProvider(this);
  }

  /**
   * 获取数据同步Provider
   *
   * @return TableDataSynchronizeProvider
   */
  default TableDataSynchronizeProvider createTableDataSynchronizeProvider() {
    return new DefaultTableDataSynchronizeProvider(this);
  }

}
