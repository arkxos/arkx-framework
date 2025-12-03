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

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.write.AutoCastTableDataWriteProvider;

public class TDengineTableDataWriteProvider extends AutoCastTableDataWriteProvider {

    public TDengineTableDataWriteProvider(ProductFactoryProvider factoryProvider) {
        super(factoryProvider);
    }

    @Override
    protected TransactionDefinition getDefaultTransactionDefinition() {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        return definition;
    }

}
