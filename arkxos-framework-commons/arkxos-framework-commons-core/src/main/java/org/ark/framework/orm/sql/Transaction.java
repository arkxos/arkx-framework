//package org.ark.framework.orm.sql;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.ark.framework.collection.Executor;
//import org.ark.framework.orm.Schema;
//import org.ark.framework.orm.SchemaSet;
//import com.arkxos.framework.commons.util.StringUtil;
//
//import com.arkxos.framework.data.db.connection.ConnectionPoolManager;
//import com.arkxos.framework.data.jdbc.Entity;
//import com.arkxos.framework.data.jdbc.JdbcTemplate;
//import com.arkxos.framework.data.jdbc.Query;
//import com.arkxos.framework.data.jdbc.Session;
//import com.arkxos.framework.data.jdbc.SessionFactory;
//
//
///**
// * @class org.ark.framework.orm.sql.Transaction
// * @author Darkness
// * @date 2012-3-8 下午1:57:38
// * @version V1.0
// */
//public class Transaction {
//	
//	public static final int INSERT = 1;
//	public static final int UPDATE = 2;
//	public static final int DELETE = 3;
//	public static final int BACKUP = 4;
//	public static final int DELETE_AND_BACKUP = 5;
//	public static final int DELETE_AND_INSERT = 6;
//	public static final int SQL = 7;
//	protected boolean outerConnFlag = false;
//	protected JdbcTemplate dataAccess;
//	protected ArrayList<Object> list = new ArrayList<Object>();
//	protected String backupOperator;
//	protected String backupMemo;
//	protected String exceptionMessage;
//	protected ArrayList<Executor> executorList = new ArrayList<Executor>(4);
//	protected String _poolName;
//
//	public Transaction() {
//	}
//
//	public Transaction(String poolName) {
//		this._poolName = poolName;
//	}
//
//	private String getPoolName() {
//		if(StringUtil.isEmpty(_poolName)) {
//			if(DBContext.getCurrentContext() != null)
//			_poolName = DBContext.getCurrentContext().getPoolName();
//		}
//		return _poolName;
//	}
//	public void setDataAccess(JdbcTemplate dAccess) {
//		this.dataAccess = dAccess;
//		this.outerConnFlag = true;
//	}
//
//	public void add(Query qb) {
//		this.list.add(new Object[] { qb, new Integer(7) });
//	}
//
//	public void add(Schema schema, int type) {
//		this.list.add(new Object[] { schema, new Integer(type) });
//	}
//	
//	public void add(Entity entity, int type) {
//		this.list.add(new Object[] { entity, new Integer(type) });
//	}
//
//	public void add(SchemaSet<?> set, int type) {
//		this.list.add(new Object[] { set, new Integer(type) });
//	}
//	
//	public void add(List<?> set, int type) {
//		this.list.add(new Object[] { set, new Integer(type) });
//	}
//
//	public void insert(Schema schema) {
//		add(schema, INSERT);
//	}
//	
//	public void insert(Entity schema) {
//		add(schema, INSERT);
//	}
//
//	public void insert(List<?> set) {
//		add(set, INSERT);
//	}
//	
//	public void insert(SchemaSet<?> set) {
//		add(set, INSERT);
//	}
//
//	public void update(Schema schema) {
//		add(schema, UPDATE);
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
//	public void update(SchemaSet<?> set) {
//		add(set, UPDATE);
//	}
//	
//	public void delete(Schema schema) {
//		add(schema, DELETE);
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
//	public void delete(SchemaSet<?> set) {
//		add(set, DELETE);
//	}
//
//	public void backup(Schema schema) {
//		add(schema, BACKUP);
//	}
//
//	public void backup(SchemaSet<?> set) {
//		add(set, BACKUP);
//	}
//
//	public void deleteAndBackup(Schema schema) {
//		add(schema, DELETE_AND_BACKUP);
//	}
//
//	public void deleteAndBackup(SchemaSet<?> set) {
//		add(set, DELETE_AND_BACKUP);
//	}
//	
//	public void deleteAndBackup(List<?> set) {
//		add(set, DELETE_AND_BACKUP);
//	}
//	@Deprecated
//	public void deleteAndBackupList(List<?> set) {
//		add(set, DELETE_AND_BACKUP);
//	}
//
//	public void deleteAndInsert(Schema schema) {
//		add(schema, DELETE_AND_INSERT);
//	}
//
//	public void deleteAndInsert(SchemaSet<?> set) {
//		add(set, DELETE_AND_INSERT);
//	}
//
//	public boolean commit() {
//		return commit(true);
//	}
//
//	private Session getSession() {
//		return SessionFactory.currentSession();
//	}
//	
//	public boolean commit(boolean setAutoCommitStatus) {
//		if (!this.outerConnFlag) {
//			this.dataAccess = new JdbcTemplate(ConnectionPoolManager.getConnection(getPoolName()));
//		}
//		boolean NoErrFlag = true;
//		try {
//			if ((!this.outerConnFlag) || (setAutoCommitStatus))
////				this.dataAccess.setAutoCommit(false);
//			for (int i = 0; i < this.list.size(); i++) {
//				Object[] arr = (Object[]) this.list.get(i);
//				Object obj = arr[0];
//				int type = ((Integer) arr[1]).intValue();
//				if (!executeObject(obj, type)) {
//					NoErrFlag = false;
//					return false;
//				}
//			}
////			this.dataAccess.commit();
//			this.list.clear();
//		} catch (Exception e) {
//			e.printStackTrace();
//			this.exceptionMessage = e.getMessage();
//			NoErrFlag = false;
//			return false;
//		} finally {
//			if (!NoErrFlag)
////					this.dataAccess.rollback();
//				if ((!this.outerConnFlag) || (setAutoCommitStatus))
////					this.dataAccess.setAutoCommit(true);
//			if (!this.outerConnFlag) {
////					this.dataAccess.close();
//			}
//		}
//		if (!NoErrFlag)
////				this.dataAccess.rollback();
//			if ((!this.outerConnFlag) || (setAutoCommitStatus))
////				this.dataAccess.setAutoCommit(true);
//		if (!this.outerConnFlag) {
////				this.dataAccess.close();
//		}
//
//		for (int i = 0; i < this.executorList.size(); i++) {
//			Executor executor = (Executor) this.executorList.get(i);
//			executor.execute();
//		}
//		return true;
//	}
//
//	protected boolean executeObject(Object obj, int type) throws SQLException {
//		if ((obj instanceof Query)) {
//			Query qb = (Query) obj;
//			qb.executeNoQuery();
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
//		} else if ((obj instanceof Schema)) {
//			Schema s = (Schema) obj;
//			s.setDataAccess(this.dataAccess);
//			if (type == INSERT) {
//				if (!s.insert())
//					return true;
//			} else if (type == UPDATE) {
//				if (!s.update())
//					return true;
//			} else if (type == DELETE) {
//				if (!s.delete())
//					return true;
//			} else if (type == BACKUP) {
//				if (s.backup(this.backupOperator, this.backupMemo))
//					return true;
//			} else if (type == DELETE_AND_BACKUP) {
//				if (!s.deleteAndBackup(this.backupOperator, this.backupMemo))
//					return true;
//			} else if ((type == DELETE_AND_INSERT) && (!s.deleteAndInsert())) {
//				return true;
//			}
//		} else if ((obj instanceof SchemaSet)) {
//			SchemaSet<?> s = (SchemaSet<?>) obj;
////			s.setDataAccess(this.dataAccess);
//			if (type == INSERT) {
//				if (!s.insert())
//					return true;
//			} else if (type == UPDATE) {
//				if (!s.update())
//					return true;
//			} else if (type == DELETE) {
//				if (!s.delete())
//					return true;
//			} else if (type == BACKUP) {
//				if (!s.backup(this.backupOperator, this.backupMemo))
//					return true;
//			} else if (type == DELETE_AND_BACKUP) {
//				if (!s.deleteAndBackup(this.backupOperator, this.backupMemo))
//					return true;
//			} else if ((type == DELETE_AND_INSERT) && (!s.deleteAndInsert())) {
//				return true;
//			}
//		}else if ((obj instanceof List)) {
//			List<? extends Entity> s = (List<? extends Entity>) obj;
//			//s.setDataAccess(this.dataAccess);
//			if (type == INSERT) {
//				if (getSession().save(s) < 0)
//					return true;
//			} else if (type == UPDATE) {
//				if (getSession().update(s) < 0)
//					return true;
//			} else if (type == DELETE) {
//				if (getSession().delete(s) < 0)
//					return true;
//			} 
////			else if (type == BACKUP) {
////				if (!SchemaRepository.backup(s, this.backupOperator, this.backupMemo))
////					return false;
////			} else if (type == DELETE_AND_BACKUP) {
////				if (!SchemaRepository.deleteAndBackup(s, this.backupOperator, this.backupMemo))
////					return false;
////			} else if ((type == DELETE_AND_INSERT) && (!SchemaRepository.deleteAndInsert(s))) {
////				return false;
////			}
//		}
//
//		return true;
//	}
//
//	public void clear() {
//		this.list.clear();
//	}
//
//	public String getExceptionMessage() {
//		return this.exceptionMessage;
//	}
//
//	public String getBackupMemo() {
//		return this.backupMemo;
//	}
//
//	public void setBackupMemo(String backupMemo) {
//		this.backupMemo = backupMemo;
//	}
//
//	public String getBackupOperator() {
//		return this.backupOperator;
//	}
//
//	public void setBackupOperator(String backupOperator) {
//		this.backupOperator = backupOperator;
//	}
//
//	public ArrayList<Object> getOperateList() {
//		return this.list;
//	}
//
//	public void addExecutor(Executor executor) {
//		this.executorList.add(executor);
//	}
//
//}
