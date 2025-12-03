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

import javax.sql.DataSource;

import io.arkx.framework.data.db.common.type.ProductTypeEnum;
import io.arkx.framework.data.db.core.annotation.Product;
import io.arkx.framework.data.db.core.features.DefaultProductFeatures;
import io.arkx.framework.data.db.core.features.ProductFeatures;
import io.arkx.framework.data.db.core.provider.AbstractFactoryProvider;
import io.arkx.framework.data.db.core.provider.manage.TableManageProvider;
import io.arkx.framework.data.db.core.provider.meta.MetadataProvider;
import io.arkx.framework.data.db.core.provider.sync.AutoCastTableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.sync.TableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.write.AutoCastTableDataWriteProvider;
import io.arkx.framework.data.db.core.provider.write.TableDataWriteProvider;

@Product(ProductTypeEnum.DB2)
public class DB2FactoryProvider extends AbstractFactoryProvider {

    public DB2FactoryProvider(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ProductFeatures getProductFeatures() {
        return new DefaultProductFeatures();
    }

    @Override
    public MetadataProvider createMetadataQueryProvider() {
        return new DB2MetadataQueryProvider(this);
    }

    @Override
    public TableDataWriteProvider createTableDataWriteProvider(boolean useInsert) {
        return new AutoCastTableDataWriteProvider(this);
    }

    @Override
    public TableManageProvider createTableManageProvider() {
        return new DB2TableManageProvider(this);
    }

    @Override
    public TableDataSynchronizeProvider createTableDataSynchronizeProvider() {
        return new AutoCastTableDataSynchronizeProvider(this);
    }

}
