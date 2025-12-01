package org.ark.framework.orm.connection;

import io.arkx.framework.data.db.connection.ConnectionPoolManager;
import io.arkx.framework.data.db.dbtype.DBTypeService;
import io.arkx.framework.data.db.dbtype.IDBType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**   
 * 
 * @author Darkness
 * @date 2013-1-28 下午03:22:33 
 * @version V1.0   
 */
public class XConnectionTest {

	@Test
	public void checkDefaultConfigDatabaseType() {
		
		IDBType database = DBTypeService.getInstance().get(ConnectionPoolManager.getDBConnConfig().getDatabaseType());
		
		assertNotNull(database);
	}
}
