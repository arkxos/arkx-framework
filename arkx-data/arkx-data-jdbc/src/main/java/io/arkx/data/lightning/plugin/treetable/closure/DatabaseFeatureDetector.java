package io.arkx.data.lightning.plugin.treetable.closure;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

/**
 * @author Nobody
 * @date 2025-07-28 1:59
 * @since 1.0
 */
@Component
public class DatabaseFeatureDetector {

	private final DataSource dataSource;

	private String databaseType;

	public DatabaseFeatureDetector(DataSource dataSource) {
		this.dataSource = dataSource;
		detectDatabaseType();
	}

	private void detectDatabaseType() {
		try (Connection conn = dataSource.getConnection()) {
			DatabaseMetaData metaData = conn.getMetaData();
			this.databaseType = metaData.getDatabaseProductName();
		}
		catch (SQLException e) {
			throw new RuntimeException("检测数据库类型失败", e);
		}
	}

	public String getDatabaseType() {
		return databaseType;
	}

}
