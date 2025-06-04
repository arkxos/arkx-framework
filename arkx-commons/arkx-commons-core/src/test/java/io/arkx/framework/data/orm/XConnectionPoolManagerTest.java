package io.arkx.framework.data.orm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionPoolManager;
import io.arkx.framework.XTest;

/**
 * 
 * @author Darkness
 * @date 2012-9-27 下午9:39:35
 * @version V1.0
 */
public class XConnectionPoolManagerTest extends XTest {

	
	@Test
	public void getConnection() {
		
		Connection oldConn = null;
		
		for (int i = 0; i < 1000; i++) {
			Connection conn = ConnectionPoolManager.getConnection();
			
			if(i != 0) {
				assertEquals(oldConn, conn);
			}
			oldConn = conn;
			
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
