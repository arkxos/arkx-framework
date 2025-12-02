// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.sr;

import javax.sql.DataSource;

import io.arkx.framework.data.db.common.type.ProductTypeEnum;
import io.arkx.framework.data.db.core.annotation.Product;
import io.arkx.framework.data.db.core.features.ProductFeatures;
import io.arkx.framework.data.db.core.provider.AbstractFactoryProvider;
import io.arkx.framework.data.db.core.provider.meta.MetadataProvider;
import io.arkx.framework.data.db.core.provider.sync.TableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.write.TableDataWriteProvider;

@Product(ProductTypeEnum.STARROCKS)
public class StarrocksFactoryProvider extends AbstractFactoryProvider {

    public StarrocksFactoryProvider(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ProductFeatures getProductFeatures() {
        return new StarrocksFeatures();
    }

    @Override
    public MetadataProvider createMetadataQueryProvider() {
        return new StarrocksMetadataQueryProvider(this);
    }

    @Override
    public TableDataWriteProvider createTableDataWriteProvider(boolean useInsert) {
        return new StarrocksTableDataWriteProvider(this);
    }

    @Override
    public TableDataSynchronizeProvider createTableDataSynchronizeProvider() {
        return new StarrocksTableDataSynchronizer(this);
    }

}
