// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: Li ZeMin (2413957313@qq.com)
// Date : 2024/12/16
// Location: nanjing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.tdengine;

import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.manage.DefaultTableManageProvider;

public class TdengineTableManageProvider extends DefaultTableManageProvider {

  public TdengineTableManageProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public void truncateTableData(String schemaName, String tableName) {
    String sql = "DELETE FROM %s.%s ".formatted(
        schemaName, tableName);
    this.executeSql(sql);
  }

  @Override
  public void dropTable(String schemaName, String tableName) {
    String sql = "DROP TABLE %s.%s ".formatted(
        schemaName, tableName);
    this.executeSql(sql);
  }

}
