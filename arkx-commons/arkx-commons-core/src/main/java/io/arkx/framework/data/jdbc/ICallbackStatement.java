package io.arkx.framework.data.jdbc;

import io.arkx.framework.data.db.connection.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


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
