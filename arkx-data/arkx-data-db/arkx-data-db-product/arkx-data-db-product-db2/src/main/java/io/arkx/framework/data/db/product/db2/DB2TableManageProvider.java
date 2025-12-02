// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.db2;

import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.manage.DefaultTableManageProvider;

public class DB2TableManageProvider extends DefaultTableManageProvider {

    public DB2TableManageProvider(ProductFactoryProvider factoryProvider) {
        super(factoryProvider);
    }

    @Override
    public void truncateTableData(String schemaName, String tableName) {
        String sql = "TRUNCATE TABLE \"%s\".\"%s\" IMMEDIATE ".formatted(schemaName, tableName);
        this.executeSql(sql);
    }

}
