package org.ark.framework.orm;

//public class BSchema<T extends Schema> extends Schema {
//	private static final long serialVersionUID = 1L;
//	private Class<T> clazz;
//	private T schema;
//	private String BackupNo;
//	private String BackupOperator;
//	private Date BackupTime;
//	private String BackupMemo;
//	protected static final String _InsertAllSQL = "insert into pt_org__branch values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//	protected static final String _UpdateAllSQL = "update pt_org__branch set BranchInnerCode=?,BranchCode=?,ParentInnerCode=?,Type=?,OrderFlag=?,Name=?,TreeLevel=?,IsLeaf=?,Phone=?,Fax=?,Manager=?,Leader1=?,Leader2=?,Prop1=?,Prop2=?,Prop3=?,Prop4=?,Memo=?,AddTime=?,AddUser=?,ModifyTime=?,ModifyUser=? where BranchInnerCode=?";
//	protected static final String _DeleteSQL = "delete from pt_org__branch  where BranchInnerCode=?";
//	protected static final String _FillAllSQL = "select * from pt_org__branch  where BranchInnerCode=?";
//
//	public BSchema(Class<T> c) {
//		try {
//			this.clazz = c;
//			this.schema = ((T) this.clazz.newInstance());
//			this.Columns = SchemaUtil.addBackupColumn(this.schema.Columns);
//			this.TableCode = ("B" + this.schema.TableCode);
//			this.NameSpace = this.schema.NameSpace;
//
//			this.InsertAllSQL = this.schema.InsertAllSQL;
//			int index = this.InsertAllSQL.indexOf(this.schema.TableCode);
//			StringBuilder sb = new StringBuilder();
//			sb.append(this.InsertAllSQL.substring(0, index)).append(this.TableCode);
//			sb.append(this.InsertAllSQL.substring(index + this.schema.TableCode.length(), this.InsertAllSQL.length() - 1));
//			sb.append(",?,?,?,?)");
//			this.InsertAllSQL = sb.toString();
//
//			this.UpdateAllSQL = this.schema.UpdateAllSQL;
//
//			StringBuilder pk = new StringBuilder();
//			pk.append(this.UpdateAllSQL.substring(this.UpdateAllSQL.lastIndexOf(" where ")));
//			pk.append(" and BackupNo=?");
//
//			sb = new StringBuilder();
//			index = this.UpdateAllSQL.indexOf(this.schema.TableCode);
//			sb.append(this.UpdateAllSQL.substring(0, index)).append(this.TableCode);
//			sb.append(this.UpdateAllSQL.substring(index + this.schema.TableCode.length(), this.UpdateAllSQL.lastIndexOf(" where ")));
//			sb.append(",BackupNo=?");
//			sb.append(",BackupOperator=?");
//			sb.append(",BackupTime=?");
//			sb.append(",BackupMemo=?");
//			sb.append(pk);
//			this.UpdateAllSQL = sb.toString();
//
//			this.DeleteSQL = this.schema.DeleteSQL;
//			sb = new StringBuilder();
//			index = this.DeleteSQL.indexOf(this.schema.TableCode);
//			sb.append(this.DeleteSQL.substring(0, index)).append(this.TableCode);
//			sb.append(this.DeleteSQL.substring(index + this.schema.TableCode.length(), this.DeleteSQL.lastIndexOf(" where ")));
//			sb.append(pk);
//			this.DeleteSQL = sb.toString();
//
//			this.FillAllSQL = this.schema.FillAllSQL;
//			sb = new StringBuilder();
//			index = this.FillAllSQL.indexOf(this.schema.TableCode);
//			sb.append(this.FillAllSQL.substring(0, index)).append(this.TableCode);
//			sb.append(this.FillAllSQL.substring(index + this.schema.TableCode.length(), this.FillAllSQL.lastIndexOf(" where ")));
//			sb.append(pk);
//			this.FillAllSQL = sb.toString();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public T getZSchema() {
//		return this.schema;
//	}
//
//	public void setV(int i, Object v) {
//		if (i < this.Columns.length - 4)
//			this.schema.setV(i, v);
//		else if (i == this.Columns.length - 4)
//			this.BackupNo = ((String) v);
//		else if (i == this.Columns.length - 3)
//			this.BackupOperator = ((String) v);
//		else if (i == this.Columns.length - 2)
//			this.BackupTime = ((Date) v);
//		else if (i == this.Columns.length - 1)
//			this.BackupMemo = ((String) v);
//	}
//
//	public Object getV(int i) {
//		if (i < this.Columns.length - 4)
//			return this.schema.getV(i);
//		if (i == this.Columns.length - 4)
//			return this.BackupNo;
//		if (i == this.Columns.length - 3)
//			return this.BackupOperator;
//		if (i == this.Columns.length - 2)
//			return this.BackupTime;
//		if (i == this.Columns.length - 1) {
//			return this.BackupMemo;
//		}
//		return null;
//	}
//
//	public Schema newSchema() {
//		return new BSchema(this.clazz);
//	}
//
//	public SchemaSet<?> newSet() {
//		return new BSet(this);
//	}
//
//	public BSet<BSchema<T>> query() {
//		return query(null, -1, -1);
//	}
//
//	public BSet<BSchema<T>> query(QueryBuilder qb) {
//		return query(qb, -1, -1);
//	}
//
//	public BSet<BSchema<T>> query(int pageSize, int pageIndex) {
//		return query(null, pageSize, pageIndex);
//	}
//
//	public BSet<BSchema<T>> query(QueryBuilder qb, int pageSize, int pageIndex) {
//		return (BSet) querySet(qb, pageSize, pageIndex);
//	}
//
//	public String getBackupNo() {
//		return this.BackupNo;
//	}
//
//	public void setBackupNo(String backupNo) {
//		if (backupNo != null) {
//			int len = StringUtil.lengthEx(backupNo, Config.getGlobalCharset().equals("UTF-8"));
//			if (len > 15) {
//				throw new RuntimeException("Data is too long, max is 15,actual is " + len + ":" + backupNo);
//			}
//		}
//		this.BackupNo = backupNo;
//	}
//
//	public String getBackupOperator() {
//		return this.BackupOperator;
//	}
//
//	public void setBackupOperator(String backupOperator) {
//		if (backupOperator != null) {
//			int len = StringUtil.lengthEx(backupOperator, Config.getGlobalCharset().equals("UTF-8"));
//			if (len > 50) {
//				throw new RuntimeException("Data is too long, max is 50,actual is " + len + ":" + backupOperator);
//			}
//		}
//		this.BackupOperator = backupOperator;
//	}
//
//	public Date getBackupTime() {
//		return this.BackupTime;
//	}
//
//	public void setBackupTime(Date backupTime) {
//		this.BackupTime = backupTime;
//	}
//
//	public void setBackupTime(String backupTime) {
//		if (backupTime == null) {
//			this.BackupTime = null;
//			return;
//		}
//		this.BackupTime = DateUtil.parseDateTime(backupTime);
//	}
//
//	public String getBackupMemo() {
//		return this.BackupMemo;
//	}
//
//	public void setBackupMemo(String backupMemo) {
//		if (backupMemo != null) {
//			int len = StringUtil.lengthEx(backupMemo, Config.getGlobalCharset().equals("UTF-8"));
//			if (len > 200) {
//				throw new RuntimeException("Data is too long, max is 200,actual is " + len + ":" + backupMemo);
//			}
//		}
//		this.BackupMemo = backupMemo;
//	}
//}