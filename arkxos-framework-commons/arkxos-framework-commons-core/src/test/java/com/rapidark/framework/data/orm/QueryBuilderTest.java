package com.rapidark.framework.data.orm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.Test;

import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.data.db.connection.ConnectionPoolManager;
import com.arkxos.framework.data.jdbc.Session;
import com.arkxos.framework.data.jdbc.SessionFactory;
import com.rapidark.framework.XTest;

/**
 * 
 * @author Darkness
 * @date 2012-4-9 下午10:09:32
 * @version V1.0
 */
public class QueryBuilderTest extends XTest {
	
	private static final String TABLE_NAME = "QueryBuilder_XTest";

	/**
	 * testCreateTable
	 */
	@SuppressWarnings("serial")
	@Override
	public void init() {
		if(ConnectionPoolManager.getConnection().getDBConfig().isOracle()) {
			int exist = SessionFactory.openSession().readOnly().createQuery("select count(tname) from tab where tname = upper( ? )", new Object[]{TABLE_NAME}).executeInt();
			if(exist > 0) {
				getSession().createQuery("DROP TABLE "+TABLE_NAME).executeNoQuery();
			}
		} else if(ConnectionPoolManager.getConnection().getDBConfig().isMysql())  {
			getSession().createQuery("DROP TABLE IF EXISTS "+TABLE_NAME).executeNoQuery();
		}
		
		getSession().createQuery("create table "+TABLE_NAME+"(id varchar(10))").executeNoQuery();
		
		// insertDatatable
		getSession().createQuery("INSERT INTO "+TABLE_NAME+"(id) VALUES(?)").addBatch(new ArrayList<ArrayList<Object>>() {
			{
				for (int i = 0; i < 20; i++) {
					ArrayList<Object> array = new ArrayList<Object>();
					array.add(i + 1);
					add(array);
				}
			}
		}).executeNoQuery();
	}

	@Test
	public void queryDatatable() {
		DataTable dataTable = SessionFactory.openSession().readOnly().createQuery("SELECT * FROM "+TABLE_NAME).executeDataTable();
		assertTrue(20 == dataTable.getRowCount());
	}

	@Test
	public void queryPagedDatatable() {
		String sql = "SELECT * FROM "+TABLE_NAME;
		assertTrue(10 == SessionFactory.openSession().readOnly().createQuery(sql).executeDataTable().getRowCount());
	}
	
	@After("")
    public void testDropTable() {
		getSession().createQuery("DROP TABLE QueryBuilder_XTest").executeNoQuery();
	}
	public static Session getSession() {
		return SessionFactory.currentSession();
	}
}


