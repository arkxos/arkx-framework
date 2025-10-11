// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.postgresql;

import io.arkx.framework.data.db.core.provider.meta.MetadataProvider;
import io.arkx.framework.data.db.core.schema.ColumnDescription;
import io.arkx.framework.data.db.core.util.GenerateSqlUtils;
import java.sql.Connection;
import java.util.List;

public final class PostgresUtils {

  public static String getTableDDL(MetadataProvider provider, Connection connection, String schema,
      String table) {
    List<ColumnDescription> columnDescriptions = provider.queryTableColumnMeta(connection, schema, table);
    List<String> pks = provider.queryTablePrimaryKeys(connection, schema, table);
    return GenerateSqlUtils.getDDLCreateTableSQL(
        provider, columnDescriptions, pks, schema, table, false);
  }


  private PostgresUtils() {
    throw new IllegalStateException();
  }

}
