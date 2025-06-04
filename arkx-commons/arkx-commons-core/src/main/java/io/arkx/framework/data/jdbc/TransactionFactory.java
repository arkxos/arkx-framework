package io.arkx.framework.data.jdbc;

import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionPoolManager;

public class TransactionFactory {

	private static ThreadLocal<Transaction> current = new ThreadLocal<>();
	private static ReentrantLock lock = new ReentrantLock();

	public static Transaction create(String poolName) {
		Connection connection = ConnectionPoolManager.getConnection(poolName, false,
				false);
		try {
			connection.setReadOnly(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new Transaction(connection);
	}

	public static Transaction createReadOnly(String poolName) {
		Connection connection = ConnectionPoolManager.getConnection(poolName, false,
				false);

		try {
			connection.setReadOnly(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new Transaction(connection);
	}

	public static void setCurrentTransaction(Transaction transaction) {
		current.set(transaction);
	}

	public static Transaction getCurrentTransaction() {
		return current.get();
	}
}
