// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.oracle;

import javax.sql.DataSource;

import io.arkx.framework.data.db.common.type.ProductTypeEnum;
import io.arkx.framework.data.db.core.annotation.Product;
import io.arkx.framework.data.db.core.features.DefaultProductFeatures;
import io.arkx.framework.data.db.core.features.ProductFeatures;
import io.arkx.framework.data.db.core.provider.AbstractFactoryProvider;
import io.arkx.framework.data.db.core.provider.manage.TableManageProvider;
import io.arkx.framework.data.db.core.provider.meta.MetadataProvider;
import io.arkx.framework.data.db.core.provider.query.TableDataQueryProvider;
import io.arkx.framework.data.db.core.provider.sync.TableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.write.TableDataWriteProvider;

@Product(ProductTypeEnum.ORACLE)
public class OracleFactoryProvider extends AbstractFactoryProvider {

    public OracleFactoryProvider(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ProductFeatures getProductFeatures() {
        return new DefaultProductFeatures();
    }

    @Override
    public MetadataProvider createMetadataQueryProvider() {
        return new OracleMetadataQueryProvider(this);
    }

    @Override
    public TableDataQueryProvider createTableDataQueryProvider() {
        return new OracleTableDataQueryProvider(this);
    }

    @Override
    public TableDataWriteProvider createTableDataWriteProvider(boolean useInsert) {
        return new OracleTableDataWriteProvider(this);
    }

    @Override
    public TableManageProvider createTableManageProvider() {
        return new OracleTableManageProvider(this);
    }

    @Override
    public TableDataSynchronizeProvider createTableDataSynchronizeProvider() {
        return new OracleTableDataSynchronizer(this);
    }

}
