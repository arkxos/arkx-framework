package com.arkxos.framework.data.simplejdbc;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.arkxos.framework.data.simplejdbc.sql.IExecuteQueryCallback;
import com.arkxos.framework.data.simplejdbc.sql.SqlHelper;

/**
 * Orm管理器
 * 
 * @author Darkness
 * @website www.rapidark.com
 * @date 2013-4-14 下午08:13:51
 * @version V1.0
 */
public class OrmManager {

	/**
	 * 根据sql查询出实体列表
	 * 
	 * @author Darkness
	 * @date 2013-4-14 下午08:58:48 
	 * @version V1.0
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> queryEntity(String sql, final Class<T> entityClazz) {
		List<T> queryResult = (List<T>) SqlHelper.executeQuery(sql, new IExecuteQueryCallback() {

			@Override
			public Object execute(ResultSet rs) throws SQLException {

				List<T> result = new ArrayList<T>();

				// 获取数据库的元数据
				ResultSetMetaData rsmd = rs.getMetaData();
				// 获取数据库表中列的数量
				int columnCount = rsmd.getColumnCount();
				
				// 获取类中定义的字段
				Field[] fields = entityClazz.getDeclaredFields();
				
				while (rs.next()) {
					T entity = null;
					try {
						entity = entityClazz.newInstance();
					} catch (InstantiationException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					}

					for (int i = 1; i <= columnCount; i++) {
						// 获取数据库列的名称
						String columnName = rsmd.getColumnLabel(i);
						
						for (Field field : fields) {
							if(field.getName().equalsIgnoreCase(columnName)) {
								field.setAccessible(true);// 设置filed的访问权限为允许
								try {
									// 设置field的值
									field.set(entity, rs.getObject(columnName));
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								}
							}
						}
					}
					
					result.add(entity);
				}

				return result;
			}
		});
		
		return queryResult;
	}

}
