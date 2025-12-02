package org.ark.framework.orm.sync.source;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:31
 * @since 1.0
 */

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

import org.ark.framework.orm.Schema;
import org.ark.framework.orm.SchemaSet;
import org.ark.framework.orm.SchemaUtil;
import org.ark.framework.orm.sync.SyncException;
import org.ark.framework.orm.sync.TableSyncConfig;
import org.ark.framework.orm.sync.util.ExceptionUtil;

import io.arkx.framework.data.jdbc.Session;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * SchemaSet数据源 从预加载的SchemaSet对象获取增量数据
 */
@Slf4j
public class SchemaSetDataSource implements DataSource {

	private final String name;

	private final Map<String, SchemaSet<?>> schemaSets;

	/**
	 * 构造函数
	 * @param name 数据源名称
	 */
	public SchemaSetDataSource(String name) {
		this.name = name;
		this.schemaSets = new HashMap<>();
	}

	/**
	 * 添加SchemaSet
	 * @param tableCode 表代码
	 * @param schemaSet SchemaSet对象
	 */
	public void addSchemaSet(String tableCode, SchemaSet<?> schemaSet) {
		if (schemaSet == null) {
			log.warn("尝试添加空的 SchemaSet 到表 [{}]", tableCode);
			return;
		}

		// 如果已有记录，则追加，否则直接放入
		if (schemaSets.containsKey(tableCode)) {
			SchemaSet<?> existingSet = schemaSets.get(tableCode);
			existingSet.addAllWildcard(schemaSet);
			log.info("已追加 SchemaSet 到表 [{}]，追加 {} 条记录，当前总数 {}", tableCode, schemaSet.size(), existingSet.size());
		}
		else {
			schemaSets.put(tableCode, schemaSet);
			log.info("已添加 SchemaSet 到表 [{}]，包含 {} 条记录", tableCode, schemaSet.size());
		}
	}

	/**
	 * 清除所有SchemaSet
	 */
	public void clearSchemaSets() {
		schemaSets.clear();
		log.info("已清除所有SchemaSet");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void initialize() {
		// SchemaSet数据源不需要特殊初始化
		log.info("SchemaSet数据源 [{}] 初始化成功", name);
	}

	@Override
	public List<Schema> fetchIncrementalData(String tableCode, TableSyncConfig config, Session session,
			Timestamp lastSyncTime) {
		// log.debug("从SchemaSet数据源 [{}] 获取表 [{}] 的增量数据，时间戳字段: {}, 上次同步时间: {}",
		// name, tableCode, config.getTimestampField());

		try {
			SchemaSet<?> schemaSet = schemaSets.get(tableCode);
			if (schemaSet == null) {
				log.warn("表 [{}] 在SchemaSet数据源中不存在", tableCode);
				return new ArrayList<>();
			}

			Schema schema1 = schemaSet.getSchema();
			String nameSpace = SchemaUtil.getNameSpace(schema1);
			session.beginTransaction();
			schema1.setSession(session);
			schema1.setExternallyManagedTransaction(true);

			Class<?> aClass = Thread.currentThread()
				.getContextClassLoader()
				.loadClass(nameSpace + "." + tableCode + "Schema");

			// 这里假设必须是 Schema 的子类（Schema 是接口或父类）
			if (Schema.class.isAssignableFrom(aClass)) {
				Schema.registerSchema(tableCode, (Class<? extends Schema>) aClass);
			}

			List<Schema> incrementalData = new ArrayList<>();
			String timestampField = config.getTimestampField();

			// 遍历SchemaSet，根据时间戳字段过滤
			for (int i = 0; i < schemaSet.size(); i++) {
				Schema schema = schemaSet.getObject(i);

				// 使用反射获取时间戳字段值
				Date updateTime = getTimestampFieldValue(schema, "UPDATE_TIME");
				Date createTime = getTimestampFieldValue(schema, "CREATE_TIME");
				Date customTime = getTimestampFieldValue(schema, config.getTimestampField());

				if (customTime == null && createTime == null && updateTime == null) {
					incrementalData.add(schema);
				}
				else if (updateTime != null && updateTime.getTime() > lastSyncTime.getTime()
						|| (createTime != null && createTime.getTime() > lastSyncTime.getTime())
						|| (customTime != null && customTime.getTime() > lastSyncTime.getTime())) {
					incrementalData.add(schema);
				}
			}

			log.debug("从SchemaSet数据源 [{}] 表 [{}] 筛选出 {} 条增量数据", name, tableCode, incrementalData.size());

			return incrementalData;

		}
		catch (Exception e) {
			log.error("从SchemaSet数据源 [{}] 获取表 [{}] 的增量数据时发生错误", name, tableCode, e);
			throw new SyncException("获取增量数据失败" + ExceptionUtil.getFullDetailMessage(e), e);
		}
	}

	/**
	 * 使用反射获取Schema对象中的时间戳字段值
	 */
	private Date getTimestampFieldValue(Schema schema, String timestampField) {
		try {
			// 构造getter方法名
			String getterName = "get" + timestampField.substring(0, 1).toUpperCase() + timestampField.substring(1);

			// 尝试调用getter方法
			try {
				Method getter = schema.getClass().getMethod(getterName);
				Object result = getter.invoke(schema);

				DateTime parse = DateUtil.parse(result.toString());

				if (parse instanceof Date) {
					return (Date) parse;
				}
				else if (result instanceof Timestamp) {
					return new Date(((Timestamp) result).getTime());
				}
				else if (result != null) {
					log.warn("时间戳字段 [{}] 的值不是Date或Timestamp类型: {}", timestampField, result.getClass().getName());
				}
			}
			catch (NoSuchMethodException e) {
				// 如果没有标准getter，尝试直接通过字段名获取
				try {
					Object value = schema.getV(timestampField);
					if (value instanceof Date) {
						return (Date) value;
					}
					else if (value instanceof Timestamp) {
						return new Date(((Timestamp) value).getTime());
					}
					else if (value != null) {
						log.warn("时间戳字段 [{}] 的值不是Date或Timestamp类型: {}", timestampField, value.getClass().getName());
					}
				}
				catch (Exception ex) {
					log.warn("无法通过getV方法获取时间戳字段 [{}] 的值", timestampField);
				}
			}

			return null;
		}
		catch (Exception e) {
			log.warn("获取Schema对象的时间戳字段 [{}] 值时发生错误", timestampField, e);
			return null;
		}
	}

	@Override
	public boolean containsTable(String tableCode) {
		return schemaSets.containsKey(tableCode);
	}

	@Override
	public void close() {
		// SchemaSet数据源不需要特殊关闭，但可以清理内存
		schemaSets.clear();
		log.debug("SchemaSet数据源 [{}] 已关闭", name);
	}

}
