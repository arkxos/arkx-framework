package org.ark.framework.orm.sync.source;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:30
 * @since 1.0
 */

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.ark.framework.orm.Schema;
import org.ark.framework.orm.sync.SyncException;
import org.ark.framework.orm.sync.TableSyncConfig;

import io.arkx.framework.data.db.connection.ConnectionConfig;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据库数据源 从数据库中获取增量数据
 */
@Slf4j
public class DatabaseDataSource implements DataSource {

	@Getter
	@Setter
	private ConnectionConfig connectionConfig;

	private final String dbName;

	private Session session;

	/**
	 * 构造函数
	 * @param dbName 数据库名称
	 */
	public DatabaseDataSource(String dbName) {
		this.dbName = dbName;
	}

	@Override
	public String getName() {
		return dbName;
	}

	@Override
	public void initialize() {
		try {
			this.session = SessionFactory.openSessionInThread(dbName);
			log.info("数据库数据源 [{}] 初始化成功", dbName);
		}
		catch (Exception e) {
			log.error("初始化数据库数据源 [{}] 失败", dbName, e);
			throw new SyncException("初始化数据库数据源失败", e);
		}
	}

	@Override
	public List<Schema> fetchIncrementalData(String tableCode, TableSyncConfig config, Session session,
			Timestamp lastSyncTime) {
		log.debug("从数据库 [{}] 获取表 [{}] 的增量数据，时间戳字段: {}, 上次同步时间: {}", dbName, tableCode, config.getTimestampField());

		try {
			if (this.session == null) {
				initialize();
			}

			// 构建增量数据查询SQL
			String query = String.format("SELECT * FROM %s WHERE %s > ? ORDER BY %s", tableCode,
					config.getTimestampField(), config.getTimestampField());

			// 执行查询
			Query queryObject = this.session.createQuery(query);

			// TODO: 将查询结果转换为Schema对象的列表
			// 这里是一个示例实现，实际实现需要根据项目中Schema的具体结构来做
			// 可能需要使用SchemaFactory或其他机制来创建Schema对象

			// 示例代码，实际需要用户自己实现这部分逻辑
			return new ArrayList<>();

		}
		catch (Exception e) {
			log.error("从数据库 [{}] 获取表 [{}] 的增量数据时发生错误", dbName, tableCode, e);
			throw new SyncException("获取增量数据失败", e);
		}
	}

	@Override
	public boolean containsTable(String tableCode) {
		try {
			if (session == null) {
				initialize();
			}

			// 检查表是否存在
			// 这里使用简单的查询，实际应根据不同数据库类型使用合适的方式
			String checkQuery = "SELECT 1 FROM " + tableCode + " WHERE 1=0";
			session.createQuery(checkQuery).executeDataTable();
			return true;
		}
		catch (Exception e) {
			log.debug("表 [{}] 在数据库 [{}] 中不存在", tableCode, dbName);
			return false;
		}
	}

	@Override
	public void close() {
		if (session != null) {
			try {
				session.close();
				log.debug("数据库数据源 [{}] 已关闭", dbName);
			}
			catch (Exception e) {
				log.warn("关闭数据库数据源 [{}] 时发生错误", dbName, e);
			}
			finally {
				session = null;
			}
		}
	}

}
