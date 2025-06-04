package com.arkxos.framework.data.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.arkxos.framework.data.db.connection.Connection;


/**
 * @class org.ark.framework.orm.query.ICallbackStatement
 * Create on May 14, 2010 1:41:05 PM
 * 
 * @author XDarkness
 * @version 1.0
 */
public interface ICallbackStatement {

	Object execute(Connection connection, PreparedStatement stmt, ResultSet rs) throws SQLException;
}
