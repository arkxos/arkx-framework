package io.arkx.framework.data.orm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import io.arkx.framework.XTest;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.jdbc.ICallbackStatement;
import io.arkx.framework.data.jdbc.JdbcTemplate;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;

/**
 * @author Darkness
 * @date 2012-4-9 下午9:25:32
 * @version V1.0
 */
public class DataAccessTest extends XTest {

	private JdbcTemplate dataAccess;

	private static final String TEST_TABLE_NAME = "test_dataaccess";

	@Override
	public void init() {
		dataAccess = new JdbcTemplate(null);
		try {
			createTable();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getConnection() {
		assertTrue(dataAccess.getConnection() != null);
	}

	@SuppressWarnings("serial")
	public void createTable() throws SQLException {
		try {
			if (dataAccess.getConnection().getDBConfig().isOracle()) {
				BigDecimal exist = (BigDecimal) dataAccess
					.executeOneValue("select count(tname) from tab where tname = upper( ? )", new ArrayList<Object>() {
						{
							add(TEST_TABLE_NAME);
						}
					});
				if (exist.intValue() > 0) {
					dataAccess.executeUpdate("DROP TABLE " + TEST_TABLE_NAME);
				}
			}
			else if (dataAccess.getConnection().getDBConfig().isMysql()) {
				dataAccess.executeUpdate("DROP TABLE IF EXISTS " + TEST_TABLE_NAME);
			}

			dataAccess.executeUpdate("create table " + TEST_TABLE_NAME + "(id varchar(10))");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		// 批量添加测试数据
		dataAccess.executeBatch("INSERT INTO " + TEST_TABLE_NAME + "(id) VALUES(?)",
				new ArrayList<ArrayList<Object>>() {
					{
						for (int i = 0; i < 20; i++) {
							ArrayList<Object> array = new ArrayList<>();
							array.add(i + 1);
							add(array);
						}
					}
				});
	}

	@Test
	public void executeQuery() throws SQLException {
		assertTrue((Boolean) dataAccess.executeQuery("SELECT ID FROM " + TEST_TABLE_NAME, new ICallbackStatement() {
			public Object execute(Connection connection, PreparedStatement stmt, ResultSet rs) throws SQLException {
				return rs.next();
			}
		}));
	}

	@SuppressWarnings("serial")
	@Test
	public void executeUpdate() throws SQLException {

		assertEquals(
				dataAccess.executeUpdate("UPDATE " + TEST_TABLE_NAME + " SET id=? WHERE id=?", new ArrayList<Object>() {
					{
						add(100);
						add(2);
					}
				}), 1);// 一条记录更新成功

		assertEquals(
				dataAccess.executeUpdate("DELETE FROM " + TEST_TABLE_NAME + " WHERE id=?", new ArrayList<Object>() {
					{
						add(2);
					}
				}), 0);// 0条记录，删除失败，id为2的记录已经被上面更新成了100
	}

	@SuppressWarnings("serial")
	@Test
	public void executeUpdateBatch() throws SQLException {
		dataAccess.executeBatch("UPDATE " + TEST_TABLE_NAME + " SET id=? WHERE id=?",
				new ArrayList<ArrayList<Object>>() {
					{
						for (int i = 20; i > 0; i--) {
							ArrayList<Object> array = new ArrayList<>();

							array.add(i + 1);
							array.add(i);

							add(array);
						}
					}
				});// 20条记录，所有id+1
	}

	@Test
	public void executeDataTable() throws SQLException {

		DataTable dataTable = getSession().createQuery("SELECT * FROM " + TEST_TABLE_NAME).executeDataTable();
		assertEquals(dataTable.getRowCount(), 20);
	}

	@SuppressWarnings("serial")
	@Test
	public void executeOneValue() throws SQLException {
		assertEquals(
				dataAccess.executeOneValue("SELECT * FROM " + TEST_TABLE_NAME + " WHERE id=?", new ArrayList<Object>() {
					{
						add(15);
					}
				}), "15");
	}

	@Test
	public void testDropTestTable() {
		try {
			dataAccess.executeUpdate("DROP TABLE " + TEST_TABLE_NAME);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Session getSession() {
		return SessionFactory.currentSession();
	}

}
