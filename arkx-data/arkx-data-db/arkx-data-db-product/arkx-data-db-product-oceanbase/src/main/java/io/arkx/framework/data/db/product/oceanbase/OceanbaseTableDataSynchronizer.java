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
import io.arkx.framework.data.db.product.oracle.OracleTableDataSynchronizer;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.sync.DefaultTableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.sync.TableDataSynchronizeProvider;
import java.util.List;

public class OceanbaseTableDataSynchronizer extends DefaultTableDataSynchronizeProvider {

  private final TableDataSynchronizeProvider delegate;
  private final ProductTypeEnum dialect;

  public OceanbaseTableDataSynchronizer(ProductFactoryProvider factoryProvider, TableDataSynchronizeProvider delegate) {
    super(factoryProvider);
    this.delegate = delegate;
    if (delegate instanceof OracleTableDataSynchronizer) {
      this.dialect = ProductTypeEnum.ORACLE;
    } else {
      this.dialect = ProductTypeEnum.MYSQL;
    }
  }

  @Override
  protected String quoteName(String name) {
    return this.dialect.quoteName(name);
  }

  @Override
  public String quoteSchemaTableName(String schemaName, String tableName) {
    return this.dialect.quoteSchemaTableName(schemaName, tableName);
  }

  @Override
  public void prepare(String schemaName, String tableName, List<String> fieldNames, List<String> pks,
                      String dbSyncMode, String slaveDbCode) {
    this.delegate.prepare(schemaName, tableName, fieldNames, pks, dbSyncMode, slaveDbCode);
  }

  @Override
  public long executeInsert(List<Object[]> records) {
    return this.delegate.executeInsert(records);
  }

  @Override
  public long executeUpdate(List<Object[]> records) {
    return this.delegate.executeUpdate(records);
  }

  @Override
  public long executeDelete(List<Object[]> records) {
    return this.delegate.executeDelete(records);
  }
}
