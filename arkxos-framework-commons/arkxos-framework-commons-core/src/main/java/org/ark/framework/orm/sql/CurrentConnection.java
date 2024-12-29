//package org.ark.framework.orm.sql;
//
//import java.sql.SQLException;
//
//import com.arkxos.framework.data.db.connection.Connection;
//import com.arkxos.framework.data.db.connection.ConnectionPoolManager;
//
//
///**
// * @class org.ark.framework.orm.sql.BlockingTransaction
// * 
// * @author Darkness
// * @date 2013-1-31 上午11:44:35
// * @version V1.0
// */
//public class CurrentConnection extends Transaction {
//
//	private static ThreadLocal<Connection> current = new ThreadLocal<>();
//
//	public static void bindTransactionToThread() {
//		Object obj = current.get();
//		if (obj == null) {
//			Connection conn = ConnectionPoolManager.getConnection();
//			try {
//				conn.setAutoCommit(false);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			current.set(conn);
//		}
//	}
//
//	public static void clearTransactionBinding() {
//		Connection connection = current.get();
//		if (connection == null) {
//			return;
//		}
//		try {
//			connection.commit();
////			connection.closeCurrentConnection();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		current.set(null);
//	}
//	
//	public static void clearTransactionBindingWithCommit() {
//		Connection connection = current.get();
//		if (connection == null) {
//			return;
//		}
////		try {
//////			connection.closeCurrentConnection();
////		} catch (SQLException e) {
////			e.printStackTrace();
////		}
//		current.set(null);
//	}
//
//	public static Connection getCurrentThreadConnection() {
//		return current.get();
//	}
//}