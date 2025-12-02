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

import java.sql.Types;

import io.arkx.framework.data.db.common.entity.IncrementPoint;
import io.arkx.framework.data.db.common.util.JdbcTypesUtils;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.query.DefaultTableDataQueryProvider;

public class OracleTableDataQueryProvider extends DefaultTableDataQueryProvider {

	private static final String TIMESTAMP_PATTERN = "yyyy-mm-dd hh24:mi:ss.ff";

	private static final String DATE_PATTERN = "yyyy-mm-dd hh24:mi:ss";

	public OracleTableDataQueryProvider(ProductFactoryProvider factoryProvider) {
		super(factoryProvider);
	}

	@Override
	protected String toGreaterThanCondition(IncrementPoint point) {
		StringBuilder sb = new StringBuilder();
		sb.append(quoteName(point.getColumnName()));
		sb.append(" > ");
		if (JdbcTypesUtils.isInteger(point.getJdbcType())) {
			sb.append(point.getMaxValue());
		}
		else if (JdbcTypesUtils.isDateTime(point.getJdbcType())) {
			if (Types.TIMESTAMP == point.getJdbcType() || Types.TIMESTAMP_WITH_TIMEZONE == point.getJdbcType()) {
				sb.append("TO_TIMESTAMP('%s', '%s')".formatted(point.getMaxValue(), TIMESTAMP_PATTERN));
			}
			else {
				sb.append("TO_DATE('%s', '%s')".formatted(point.getMaxValue(), DATE_PATTERN));
			}
		}
		return sb.toString();
	}

}
