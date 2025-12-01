package io.arkx.framework.data.simplejdbc.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**   
 * 查询结果的回调处理操作类
 * @author Darkness
 * @date 2013-4-14 上午10:50:13 
 * @version V1.0   
 */
public interface IExecuteQueryCallback {
	
	/**
	 * 查询结果集的回调处理
	 * 
	 * @author Darkness
	 * @date 2013-4-14 上午11:06:49 
	 * @version V1.0
	 */
	Object execute(ResultSet rs) throws SQLException;
}
