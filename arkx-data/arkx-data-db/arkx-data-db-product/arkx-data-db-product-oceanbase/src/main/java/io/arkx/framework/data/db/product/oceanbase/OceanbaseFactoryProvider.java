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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import io.arkx.framework.data.db.common.type.ProductTypeEnum;
import io.arkx.framework.data.db.core.annotation.Product;
import io.arkx.framework.data.db.core.features.DefaultProductFeatures;
import io.arkx.framework.data.db.core.features.ProductFeatures;
import io.arkx.framework.data.db.core.provider.AbstractFactoryProvider;
import io.arkx.framework.data.db.core.provider.manage.DefaultTableManageProvider;
import io.arkx.framework.data.db.core.provider.manage.TableManageProvider;
import io.arkx.framework.data.db.core.provider.meta.MetadataProvider;
import io.arkx.framework.data.db.core.provider.query.TableDataQueryProvider;
import io.arkx.framework.data.db.core.provider.sync.AutoCastTableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.sync.TableDataSynchronizeProvider;
import io.arkx.framework.data.db.core.provider.write.AutoCastTableDataWriteProvider;
import io.arkx.framework.data.db.core.provider.write.TableDataWriteProvider;
import io.arkx.framework.data.db.product.mysql.MysqlFeatures;
import io.arkx.framework.data.db.product.mysql.MysqlMetadataQueryProvider;
import io.arkx.framework.data.db.product.oracle.OracleMetadataQueryProvider;
import io.arkx.framework.data.db.product.oracle.OracleTableDataSynchronizer;
import io.arkx.framework.data.db.product.oracle.OracleTableDataWriteProvider;
import io.arkx.framework.data.db.product.oracle.OracleTableManageProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Product(ProductTypeEnum.OCEANBASE)
public class OceanbaseFactoryProvider extends AbstractFactoryProvider {

    private Boolean isMySqlMode;

    public OceanbaseFactoryProvider(DataSource dataSource) {
        super(dataSource);

        try (Connection connection = getDataSource().getConnection()) {
            this.isMySqlMode = OceanbaseUtils.isOceanBaseUseMysqlMode(connection);
            if (log.isDebugEnabled()) {
                log.debug("#### Target OceanBase is {} Mode ", this.isMySqlMode ? "MySQL" : "Oracle");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductFeatures getProductFeatures() {
        return isMySqlMode ? new MysqlFeatures() : new DefaultProductFeatures();
    }

    @Override
    public MetadataProvider createMetadataQueryProvider() {
        MetadataProvider provider = isMySqlMode
                ? new MysqlMetadataQueryProvider(this)
                : new OracleMetadataQueryProvider(this);
        return new OceanbaseMetadataQueryProvider(this, provider);
    }

    @Override
    public TableDataQueryProvider createTableDataQueryProvider() {
        return new OceanbaseTableDataQueryProvider(this, isMySqlMode);
    }

    @Override
    public TableDataWriteProvider createTableDataWriteProvider(boolean useInsert) {
        TableDataWriteProvider provider = isMySqlMode
                ? new AutoCastTableDataWriteProvider(this)
                : new OracleTableDataWriteProvider(this);
        return new OceanbaseTableDataWriteProvider(this, provider);
    }

    @Override
    public TableManageProvider createTableManageProvider() {
        TableManageProvider provider = isMySqlMode
                ? new DefaultTableManageProvider(this)
                : new OracleTableManageProvider(this);
        return new OceanbaseTableManageProvider(this, provider);
    }

    @Override
    public TableDataSynchronizeProvider createTableDataSynchronizeProvider() {
        TableDataSynchronizeProvider provider = isMySqlMode
                ? new AutoCastTableDataSynchronizeProvider(this)
                : new OracleTableDataSynchronizer(this);
        return new OceanbaseTableDataSynchronizer(this, provider);
    }
}
