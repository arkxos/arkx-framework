package com.rapidark.framework.data.jdbc;

import java.sql.SQLException;

import com.rapidark.framework.data.db.connection.Connection;
import com.rapidark.framework.data.db.exception.CloseException;
import com.rapidark.framework.data.db.exception.CommitException;
import com.rapidark.framework.data.db.exception.RollbackException;
import com.rapidark.framework.data.db.exception.SetAutoCommitException;

public class Transaction {

	private Connection connection;
	
	public Transaction(Connection connection) {
		this.connection = connection;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void begin() {
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new SetAutoCommitException(e);
		}
	}
	

	/**
	 * 提交事务
	 */
	public void commit() {
		try {
			connection.commit();
		} catch (Exception e) {
			rollback();

			throw new CommitException(e);
		} finally {
			close();
		}
	}
	
	public void rollback() {
		try {
			connection.rollback();
		} catch (SQLException e) {
			throw new RollbackException(e);
		}
	}
	
	public void close() {
		try {
			if(!connection.isClosed()){
				connection.close();
			}
		} catch (SQLException e) {
			throw new CloseException(e);
		}
	}
}
