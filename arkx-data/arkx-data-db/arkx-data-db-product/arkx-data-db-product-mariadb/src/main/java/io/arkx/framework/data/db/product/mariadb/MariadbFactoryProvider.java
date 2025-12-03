// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.mariadb;

import javax.sql.DataSource;

import io.arkx.framework.data.db.common.type.ProductTypeEnum;
import io.arkx.framework.data.db.core.annotation.Product;
import io.arkx.framework.data.db.core.features.ProductFeatures;
import io.arkx.framework.data.db.core.provider.AbstractFactoryProvider;
import io.arkx.framework.data.db.core.provider.meta.MetadataProvider;
import io.arkx.framework.data.db.core.provider.sync.AutoCastTableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.sync.TableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.write.AutoCastTableDataWriteProvider;
import io.arkx.framework.data.db.core.provider.write.TableDataWriteProvider;
import io.arkx.framework.data.db.product.mysql.MysqlFeatures;
import io.arkx.framework.data.db.product.mysql.MysqlMetadataQueryProvider;

@Product(ProductTypeEnum.MARIADB)
public class MariadbFactoryProvider extends AbstractFactoryProvider {

    public MariadbFactoryProvider(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ProductFeatures getProductFeatures() {
        return new MysqlFeatures();
    }

    @Override
    public MetadataProvider createMetadataQueryProvider() {
        return new MysqlMetadataQueryProvider(this);
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
