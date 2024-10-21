package org.ark.framework.orm.db;

import com.rapidark.framework.data.db.connection.ConnectionPoolManager;
import com.rapidark.framework.data.db.dbtype.DBTypeService;
import com.rapidark.framework.data.db.dbtype.IDBType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**   
 * 
 * @author Darkness
 * @date 2013-1-28 下午03:30:47 
 * @version V1.0   
 */
public class DatabaseServiceTest {

	@Test
	public void isTableExist() {
		IDBType database = DBTypeService.getInstance().get(ConnectionPoolManager.getDBConnConfig().getDatabaseType());
		
		boolean isTableExist = database.isTableExist("rapid_ark", "pt_comp__maxno");
		assertTrue(isTableExist);
	}
}
