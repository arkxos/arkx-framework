package com.arkxos.framework.data.db;
//package com.rapidark.framework.data.db;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.rapidark.framework.commons.collection.Executor;
//import com.rapidark.framework.data.db.connection.ConnectionPoolManager;
//import com.rapidark.framework.data.db.exception.DatabaseException;
//import com.rapidark.framework.data.db.orm.DAO;
//import com.rapidark.framework.data.db.orm.DAOSet;
//import com.rapidark.framework.data.jdbc.Entity;
//import com.rapidark.framework.data.jdbc.JdbcTemplate;
//import com.rapidark.framework.data.jdbc.Query;
//import com.rapidark.framework.data.jdbc.Session;
//import com.rapidark.framework.data.jdbc.SessionFactory;
//
///**
// * 非阻塞事务处理类，使用本类处理事务不会一开始即占用连接，只有在最后commit()时<br>
// * 才会最终占用数据库连接，在此之前只是将操作缓存在内存之中，从而节约占用数据库连接的时间。<br>
// * 1、一般情况都要求使用本类处理事务，本类在任何情况下都不需要手工管理连接，并且性能较优。<br>
// * 2、因本类缓存了数据库操作，如果数据库操作涉及到大量数据的插入(例如一次插入100W条数据)，<br>
// * 则可能会导致内存溢出，此种情况下请使用阻塞型事务。<br>
// * 3、在本类的事务处理过程中查询数据库，查询到的值依然是事务未开始之前的值(因为在未commit()<br>
// * 之前，实际上未向数据库提交任何操作)。 <br>
// * 4、如果事务处理的过程中需要从数据库查询值，并且要求查询到的值是本次事务己提交的操作的结果，<br>
// * 则需要使用阻塞型事务处理。 <br>
// * 5、若要使用JDBC原生事务处理，请使用DataAccess类，一般情况下不推荐使用。<br>
// * 
// * @see com.rapidark.framework.data.db.BlockingTransaction
// */
//public class Transaction {
//	/**
//	 * 插入数据
//	 */
//	public static final int INSERT = 1;
//
//	/**
//	 * 更新数据
//	 */
//	public static final int UPDATE = 2;
//
//	/**
//	 * 删除数据
//	 */
//	public static final int DELETE = 3;
//
//	/**
//	 * 先删除再插入数据
//	 */
//	public static final int DELETE_AND_INSERT = 6;
//
//	/**
//	 * SQL操作
//	 */
//	public static final int SQL = 7;
//
//	/**
//	 * 是否是外部JDBC连接
//	 */
//	protected boolean outerConnFlag = false;
//
//	protected JdbcTemplate dataAccess;
//
//	/**
//	 * 操作队列
//	 */
//	protected ArrayList<Object> list = new ArrayList<>();
//
//	/**
//	 * 备份到B表操作的操作人
//	 */
//	protected String backupOperator;
//
//	/**
//	 * 备份到B表操作的备注
//	 */
//	protected String backupMemo;
//
//	/**
//	 * 如果产生异常，则此变量会有值
//	 */
//	protected String exceptionMessage;// 异常消息
//
//	/**
//	 * 执行器列表
//	 */
//	protected ArrayList<Executor> executorList = new ArrayList<>(4);
//
//	/**
//	 * 在此连接池上执行操作
//	 */
//	protected String poolName;
//
//	/**
//	 * 空构造器
//	 */
//	public Transaction() {
//	}
//
//	/**
//	 * 构造器
//	 * 
//	 * @param poolName 事务中的所有SQL将在此连接池中执行
//	 */
//	public Transaction(String poolName) {// NO_UCD
//		this.poolName = poolName;
//	}
//
//	/**
//	 * 设置当前事务使用的DataAccess对象
//	 */
//	public void setDataAccess(JdbcTemplate dAccess) {
//		dataAccess = dAccess;
//		outerConnFlag = true;
//	}
//
//	/**
//	 * 增加一个SQL操作
//	 */
//	/**
//	 * @param q 构造器
//	 * @return 实例本身
//	 */
//	public void add(Query q) {
//		list.add(new Object[] { q, new Integer(Transaction.SQL) });
//	}
//	
//	public void insert(Entity entity) {
//		add(entity, INSERT);
//	}
//	
//	public void insert(List<? extends Entity> columnSet) {
//		for (Entity entity : columnSet) {
//			add(entity, INSERT);
//		}
//	}
//	
//	/**
//	 * 增加一个DAO插入操作
//	 */
//	public void insert(DAO<?> repository) {
//		add(repository, Transaction.INSERT);
//	}
//
//	/**
//	 * 增加一个DAOSet插入操作
//	 */
//	public void insert(DAOSet<?> set) {
//		add(set, Transaction.INSERT);
//	}
//	
//	public void update(List<?> schema) {
//		add(schema, UPDATE);
//	}
//	
//	public void update(Entity schema) {
//		add(schema, UPDATE);
//	}
//
//	/**
//	 * 增加一个DAO更新操作
//	 */
//	public void update(DAO<?> repository) {
//		add(repository, Transaction.UPDATE);
//	}
//
//	/**
//	 * 增加一个DAOSet更新操作
//	 */
//	public void update(DAOSet<?> set) {
//		add(set, Transaction.UPDATE);
//	}
//
//	public void delete(Entity schema) {
//		add(schema, DELETE);
//	}
//	
//	public void delete(List<? extends Entity> schema) {
//		add(schema, DELETE);
//	}
//	
//	/**
//	 * 增加一个DAO删除操作
//	 */
//	public void delete(DAO<?> repository) {
//		add(repository, Transaction.DELETE);
//	}
//
//	/**
//	 * 增加一个DAOSet删除操作
//	 */
//	public void delete(DAOSet<?> set) {
//		add(set, Transaction.DELETE);
//	}
//
//	/**
//	 * 增加一个DAO删除并新建操作
//	 */
//	public void deleteAndInsert(DAO<?> repository) {
//		add(repository, Transaction.DELETE_AND_INSERT);
//	}
//
//	/**
//	 * 增加一个DAOSet删除并新建操作
//	 */
//	public void deleteAndInsert(DAOSet<?> set) {// NO_UCD
//		add(set, Transaction.DELETE_AND_INSERT);
//	}
//
//	/**
//	 * 提交事务到数据库
//	 */
//	public boolean commit() {
//		return commit(true);
//	}
//
//	/**
//	 * 提交事务，若setAutoCommitStatus为false并且使用的是外部的DataAccess，则只将SQL提交到DataAccess。
//	 */
//	public boolean commit(boolean setAutoCommitStatus) {
//		if (!outerConnFlag) {
//			dataAccess = new JdbcTemplate(ConnectionPoolManager.getConnection(poolName));
//		}
//		boolean NoErrFlag = true;
//		try {
//			if (!outerConnFlag || setAutoCommitStatus) {
////				dataAccess.setAutoCommit(false);
//			}
//			for (int i = 0; i < list.size(); i++) {
//				Object[] arr = (Object[]) list.get(i);
//				Object obj = arr[0];
//				int type = ((Integer) arr[1]).intValue();
//				if (!executeOperation(obj, type)) {
//					NoErrFlag = false;
//					return false;
//				}
//			}
////			dataAccess.commit();
//			list.clear();
//		} catch (Exception e) {
//			e.printStackTrace();
//			exceptionMessage = e.getMessage();
//			NoErrFlag = false;
//			return false;
//		} finally {
//			if (!NoErrFlag) {
//				try {
////					dataAccess.rollback();
//				} catch (DatabaseException e1) {
//					e1.printStackTrace();
//				}
//			}
//			try {
//				if (!outerConnFlag || setAutoCommitStatus) {
////					dataAccess.setAutoCommit(true);
//				}
//			} catch (DatabaseException e1) {
//				e1.printStackTrace();
//			}
//			if (!outerConnFlag) {
//				try {
////					dataAccess.close();
//				} catch (DatabaseException e) {
//					e.printStackTrace();
//				}
//			}
//			for (int i = 0; i < executorList.size(); i++) {
//				Executor executor = executorList.get(i);
//				executor.execute();
//			}
//		}
//		return true;
//	}
//	
//	private Session getSession() {
//		return SessionFactory.currentSession();
//	}
//
//	protected boolean executeOperation(Object obj, int type) throws SQLException {
//		if (obj instanceof Query) {
//			com.rapidark.framework.data.jdbc.Query query = (com.rapidark.framework.data.jdbc.Query) obj;
//			query.setConnection(dataAccess.getConnection());
//			query.executeNoQuery();
//		} else if ((obj instanceof Entity)) {
//			Entity entity = (Entity) obj;
//
//			if (type == INSERT) {
//				if (getSession().save(entity) < 0) {
//					return true;
//				}
//				return true;
//			} else if (type == UPDATE) {
//				if (getSession().update(entity) < 0)
//					return true;
//			} else if (type == DELETE) {
//				if (getSession().delete(entity) < 0)
//					return true;
//			}
//		} else if (obj instanceof DAO) {
//			DAO<?> s = (DAO<?>) obj;
//			s.setDataAccess(dataAccess);
//			if (type == Transaction.INSERT) {
//				if (!s.insert()) {
//					return false;
//				}
//			} else if (type == Transaction.UPDATE) {
//				if (!s.update()) {
//					return false;
//				}
//			} else if (type == Transaction.DELETE) {
//				s.delete();
//			} else if (type == Transaction.DELETE_AND_INSERT) {
//				if (!s.deleteAndInsert()) {
//					return false;
//				}
//			}
//		} else if (obj instanceof DAOSet) {
//			DAOSet<?> s = (DAOSet<?>) obj;
//			s.setDataAccess(dataAccess);
//			if (type == Transaction.INSERT) {
//				if (!s.insert()) {
//					return false;
//				}
//			} else if (type == Transaction.UPDATE) {
//				if (!s.update()) {
//					return false;
//				}
//			} else if (type == Transaction.DELETE) {
//				if (!s.delete()) {
//					return false;
//				}
//			} else if (type == Transaction.DELETE_AND_INSERT) {
//				if (!s.deleteAndInsert()) {
//					return false;
//				}
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * 清除所有的操作
//	 */
//	public void clear() {
//		list.clear();
//	}
//
//	/**
//	 * 获取执行过程中的SQL异常消息
//	 */
//	public String getExceptionMessage() {
//		return exceptionMessage;
//	}
//
//	/**
//	 * 获取本次事务统一的备份备注信息
//	 */
//	public String getBackupMemo() {
//		return backupMemo;
//	}
//
//	/**
//	 * 设置本次事务统一的备份备注信息
//	 */
//	public Transaction setBackupMemo(String backupMemo) {
//		this.backupMemo = backupMemo;
//		return this;
//	}
//
//	/**
//	 * 获取本次事务统一的备份人信息
//	 */
//	public String getBackupOperator() {
//		return backupOperator;
//	}
//
//	/**
//	 * 设置本次事务统一的备份人信息
//	 */
//	public void setBackupOperator(String backupOperator) {
//		this.backupOperator = backupOperator;
//	}
//
//	/**
//	 * 返回包含所有操作的List
//	 */
//	public ArrayList<Object> getOperateList() {
//		return list;
//	}
//
//	/**
//	 * 增加一个执行器，执行器中的逻辑将在commit()之后执行
//	 */
//	public void addExecutor(Executor executor) {
//		executorList.add(executor);
//	}
//	
//	public void add(Entity entity, int type) {
//		this.list.add(new Object[] { entity, new Integer(type) });
//	}
//
//	public void add(List<?> set, int type) {
//		this.list.add(new Object[] { set, new Integer(type) });
//	}
//	
//	/**
//	 * 增加一个DAO操作，操作类型为opType
//	 */
//	public void add(DAO<?> repository, int opType) {
//		list.add(new Object[] { repository, new Integer(opType) });
//	}
//
//	/**
//	 * 增加一个DAOSet操作，操作类型为opType
//	 */
//	public void add(DAOSet<?> set, int opType) {
//		list.add(new Object[] { set, new Integer(opType) });
//	}
//
//	
//}
