package io.arkx.framework.config;

import com.arkxos.framework.Config;
import io.arkx.framework.commons.util.ObjectUtil;
import com.arkxos.framework.data.db.connection.Connection;
import com.arkxos.framework.data.db.connection.ConnectionPoolManager;
import com.arkxos.framework.data.db.exception.DatabaseException;
import com.arkxos.framework.data.jdbc.TransactionFactory;

/**
 * 配置读写分离时的只读库的连接池名称，可以配置多个连接池名称，以逗号分隔。<br>
 * 如果配置有多个只读库(一般处于database.xml中)，则只读语句会被随机分配到不同的只读库。
 * 
 */
public class ReadOnlyDB implements IApplicationConfigItem {
	public static final String ID = "ReadOnlyDB";
	private static int i = 0;
	private static String[] pools;

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "The readonly database in Read/Write spliting mode";
	}

	public static String getValue() {
		return Config.getValue("App." + ID);
	}

	public static Connection getReadOnlyDBConn(boolean bLongTimeFlag) {
		if (TransactionFactory.getCurrentTransaction() != null) {
			return TransactionFactory.getCurrentTransaction().getConnection();
		}
		String str = getValue();
		if (pools == null) {
			if (ObjectUtil.notEmpty(str)) {

				pools = getValue().split("");
			} else {
				pools = new String[0];
			}
		}
		if (pools.length == 0) {
			throw new DatabaseException("ReadOnlyDB not configured!");
		}
		return ConnectionPoolManager.getConnection(pools[i++ % pools.length], bLongTimeFlag);
	}
}
