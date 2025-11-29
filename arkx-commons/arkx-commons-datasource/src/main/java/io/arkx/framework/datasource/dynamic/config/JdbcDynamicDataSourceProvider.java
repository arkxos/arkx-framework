/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.arkx.framework.datasource.dynamic.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import io.arkx.framework.datasource.dynamic.enums.DsConfTypeEnum;
import io.arkx.framework.datasource.dynamic.enums.DsJdbcUrlEnum;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;

import io.arkx.framework.datasource.dynamic.support.DataSourceConstants;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;

/**
 * @author lengleng
 * @date 2020/2/6
 * <p>
 * 从数据源中获取 配置信息
 */
@Slf4j
public class JdbcDynamicDataSourceProvider extends AbstractJdbcDataSourceProvider {

	private final DataSourceProperties properties;

	private final StringEncryptor stringEncryptor;

	public JdbcDynamicDataSourceProvider(DefaultDataSourceCreator defaultDataSourceCreator,
			StringEncryptor stringEncryptor, DataSourceProperties properties) {
		super(defaultDataSourceCreator, properties.getDriverClassName(), properties.getUrl(), properties.getUsername(),
				properties.getPassword());
		this.stringEncryptor = stringEncryptor;
		this.properties = properties;
	}

	/**
	 * 执行语句获得数据源参数
	 * @param statement 语句
	 * @return 数据源参数
	 * @throws SQLException sql异常
	 */
	@Override
	protected Map<String, DataSourceProperty> executeStmt(Statement statement) throws SQLException {

		Map<String, DataSourceProperty> map = new HashMap<>(8);

		try {
			ResultSet rs = statement.executeQuery(properties.getQueryDsSql());

			while (rs.next()) {
				String name = rs.getString(DataSourceConstants.NAME);
				String username = rs.getString(DataSourceConstants.DS_USER_NAME);
				String password = rs.getString(DataSourceConstants.DS_USER_PWD);
				Integer confType = rs.getInt(DataSourceConstants.DS_CONFIG_TYPE);
				String dsType = rs.getString(DataSourceConstants.DS_TYPE);

				DataSourceProperty property = new DataSourceProperty();
				property.setUsername(username);
				property.setPassword(stringEncryptor.decrypt(password));

				String url;
				// JDBC 配置形式
				DsJdbcUrlEnum urlEnum = DsJdbcUrlEnum.get(dsType);
				if (DsConfTypeEnum.JDBC.getType().equals(confType)) {
					url = rs.getString(DataSourceConstants.DS_JDBC_URL);
				}
				else {
					String host = rs.getString(DataSourceConstants.DS_HOST);
					String port = rs.getString(DataSourceConstants.DS_PORT);
					String dsName = rs.getString(DataSourceConstants.DS_NAME);
					url = String.format(urlEnum.getUrl(), host, port, dsName);
				}
				property.setUrl(url);
				map.put(name, property);
			}

		} catch (Exception e) {
			log.warn("动态数据源配置表异常:{}", e.getMessage());
		}

		return map;
	}

}
