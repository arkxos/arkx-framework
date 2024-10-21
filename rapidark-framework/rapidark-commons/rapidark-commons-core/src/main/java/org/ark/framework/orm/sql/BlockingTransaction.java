//package org.ark.framework.orm.sql;
//
//import java.sql.SQLException;
//
//import org.ark.framework.collection.Executor;
//import org.ark.framework.orm.Schema;
//import org.ark.framework.orm.SchemaSet;
//
//import com.rapidark.framework.data.db.connection.Connection;
//import com.rapidark.framework.data.db.connection.ConnectionPoolManager;
//import com.rapidark.framework.data.db.exception.CommitException;
//import com.rapidark.framework.data.jdbc.JdbcTemplate;
//import com.rapidark.framework.data.jdbc.Query;
//
//
///**
// * @class org.ark.framework.orm.sql.BlockingTransaction
// * 
// * @author Darkness
// * @date 2013-1-31 上午11:44:35 
// * @version V1.0
// */
//public class BlockingTransaction extends Transaction {
//	
//	private boolean isExistsOpeningOperate = false;
//
//	private static ThreadLocal<Object> current = new ThreadLocal<Object>();
//	private Connection conn;
//
//	public BlockingTransaction() {
//		if (this.dataAccess == null) {
//			this.conn = ConnectionPoolManager.getConnection();
//			this.conn.isBlockingTransactionStarted = true;
//			this.dataAccess = new JdbcTemplate(this.conn);
////			this.dataAccess.setAutoCommit(false);
//			bindTransactionToThread();
//		}
//	}
//
//	public BlockingTransaction(String poolName) {
//		this.dataAccess = new JdbcTemplate(ConnectionPoolManager.getConnection(poolName));
//		this.conn = this.dataAccess.getConnection();
//		this.conn.isBlockingTransactionStarted = true;
//		
////		this.dataAccess.setAutoCommit(false);
//		bindTransactionToThread();
//	}
//
//	public BlockingTransaction(JdbcTemplate da) {
//		this.dataAccess = da;
//		this.conn = this.dataAccess.getConnection();
//		this.conn.isBlockingTransactionStarted = true;
////		this.dataAccess.setAutoCommit(false);
//		bindTransactionToThread();
//	}
//
//	public void setDataAccess(JdbcTemplate dAccess) {
//		if ((this.dataAccess != null) && (!this.outerConnFlag)) {
//			throw new RuntimeException("setDataAccess() must before any add()");
//		}
//		super.setDataAccess(dAccess);
//	}
//
//	public void add(Query qb) {
//		executeWithBlockedConnection(qb, 7, true);
//	}
//
//	public void add(Schema schema, int type) {
//		executeWithBlockedConnection(schema, type, true);
//	}
//
//	public void add(SchemaSet<?> set, int type) {
//		executeWithBlockedConnection(set, type, true);
//	}
//
//	public void addWithException(Query qb) throws Exception {
//		executeWithBlockedConnection(qb, 7, false);
//	}
//
//	public void addWithException(Schema schema, int type) throws Exception {
//		executeWithException(schema, type);
//	}
//
//	public void addWithException(SchemaSet<?> set, int type) throws Exception {
//		executeWithException(set, type);
//	}
//
//	private void executeWithBlockedConnection(Object obj, int type, boolean rollBackFlag) {
//		try {
//			executeObject(obj, type);
//			this.isExistsOpeningOperate = true;
//		} catch (SQLException e) {
//			if ((!this.outerConnFlag) && (rollBackFlag)) {
//				try {
////					this.dataAccess.rollback();
//					this.conn.isBlockingTransactionStarted = false;
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				} finally {
////						this.dataAccess.close();
//				}
//			}
//			throw new RuntimeException(e);
//		}
//	}
//
//	private void executeWithException(Object obj, int type) throws Exception {
//		executeObject(obj, type);
//		this.isExistsOpeningOperate = true;
//	}
//
//	public boolean commit() {
//		return commit(true);
//	}
//
//	public boolean commit(boolean setAutoCommitStatus) {
//		if (this.dataAccess != null) {
//			try {
////				this.dataAccess.commit();
//			} catch (CommitException e) {
//				e.printStackTrace();
//				this.exceptionMessage = e.getMessage();
//				if (!this.outerConnFlag) {
////					this.dataAccess.rollback();
//				}
//				return false;
//			} finally {
//					if ((!this.outerConnFlag) || (setAutoCommitStatus))
////						this.dataAccess.setAutoCommit(true);
//				if (!this.outerConnFlag) {
//					try {
//						this.conn.isBlockingTransactionStarted = false;
//						this.dataAccess.getConnection().close();
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//				}
//				this.isExistsOpeningOperate = false;
//				current.set(null);
//			}
//			for (int i = 0; i < this.executorList.size(); i++) {
//				Executor executor = (Executor) this.executorList.get(i);
//				executor.execute();
//			}
//		}
//		return true;
//	}
//
//	public void rollback() {
//		if (this.dataAccess != null) {
////				this.dataAccess.rollback();
//				this.isExistsOpeningOperate = false;
//			try {
//				this.conn.isBlockingTransactionStarted = false;
//				this.dataAccess.getConnection().close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			current.set(null);
//		}
//	}
//
//	private void bindTransactionToThread() {
//		Object obj = current.get();
//		if (obj == null)
//			current.set(this);
//		else
//			throw new RuntimeException("One thread cann't have two BlockingTransaction!");
//	}
//
//	public static void clearTransactionBinding() {
//		Object obj = current.get();
//		if (obj == null) {
//			return;
//		}
//		BlockingTransaction bt = (BlockingTransaction) obj;
//		if (bt.isExistsOpeningOperate) {
//			bt.rollback();
//		}
//		current.set(null);
//	}
//
//	public static Connection getCurrentThreadConnection() {
//		Object obj = current.get();
//		if (obj == null) {
//			return null;
//		}
//		BlockingTransaction bt = (BlockingTransaction) obj;
//		if ((bt.dataAccess == null) || (bt.dataAccess.getConnection() == null)) {
//			return null;
//		}
//		return bt.dataAccess.getConnection();
//	}
//}