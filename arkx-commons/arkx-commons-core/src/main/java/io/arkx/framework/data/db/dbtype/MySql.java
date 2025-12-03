package io.arkx.framework.data.db.dbtype;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.arkx.framework.Config;
import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.data.db.command.*;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionConfig;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;

import com.sun.star.uno.RuntimeException;

/**
 * MySQL数据库
 *
 * @author Darkness
 * @date 2011-12-10 下午04:57:55
 * @version V1.0
 */
public class MySql extends AbstractDBType {

    public final static String ID = "MYSQL";

    @Override
    public String getExtendItemID() {
        return ID;
    }

    @Override
    public String getExtendItemName() {
        return "Mysql";
    }

    @Override
    public boolean isFullSupport() {
        return true;
    }

    @Override
    public String getJdbcUrl(ConnectionConfig dcc) {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:mysql://");
        sb.append(dcc.DBServerAddress);
        sb.append(":");
        sb.append(dcc.DBPort);
        sb.append("/");
        sb.append(dcc.DBName);
        sb.append("?serverTimezone=GMT%2B8&useSSL=true");
        return sb.toString();
    }

    @Override
    public void afterConnectionCreate(Connection conn) {
        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String charset = conn.getDBConfig().Charset;
            if (ObjectUtil.empty(charset)) {
                charset = Config.getGlobalCharset();
            }
            stmt.execute("SET NAMES '" + charset.replaceAll("\\-", "").toLowerCase() + "'");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getDefaultPort() {
        return 3306;
    }

    @Override
    public String getPKNameFragment(String table) {
        return "primary key";
    }

    @Override
    public String toSQLType(DataTypes columnType, int length, int precision) {
        String type = null;
        if (columnType == DataTypes.BIGDECIMAL) {
            type = "double";
        } else if (columnType == DataTypes.BLOB) {
            type = "longblob";
        } else if (columnType == DataTypes.DATETIME) {
            type = "datetime";
        } else if (columnType == DataTypes.DECIMAL) {
            type = "decimal";
        } else if (columnType == DataTypes.DOUBLE) {
            type = "double";
        } else if (columnType == DataTypes.FLOAT) {
            type = "float";
        } else if (columnType == DataTypes.INTEGER) {
            type = "int";
        } else if (columnType == DataTypes.LONG) {
            type = "bigint";
        } else if (columnType == DataTypes.SMALLINT) {
            type = "int";
        } else if (columnType == DataTypes.STRING) {
            type = "varchar";
        } else if (columnType == DataTypes.CLOB) {
            type = "mediumtext";
        }
        if (ObjectUtil.empty(type)) {
            throw new RuntimeException("Unknown DBType " + getExtendItemID() + " or DataType" + columnType);
        }
        if (length == 0 && columnType == DataTypes.STRING) {
            throw new RuntimeException("varchar's length can't be empty!");
        }
        return type + getFieldExtDesc(length, precision);
    }

    @Override
    public String getPagedSQL(String sql, int pageSize, int pageIndex, int connectionId) {
        StringBuffer sb = new StringBuffer();
        int start = pageIndex * pageSize;
        int end = (pageIndex + 1) * pageSize;
        sb.append(sql);
        sb.append(" limit " + start + "," + pageSize);
        return sb.toString();
    }

    @Override
    public String[] toSQLArray(CreateTableCommand c) {
        String[] arr = c.getDefaultSQLArray(ID);
        arr[0] = arr[0] + " engine=innodb";// 指定表引擎为innodb
        return arr;
    }

    @Override
    public String[] toSQLArray(AdvanceChangeColumnCommand c) {
        String sql = "alter table " + c.Table + " modify column " + c.Column + " "
                + toSQLType(DataTypes.valueOf(c.DataType), c.Length, c.Precision);
        if (c.Mandatory) {
            sql += " not null";
        }
        return new String[]{sql};
    }

    @Override
    public String[] toSQLArray(DropTableCommand c) {
        String sql = "drop table if exists " + c.Table;
        return new String[]{sql};
    }

    @Override
    public String[] toSQLArray(DropIndexCommand c) {
        return new String[]{"alter table " + c.Table + " drop index " + c.Name};
    }

    @Override
    public String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public String getSQLSperator() {
        return ";\n";
    }

    @Override
    public String[] toSQLArray(RenameColumnCommand c) {
        return new String[]{"alter table " + c.Table + " change " + c.Column + " " + c.NewColumn + " "
                + toSQLType(DataTypes.valueOf(c.DataType), c.Length, c.Precision)};
    }

    @Override
    public String getForUpdate() {
        return " for update";
    }

    @Override
    public boolean isTableExist(String databaseName, String tableName) {

        String sql = "SELECT count(1) FROM information_schema.tables t WHERE LCASE(t.table_name) = LCASE(?) AND t.table_schema = '"
                + databaseName + "'";

        return getSession().createQuery(sql, tableName).executeInt() > 0;
    }

    public static Session getSession() {
        return SessionFactory.currentSession();
    }

}
