// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.core.provider;

import java.util.Objects;

import javax.sql.DataSource;

import io.arkx.framework.data.db.common.type.ProductTypeEnum;
import io.arkx.framework.data.db.common.util.ExamineUtils;
import io.arkx.framework.data.db.core.annotation.Product;

public abstract class AbstractFactoryProvider implements ProductFactoryProvider {

	private DataSource dataSource;

	protected AbstractFactoryProvider(DataSource dataSource) {
		ExamineUtils.checkNotNull(dataSource, "datasource");
		this.dataSource = dataSource;
	}

	@Override
	public DataSource getDataSource() {
		return this.dataSource;
	}

	@Override
	public final ProductTypeEnum getProductType() {
		Product annotation = getClass().getAnnotation(Product.class);
		ExamineUtils.checkState(Objects.nonNull(annotation), "Should use Product annotation for class : %s",
				getClass().getName());
		return annotation.value();
	}

}
