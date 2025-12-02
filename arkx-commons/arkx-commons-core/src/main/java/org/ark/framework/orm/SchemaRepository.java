package org.ark.framework.orm;

import java.util.List;

/**
 * @class org.ark.framework.orm.SchemaRepository
 * @author Darkness
 * @date 2012-10-18 下午04:11:10
 * @version V1.0
 */
public class SchemaRepository {

	// public static boolean backup(List<? extends Schema> schemaList, String
	// backupOperator, String backupMemo) {
	// if (schemaList == null || schemaList.size() == 0) {
	// return true;
	// }
	// backupOperator = StringUtil.isEmpty(backupOperator) ? User.getUserName() :
	// backupOperator;
	// backupOperator = StringUtil.isEmpty(backupOperator) ? "SYSTEM" :
	// backupOperator;
	// String backupSQL = schemaList.get(0).getInsertAllSQL().substring(0,
	// schemaList.get(0).getInsertAllSQL().length() - 1) + ",?,?,?,?)";
	// backupSQL = StringUtil.replaceEx(backupSQL, schemaList.get(0).getTableCode(),
	// "B" + schemaList.get(0).getTableCode());
	// Date current = new Date();
	// int bConnFlag = 0;
	// DataAccess mDataAccess = null;
	// if (bConnFlag == 0) {
	// mDataAccess = new DataAccess();
	// }
	// long start = System.currentTimeMillis();
	// PreparedStatement pstmt = null;
	// try {
	// XConnection conn = mDataAccess.getConnection();
	// boolean autoComit = conn.getAutoCommit();
	// if (bConnFlag == 0) {
	// conn.setAutoCommit(false);
	// }
	// int i = schemaList.get(0).getColumns().length;
	// SchemaColumn sc1 = new SchemaColumn("BackupNo", 1, i, 15, 0, true, true);
	// SchemaColumn sc2 = new SchemaColumn("BackupOperator", 1, i + 1, 200, 0, true,
	// false);
	// SchemaColumn sc3 = new SchemaColumn("BackupTime", 12, i + 2, 0, 0, true,
	// false);
	// SchemaColumn sc4 = new SchemaColumn("BackupMemo", 1, i + 3, 50, 0, false,
	// false);
	// pstmt = conn.prepareStatement(backupSQL, 1003, 1007);
	// for (Schema schema : schemaList) {
	// int j = 0;
	// for (SchemaColumn sc : schema.getColumns()) {
	// if ((sc.isMandatory()) && (schema.getV(sc.getColumnOrder()) == null)) {
	// LogUtil.warn(schema.getTableCode() + "'s mandatory column " +
	// sc.getColumnName() + " can't be null");
	// return false;
	// }
	// Object v = schema.getV(sc.getColumnOrder());
	// SchemaUtil.setParam(sc, pstmt, conn, j++, v);
	// }
	// SchemaUtil.setParam(sc1, pstmt, conn, i, SchemaUtil.getBackupNo());
	// SchemaUtil.setParam(sc2, pstmt, conn, i + 1, backupOperator);
	// SchemaUtil.setParam(sc3, pstmt, conn, i + 2, current);
	// SchemaUtil.setParam(sc4, pstmt, conn, i + 3, backupMemo);
	// pstmt.addBatch();
	// }
	// pstmt.executeBatch();
	// if (bConnFlag == 0) {
	// conn.commit();
	// conn.setAutoCommit(autoComit);
	// }
	// conn.setLastSuccessExecuteTime(System.currentTimeMillis());
	// DataAccess.log(start, backupSQL, null);
	// return true;
	// } catch (Throwable e) {
	// if ((e instanceof BatchUpdateException)) {
	//// LogUtil.warn(toDataTable());
	// }
	// String message = "Error:Set backup from " + schemaList.get(0).getTableCode()
	// + " failed:" + e.getMessage();
	// LogUtil.warn(message);
	// DataAccess.log(start, message, null);
	//
	// throw new RuntimeException(e);
	// } finally {
	// if (pstmt != null) {
	// try {
	// pstmt.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// pstmt = null;
	// }
	// if (bConnFlag == 0)
	// try {
	// mDataAccess.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// public static boolean deleteAndBackup(List s) {
	// return deleteAndBackup(s, "", "");
	// }
	// public static boolean deleteAndBackup(List s, String backupOperator, String
	// backupMemo) {
	// if (ObjectUtil.empty(backupMemo))
	// backupMemo = "Delete";
	// try {
	// backupOperator = ObjectUtil.empty(backupOperator) ? User.getUserName() :
	// backupOperator;
	// backupOperator = ObjectUtil.empty(backupOperator) ? "SYSTEM" :
	// backupOperator;
	// int bConnFlag = 0;
	// if (bConnFlag == 1) {
	// if (!backup(s, backupOperator, backupMemo)) {
	// return true;
	// }
	// delete(s);
	// return true;
	// }
	// DataAccess mDataAccess = new DataAccess();
	// bConnFlag = 1;
	// try {
	// mDataAccess.setAutoCommit(false);
	// backup(s, backupOperator, backupMemo);
	// delete(s);
	// mDataAccess.commit();
	// return true;
	// } catch (Throwable e) {
	// e.printStackTrace();
	// try {
	// mDataAccess.rollback();
	// } catch (SQLException e1) {
	// e1.printStackTrace();
	// }
	// return false;
	// } finally {
	// try {
	// mDataAccess.setAutoCommit(true);
	// mDataAccess.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// mDataAccess = null;
	// bConnFlag = 0;
	// }
	// } catch (Exception e) {throw new RuntimeException(e);
	// }
	// }
	//
	// public static boolean deleteAndInsert(List<? extends Schema> s) {
	// int bConnFlag= 0;
	// if (bConnFlag == 1) {
	// if (!delete(s)) {
	// return false;
	// }
	// return insert(s);
	// }
	// DataAccess mDataAccess = new DataAccess();
	// bConnFlag = 1;
	// try {
	// mDataAccess.setAutoCommit(false);
	// delete(s);
	// insert(s);
	// mDataAccess.commit();
	// return true;
	// } catch (Throwable e) {
	// e.printStackTrace();
	// try {
	// mDataAccess.rollback();
	// } catch (SQLException e1) {
	// e1.printStackTrace();
	// }
	// return false;
	// } finally {
	// try {
	// mDataAccess.setAutoCommit(true);
	// mDataAccess.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// mDataAccess = null;
	// bConnFlag = 0;
	// }
	// }

	public static boolean deleteAndBackup(List<? extends Schema> set) {
		// TODO Auto-generated method stub
		return false;
	}

}
