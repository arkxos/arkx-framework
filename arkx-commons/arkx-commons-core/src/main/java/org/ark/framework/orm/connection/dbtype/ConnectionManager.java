package org.ark.framework.orm.connection.dbtype;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.data.db.connection.ConnectionConfig;
import io.arkx.framework.data.db.connection.ConnectionPool;

/**
 * @class org.ark.framework.orm.connection.dbtype.ConnectionManager
 * @author Darkness
 * @date 2012-4-1 上午10:08:23
 * @version V1.0
 */
public class ConnectionManager {

    private Connection connection;

    public ConnectionManager(String dbType, String server, String port, String userName, String password,
            String dbName) {

        ConnectionConfig config = new ConnectionConfig();
        config.setDatabaseName(dbName);
        config.setUserName(userName);
        config.setPassword(password);
        config.setDatabaseType(dbType);
        config.setHost(server);
        config.setPort(port);
        config.setPoolName("bbsPool");

        // dbType.setDbInfo(config);

        try {
            connection = ConnectionPool.createConnection(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        } // . dbType.getConnection(server, port, userName, password, dbName);
    }

    /**
     * 取得当前连接数据库指定表的字段信息。
     *
     * @param tableName
     *            表名称
     * @return 字段信息列表
     * @exception SQLException
     *                Description of the Exception
     * @throws Exception
     *             失败时抛出
     */
    public List<TableField> getFieldList(String tableName) {

        List<TableField> result = new ArrayList<TableField>();

        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM " + tableName);
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            for (int i = 0; i < meta.getColumnCount(); i++) {
                TableField field = new TableField();
                int cursor = i + 1;
                field.name = meta.getColumnName(cursor);
                field.type = meta.getColumnType(cursor);
                field.size = meta.getColumnDisplaySize(cursor);
                field.scale = meta.getScale(cursor);
                field.isNullable = meta.isNullable(cursor);
                field.precision = meta.getPrecision(cursor);

                result.add(field);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static class TableField {

        public int precision;
        public int isNullable;
        public int scale;
        public int size;
        public int type;
        public String name;

    }

    /**
     * 获取数据库的所有表名
     *
     * @author Darkness
     * @date 2012-4-1 上午10:14:38
     * @version V1.0
     */
    public List<String> getAllTableNames() {

        List<String> tableNames = new ArrayList<String>();

        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString(3);
                System.out.println("表名：" + tableName);
                tableNames.add(tableName);
                System.out.println("表所属用户名：" + rs.getString(2));
                System.out.println("------------------------------");
            }
        } catch (Exception e) {
        }

        return tableNames;
    }

    public static void main(String[] args) {
        // ConnectionManager manager = new ConnectionManager(DatabaseService.MYSQL,
        // "192.168.1.117", "3306", "root", "darkness", "ark");
        // List<String> tables = manager.getAllTableNames();
        // for (Object table : tables) {
        // System.out.println(table);
        // }
        // List<TableField> fileds = manager.getFieldList("Z_CAR");

    }
}
