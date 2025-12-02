package io.arkx.framework.data.jdbc;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.ark.framework.orm.sql.DBUtil;
import org.ark.framework.orm.sql.LobUtil;

import io.arkx.framework.Config;
import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.util.StringUtil;

/**
 * @class org.ark.framework.orm.query.ResultDataTable
 * @author Darkness
 * @date 2012-11-19 下午08:34:43
 * @version V1.0
 */
public class ResultDataTable extends DataTable {

    private static final long serialVersionUID = 1L;

    public ResultDataTable(ResultSet rs) {
        this(rs, Integer.MAX_VALUE, 0, false);
    }

    public ResultDataTable(ResultSet rs, boolean latin1Flag) {
        this(rs, Integer.MAX_VALUE, 0, latin1Flag);
    }

    public ResultDataTable(ResultSet rs, int pageSize, int pageIndex) {
        this(rs, pageSize, pageIndex, false);
    }

    public ResultDataTable(ResultSet rs, int pageSize, int pageIndex, boolean latin1Flag) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            DataColumn[] types = new DataColumn[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                String name = rsmd.getColumnLabel(i);
                boolean b = rsmd.isNullable(i) == 1;
                DataColumn dc = new DataColumn();
                dc.setAllowNull(b);
                dc.setColumnName(name);

                int dataType = rsmd.getColumnType(i);
                if ((dataType == 1) || (dataType == 12)) {
                    dc.setColumnType(1);
                } else if ((dataType == 93) || (dataType == 91)) {
                    dc.setColumnType(12);
                } else if (dataType == 3) {
                    dc.setColumnType(4);
                } else if ((dataType == 8) || (dataType == 7)) {
                    dc.setColumnType(6);
                } else if (dataType == 6) {
                    dc.setColumnType(5);
                } else if (dataType == 4) {
                    dc.setColumnType(8);
                } else if ((dataType == 5) || (dataType == -6)) {
                    dc.setColumnType(9);
                } else if (dataType == -7) {
                    dc.setColumnType(11);
                } else if (dataType == -5) {
                    dc.setColumnType(7);
                } else if ((dataType == 2004) || (dataType == -4)) {
                    dc.setColumnType(2);
                } else if ((dataType == 2005) || (dataType == -1)) {
                    dc.setColumnType(10);
                } else if (dataType == 2) {
                    int dataScale = rsmd.getScale(i);
                    int dataPrecision = rsmd.getPrecision(i);
                    if (dataScale == 0) {
                        if (dataPrecision == 0)
                            dc.setColumnType(3);
                        else
                            dc.setColumnType(7);
                    } else
                        dc.setColumnType(3);
                } else {
                    dc.setColumnType(1);
                }
                types[(i - 1)] = dc;
            }

            this.columns = types;
            // renameAmbiguousColumns(this.columns);
            renameAmbiguousColumns();

            ArrayList<DataRow> list = new ArrayList<DataRow>();
            int index = 0;
            int begin = pageIndex * pageSize;
            int end = (pageIndex + 1) * pageSize;
            while (rs.next()) {
                if (index >= end) {
                    break;
                }
                if (index >= begin) {
                    Object[] t = new Object[columnCount];
                    for (int j = 1; j <= columnCount; j++) {
                        if (this.columns[(j - 1)].getColumnType().code() == 10) {
                            String str = LobUtil.clobToString(rs.getClob(j));
                            if ((latin1Flag) && (StringUtil.isNotEmpty(str))) {
                                try {
                                    str = new String(str.getBytes("ISO-8859-1"), Config.getGlobalCharset());
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (" ".equals(str)) {
                                str = "";
                            }
                            t[(j - 1)] = str;
                        } else if (this.columns[(j - 1)].getColumnType().code() == 2) {
                            t[(j - 1)] = LobUtil.blobToBytes(rs.getBlob(j));
                        } else if (this.columns[(j - 1)].getColumnType().code() == 12) {
                            Object obj = rs.getObject(j);
                            if ((obj instanceof Date))
                                t[(j - 1)] = obj;
                            else
                                t[(j - 1)] = DBUtil.getOracleTimestamp(obj);
                        } else if (this.columns[(j - 1)].getColumnType().code() == 11) {
                            t[(j - 1)] = (("true".equals(rs.getString(j))) || ("1".equals(rs.getString(j)))
                                    ? "1"
                                    : "0");
                        } else if (this.columns[(j - 1)].getColumnType().code() == 1) {
                            String str = rs.getString(j);
                            if ((latin1Flag) && (StringUtil.isNotEmpty(str))) {
                                try {
                                    str = new String(str.getBytes("ISO-8859-1"), Config.getGlobalCharset());
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            t[(j - 1)] = str;
                        } else {
                            t[(j - 1)] = rs.getObject(j);
                        }
                    }
                    DataRow tmpRow = new DataRow(this, t);
                    list.add(tmpRow);
                }
                index++;
            }
            this.rows = list;// new DataRow[list.size()];
            // list.toArray(this.rows);
            // logger.error(this.rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
