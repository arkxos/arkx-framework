package io.arkx.framework.data.simplejdbc.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * mysql分页处理器
 *
 * @author Darkness
 * @date 2013-4-15 下午04:33:55
 * @version V1.0
 */
public class MySqlPagedHandler implements IPagedHandler {

    @Override
    public String getSupportDatabaseType() {
        return "mysql";
    }

    @Override
    public PreparedStatement createPagedStatement(Connection conn, String sql, int start, int limit)
            throws SQLException {
        String pagedSql = sql + " limit ?, ?";

        // statement用来执行SQL语句
        PreparedStatement statement = conn.prepareStatement(pagedSql);
        statement.setInt(1, start);
        statement.setInt(2, limit);

        return statement;
    }

}
