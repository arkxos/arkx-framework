package org.ark.framework.orm.connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.arkx.framework.data.db.connection.ConnectionPoolManager;
import io.arkx.framework.data.db.dbtype.DBTypeService;
import io.arkx.framework.data.db.dbtype.IDBType;

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
