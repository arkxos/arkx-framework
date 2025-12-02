package io.arkx.framework.data.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ark.framework.infrastructure.entityfactoryframework.EntityBuilderFactory;
import org.ark.framework.orm.sql.SelectSQLParser;

import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.PageDataTable;
import io.arkx.framework.data.db.QueryException;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionConfig;
import io.arkx.framework.data.db.connection.ConnectionPoolManager;
import io.arkx.framework.data.db.dbtype.DBTypeService;

import lombok.extern.slf4j.Slf4j;

/**
 * @class org.ark.framework.orm.query.QueryBuilder
 * <h2>SQL查询器，用于构造参数化SQL并执行，以避免SQL注入。支持批量模式。</h2> <br/>
 * QueryBuilder query = new QueryBuilder("SELECT * FROM "+TABLE_NAME); <br/>
 * Assert.assertTrue(20 == query.executeDataTable().getRowCount());
 * @author Darkness
 * @date 2011-12-13 下午06:55:51
 * @version V1.0
 */
@Slf4j
public class Query {

	private ArrayList<Object> params = new ArrayList<>();

	private ArrayList<ArrayList<Object>> batches;

	protected StringBuilder sql = new StringBuilder();

	private boolean batchMode;

	private Transaction transaction;

	private boolean isPagedQuery = false;

	private String pagedSql;

	private int pageIndex;

	private int pageSize;

	/**
	 * 构造一个空的查询，等待使用setSQL()方法设置SQL语句
	 */
	Query(Transaction transaction) {
		this.transaction = transaction;
		params = new ArrayList<>();
		sql = new StringBuilder();
	}

	/**
	 * 根据传入的SQL字符串构造一个SQL查询，参数个数可变
	 *
	 * @method QueryBuilder
	 * @param sql
	 * @param {Object...} params
	 * @constructor
	 */
	Query(Transaction transaction, String sql, Object... params) {
		this(transaction);
		setSQL(sql);
		add(params);
	}

	private Connection getConnection() {
		return this.transaction.getConnection();
	}

	/**
	 * 批量操作时，返回所有批量操作的参数列表
	 */
	public ArrayList<ArrayList<Object>> getBatches() {
		return this.batches;
	}

	/**
	 * 添加SQL参数值
	 *
	 * @method add
	 * @param {Object...} params
	 *
	 * @author Darkness
	 * @date 2013-2-22 上午10:31:31
	 * @version V1.0
	 */
	public Query add(Object... paramValues) {
		if (paramValues != null) {
			for (Object param : paramValues) {
				this.params.add(param);
			}
		}
		return this;
	}

	/**
	 * 添加SQL参数值
	 */
	public Query add(Collection<?> params) {
		for (Object param : params) {
			this.params.add(param);
		}
		return this;
	}

	/**
	 * 设置指定位置的SQL参数
	 */
	public Query set(int index, Object param) {// NO_UCD
		params.set(index, param);
		return this;
	}

	/**
	 * 当前SQL操作是否是批量模式
	 */
	public boolean isBatchMode() {
		return this.batchMode;
	}

	/**
	 * 设置批量模式
	 */
	public void setBatchMode(boolean batchMode) {
		if ((batchMode) && (this.batches == null)) {
			this.batches = new ArrayList<>();
		}
		this.batchMode = batchMode;
	}

	/**
	 * 增加一个批次
	 */
	public void addBatch() {
		if (!this.batchMode) {
			throw new RuntimeException("Must invoke setBatchMode(true) before addBatch()");
		}
		this.batches.add(this.params);
		this.params = new ArrayList<>();
	}

	/**
	 * 添加批处理参数
	 *
	 * @method addBatch
	 * @param {List<List<Object>>} params
	 * @author Darkness
	 * @date 2013-2-22 上午10:32:52
	 * @version V1.0
	 */
	public Query addBatch(List<ArrayList<Object>> params) {
		setBatchMode(true);
		batches.addAll(params);
		return this;
	}

	public Query add(Object param) {
		this.params.add(param);
		return this;
	}

	/**
	 * 设置SQL语句
	 */
	public Query setSQL(String sql) {
		this.sql = new StringBuilder(sql);
		return this;
	}

	/**
	 * 追加部分SQL语句，同时追加SQL参数
	 *
	 * @method append
	 * @param {String} sqlPart
	 * @param {Object...} params
	 *
	 * @author Darkness
	 * @date 2013-2-22 上午10:33:42
	 * @version V1.0
	 */
	public Query append(String sqlPart, Object... params) {
		this.sql.append(sqlPart);
		add(params);
		return this;
	}

	public Query setPagedQuery(boolean ispaged) {
		this.isPagedQuery = ispaged;
		return this;
	}

	/**
	 * 获得本查询使用的参数化SQL
	 */
	public String getSQL() {
		if (this.isPagedQuery) {
			ConnectionConfig connectionConfig = null;
			connectionConfig = getConnection().getDBConfig();
			this.pagedSql = JdbcTemplate.getPagedSQL(connectionConfig, this.sql.toString(), pageSize, pageIndex);

			return this.pagedSql;
		}
		return this.sql.toString();
	}

	/**
	 * 返回当前所有SQL参数
	 */
	public ArrayList<Object> getParams() {
		return this.params;
	}

	/**
	 * 一次性设置所有SQL参数
	 *
	 * @method setParams
	 * @param {ArrayList<Object>} list
	 *
	 * @author Darkness
	 * @date 2013-2-22 上午10:51:09
	 * @version V1.0
	 */
	public void setParams(ArrayList<Object> list) {
		this.params = list;
	}

	/**
	 * 批量模式下清空所有批次
	 */
	public void clearBatches() {
		if (this.batchMode) {
			if (this.batches != null) {
				this.batches.clear();
			}
			this.batches = new ArrayList<>();
		}
	}

	/**
	 * 检查参数化SQL中的问号个数与SQL参数个数是否相符
	 */
	public boolean checkParams() {
		char[] arr = this.sql.toString().toCharArray();
		boolean StringCharFlag = false;
		int count = 0;
		for (int i = 0; i < arr.length; i++) {
			char c = arr[i];
			if (c == '\'') {
				if (!StringCharFlag)
					StringCharFlag = true;
				else
					StringCharFlag = false;
			}
			else if (c == '?') {
				if (!StringCharFlag) {
					count++;
				}
			}
		}

		if (count != this.params.size()) {
			throw new RuntimeException("SQL has " + count + " parameter，but value count is " + this.params.size());
		}
		return true;
	}

	/**
	 * 转成可读的SQL语句
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.sql);
		sb.append("\t{");
		for (int i = 0; i < this.params.size(); i++) {
			if (i != 0) {
				sb.append(",");
			}
			Object o = this.params.get(i);
			if (o == null) {
				sb.append("null");
			}
			else {
				String str = this.params.get(i).toString();
				if (str.length() > 40) {
					str = str.substring(0, 37);
					sb.append(str);
					sb.append("...");
				}
				else {
					sb.append(str);
				}
			}
		}
		sb.append("}");
		return sb.toString();
	}

	public String getPagedSql() {
		return pagedSql;
	}

	public void setPagedSql(String pagedSql) {
		this.pagedSql = pagedSql;
	}

	/**
	 * 获取分页大小
	 *
	 * @method getPageSize
	 * @private
	 */
	public int getPageSize() {
		return pageSize;
	}

	public Query setPageSize(int pageSize) {
		this.isPagedQuery = true;
		this.pageSize = pageSize;
		return this;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public Query setPageIndex(int pageIndex) {
		this.isPagedQuery = true;
		this.pageIndex = pageIndex;
		return this;
	}

	public boolean isPagedQuery() {
		return isPagedQuery;
	}

	/**
	 * 如果要在select时为选中的加锁，则需要调用本方法
	 */
	public void appendForUpdateLock() {
		ConnectionConfig dbcc = null;
		dbcc = this.getConnection().getDBConfig();
		append(DBTypeService.getInstance().get(dbcc.DBType).getForUpdate());
	}

	/**
	 * 执行SQL并将结果集封装成DataTable返回
	 */
	public DataTable fetch() {
		return executeDataTable();
	}

	/**
	 * 分页执行SQL并将结果集封装成DataTable返回
	 * @param pageSize 分页大小
	 * @param pageIndex 第几页，0为第一页
	 */
	public DataTable fetch(int pageSize, int pageIndex) {
		return executePagedDataTable(pageSize, pageIndex).getData();
	}

	public DataTable executeDataTable() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.getConnection());
		return jdbcTemplate.executeDataTable(getSQL(), getParams());
	}

	/**
	 * 分页查询结果集
	 *
	 * @method executePagedDataTable
	 * @param {IPageInfo} pageInfo(分页信息)
	 * @param {int} pageSize(每页大小)
	 * @param {int} pageIndex(从第几页开始)
	 * @return {PageDataTable}
	 * @author Darkness
	 * @date 2013-2-22 上午10:37:56
	 * @version V1.0
	 */
	public PageDataTable executePagedDataTable(int pageSize, int pageIndex) {
		setPageSize(pageSize).setPageIndex(pageIndex);

		DataTable dataTable = executeDataTable();

		PageDataTable pageData = new PageDataTable();
		pageData.setPageEnabled(true);
		pageData.setPageIndex(pageIndex);
		pageData.setPageSize(pageSize);

		pageData.setData(dataTable);
		pageData.setTotal(getCount());

		return pageData;
	}

	/**
	 * 分页查询实体集合
	 *
	 * @method findPagedEntities
	 * @param {Class} entityClass(实体的类型)
	 * @param {int} pageSize(每页大小)
	 * @param {int} pageIndex(从第几页开始)
	 * @return {List<T>}
	 * @author Darkness
	 * @date 2012-12-2 下午05:15:49
	 * @version V1.0
	 */
	public List<? extends Entity> findPagedEntities(Class<? extends Entity> clazz, int pageSize, int pageIndex) {
		setPageSize(pageSize).setPageIndex(pageIndex);

		return findEntities(clazz);
	}

	/**
	 * 查询实体
	 *
	 * @method findEntity
	 * @param {Class} entityClass(实体的类型)
	 * @return {<T extends Entity>}
	 * @author Darkness
	 * @date 2012-11-25 下午06:22:33
	 * @version V1.0
	 */
	public <T extends Entity> T findEntity(Class<T> clazz) {
		DataTable dataTable = executeDataTable();

		return (T) EntityBuilderFactory.buildEntityFromDataTable(clazz, dataTable);
	}

	/**
	 * 查询实体集合
	 *
	 * @method findEntities
	 * @param {Class} entityClass(实体的类型)
	 * @return {List<T>}
	 * @author Darkness
	 * @date 2012-11-25 下午06:22:43
	 * @version V1.0
	 */
	public <T extends Entity> List<T> findEntities(Class<T> clazz) {
		DataTable dataTable = this.executeDataTable();

		List<T> result = (List<T>) EntityBuilderFactory.buildEntitiesFromDataTable(clazz, dataTable);
		if (result == null) {
			return new ArrayList<T>();
		}

		return result;
	}

	/**
	 * 通过针对查询器中的SQL构造一个对应的select count(*)语句来获知查询器执行结果的总条数
	 *
	 * @method getCount
	 * @return int 查询器执行结果的总条数。
	 * @author Darkness
	 * @date 2013-2-22 上午10:24:37
	 * @version V1.0
	 */
	@SuppressWarnings("unchecked")
	public int getCount() {
		Query cqb = new Query(this.transaction);
		cqb.setParams((ArrayList<Object>) this.getParams().clone());
		String sql = this.sql.toString();
		int index1 = sql.lastIndexOf(")");
		int index2 = sql.toLowerCase().lastIndexOf("order by");
		if (index2 > index1) {
			sql = sql.substring(0, index2);
		}

		if (ConnectionPoolManager.getDBConnConfig().isMysql()) {
			SelectSQLParser ssp = new SelectSQLParser();
			ssp.setSQL(sql);
			try {
				ssp.parse();
			}
			catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
			cqb.setSQL(ssp.getMysqlCountSQL());
		}
		else {
			cqb.setSQL("select count(1) from (" + sql + ") t1");
		}
		Object obj = cqb.executeOneValue();
		if (obj == null) {
			return 0;
		}
		return Integer.parseInt(obj.toString());
	}

	public Object executeOneValue() {
		JdbcTemplate da = new JdbcTemplate(this.getConnection());
		return da.executeOneValue(this.getSQL(), this.getParams());
	}

	public int executeInt() {
		JdbcTemplate da = new JdbcTemplate(this.getConnection());
		return da.executeInt(this.getSQL(), this.getParams());
	}

	public long executeLong() {
		JdbcTemplate da = new JdbcTemplate(this.getConnection());
		return da.executeLong(this.getSQL(), this.getParams());
	}

	/**
	 * 执行查询，并返回第一条记录的第一个字段的值并转化为float
	 */
	public float executeFloat() {
		JdbcTemplate da = new JdbcTemplate(this.getConnection());
		return da.executeFloat(this.getSQL(), this.getParams());
	}

	public double executeDouble() {
		JdbcTemplate da = new JdbcTemplate(this.getConnection());
		return da.executeDouble(this.getSQL(), this.getParams());
	}

	public String executeString() {
		JdbcTemplate da = new JdbcTemplate(this.getConnection());
		return da.executeString(this.getSQL(), this.getParams());
	}

	public Object executeQuery(ICallbackStatement callbackStatement) {
		JdbcTemplate da = new JdbcTemplate(this.getConnection());
		return da.executeQuery(this.getSQL(), this.getParams(), callbackStatement);
	}

	/**
	 * 执行更新，返回影响的行数
	 *
	 * @method executeNoQuery
	 * @return int
	 *
	 * @author Darkness
	 * @date 2013-2-22 上午10:49:24
	 * @version V1.0
	 */
	public int executeNoQuery() {
		JdbcTemplate da = new JdbcTemplate(this.getConnection());
		try {
			int t = -1;
			if (batchMode) {
				int[] result = da.executeBatch(getSQL(), batches);
				if (result == null || result.length <= 0) {
					return 0;
				}
				return result[0];
			}
			else {
				t = da.executeUpdate(getSQL(), params);
			}
			return t;
		}
		catch (Exception e) {
			System.out.println("error sql:" + getSQL());
			e.printStackTrace();
			throw new QueryException(e.getMessage() + "[" + getSQL() + "]");
		}
		finally {
		}
	}

}
