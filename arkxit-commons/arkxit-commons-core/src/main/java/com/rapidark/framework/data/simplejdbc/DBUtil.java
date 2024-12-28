package com.rapidark.framework.data.simplejdbc;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rapidark.framework.commons.util.PropertiesUtil;
import com.rapidark.framework.data.simplejdbc.query.Criterion;

/**
 * @author Darkness
 * @date 2014-4-15 下午1:22:30
 * @version V1.0
 */
public class DBUtil {
	
	private static Log logger = LogFactory.getLog(DBUtil.class);

	private static Connection _connection;
	
	private static Connection connection() {
		if(_connection == null) {
			DBUtil.createConnectionSmall(null);
		}
		return _connection;
	}

	public static void createConnectionSmall(String rootPath) {
		try {
			if (rootPath == null) {
				rootPath = System.getProperty("user.dir");
			}

			Class.forName("smallsql.database.SSDriver");
			_connection = DriverManager.getConnection("jdbc:smallsql:" + rootPath + File.separator + "data");
		} catch (Throwable e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static void createConnection(String rootPath) {
		InputStream databasePropertiesFilePath = DBUtil.class.getResourceAsStream("/database.properties");
		logger.debug("database properties file path: " + databasePropertiesFilePath);
		Map<String, String> props = PropertiesUtil.read(databasePropertiesFilePath);
		String url = props.get("jdbc.url");//"jdbc:mysql://localhost:3306/sky_stock";
		String user = props.get("jdbc.username");//"root";
		String pwd = props.get("jdbc.password");//"root";
		String driver = props.get("jdbc.driverClassName");//"com.mysql.cj.jdbc.Driver"
		
		logger.debug("jdbc.url:" + url);
		
		// 加载驱动，这一句也可写为：Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// 建立到MySQL的连接
		try {
			_connection = DriverManager.getConnection(url, user, pwd);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static int executeUpdate(String sql, boolean needGeneratedKeys) {
		System.out.println("sql:" + sql);
		Statement statement = null;
		ResultSet resultSet = null;
		int ret = -1;

		try {
			statement = connection().createStatement();

			if (needGeneratedKeys) {
				ret = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			} else {
				ret = statement.executeUpdate(sql);
			}

			if (needGeneratedKeys && ret >= 0) {
				resultSet = statement.getGeneratedKeys();

				if (resultSet.next()) {
					ret = resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(resultSet, statement);
			resultSet = null;
			statement = null;
		}

		return ret;
	}

	public static int executeUpdate(String sql, Object[] parameters, boolean needGeneratedKeys) {
		System.out.println("update:"+sql);
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		int ret = -1;

		try {
			if (needGeneratedKeys) {
				statement = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			} else {
				statement = connection().prepareStatement(sql);
			}

			setParameters(statement, parameters);
			ret = statement.executeUpdate();

			if (needGeneratedKeys && ret >= 0) {
				resultSet = statement.getGeneratedKeys();

//				if (resultSet.next()) {
//					ret = resultSet.getInt(1);
//				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(resultSet, statement);
			resultSet = null;
			statement = null;
		}

		return ret;
	}

	public static int executeBatchUpdate(String sql, List<Object[]> parameters) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		int ret = -1;

		try {
			connection().setAutoCommit(false);

			statement = connection().prepareStatement(sql);

			for (Object[] objects : parameters) {
				setParameters(statement, objects);
				// 把一个SQL命令加入命令列表
				statement.addBatch();
			}

			// 执行批量更新
			statement.executeBatch();
			connection().commit();
			connection().setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(resultSet, statement);
			resultSet = null;
			statement = null;
		}

		return ret;
	}

	public static List<Object[]> executeQuery(String sql, int columnCount) {
		return executeQuery(sql, columnCount, new DefaultQueryBuildEntity());
	}

	public static <T> List<T> executeQuery(String sql, int columnCount, IQueryBuildEntity<T> queryBuildEntity) {
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection().createStatement();
			resultSet = statement.executeQuery(sql);
			return buildResult(resultSet, columnCount, queryBuildEntity);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(resultSet, statement);
			resultSet = null;
			statement = null;
		}

		return new ArrayList<T>();
	}

	public static List<Object[]> executeQuery(String sql, Object[] parameters, int columnCount) {
		System.out.println("query:" + sql);
		return executeQuery(sql, parameters, columnCount, new DefaultQueryBuildEntity());
	}

	public static <T> List<T> executeQuery(String sql, Object[] parameters, int columnCount, IQueryBuildEntity<T> queryBuildEntity) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection().prepareStatement(sql);
			setParameters(statement, parameters);
			resultSet = statement.executeQuery();
			return buildResult(resultSet, columnCount, queryBuildEntity);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(resultSet, statement);
			resultSet = null;
			statement = null;
		}

		return new ArrayList<T>();
	}

	public static String createConditionSQL(Criterion... criterions) {
		StringBuilder sql = new StringBuilder();
		int index = 0;

		for (Criterion criterion : criterions) {
			if (index == 0) {
				sql.append(" where ");
			} else {
				sql.append(" and ");
			}

			sql.append(criterion.toSqlString());
			index++;
		}

		return sql.toString();
	}

	public static Object[] createConditionParameters(Criterion... criterions) {
		List<Object> list = new ArrayList<Object>();

		for (Criterion criterion : criterions) {
			if (criterion.getValue() != null) {
				list.add(criterion.getValue());
			} else if (criterion.getValues() != null) {
				list.addAll(criterion.getValues());
			}
		}

		return list.toArray();
	}

	private static <T> List<T> buildResult(ResultSet resultSet, int columnCount, IQueryBuildEntity<T> queryBuildEntity) throws SQLException {

		return queryBuildEntity.buildEntity(resultSet, columnCount);
	}

	private static void setParameters(PreparedStatement statement, Object[] parameters) throws SQLException {
		int index = 1;

		for (Object parameter : parameters) {
			statement.setObject(index++, parameter);
		}
	}

	private static void close(ResultSet resultSet, Statement statement) {
		if (resultSet != null) {
			try {
				resultSet.close();
				resultSet = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (statement != null) {
			try {
				statement.close();
				statement = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeConnection() {
		try {
			if (_connection != null && !_connection.isClosed()) {
				_connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}