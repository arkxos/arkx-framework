package org.ark.framework.orm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import io.arkx.framework.commons.collection.CaseIgnoreMapx;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.data.db.dbtype.DBTypeService;
import io.arkx.framework.data.db.dbtype.IDBType;

/**
 * @class org.ark.framework.orm.TableUpdater
 * @author Darkness
 * @date 2012-3-8 下午2:01:59
 * @version V1.0
 */
public class TableUpdater {
    public static String toOracleSQL(String fileName) {
        UpdateSQLParser usp = new UpdateSQLParser(fileName);
        String[] arr = usp.convertToSQLArray(DBTypeService.getInstance().get("ORACLE"));
        StringBuilder sb = new StringBuilder();
        sb.append("alter session set nls_date_format = 'YYYY-MM-DD HH24:MI:SS';\n");
        for (int i = 0; i < arr.length; i++) {
            String line = arr[i];
            if ((StringUtil.isEmpty(line)) || (line.startsWith("/*"))) {
                sb.append(line + "\n");
            } else
                sb.append(arr[i] + ";\n");
        }
        return sb.toString();
    }

    public static String toSQLServerSQL(String fileName) {
        UpdateSQLParser usp = new UpdateSQLParser(fileName);
        String[] arr = usp.convertToSQLArray(DBTypeService.getInstance().get("MSSQL"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            String line = arr[i];
            if ((StringUtil.isEmpty(line)) || (line.startsWith("/*"))) {
                sb.append(line + "\n");
            } else
                sb.append(arr[i] + "\ngo\n");
        }
        return sb.toString();
    }

    public static String toMysqlrSQL(String fileName) {
        UpdateSQLParser usp = new UpdateSQLParser(fileName);
        String[] arr = usp.convertToSQLArray(DBTypeService.getInstance().get("MYSQL"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            String line = arr[i];
            if ((StringUtil.isEmpty(line)) || (line.startsWith("/*"))) {
                sb.append(line + "\n");
            } else
                sb.append(arr[i] + ";\n");
        }
        return sb.toString();
    }

    public static String toDB2SQL(String fileName) {
        UpdateSQLParser usp = new UpdateSQLParser(fileName);
        String[] arr = usp.convertToSQLArray(DBTypeService.getInstance().get("DB2"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            String line = arr[i];
            if ((StringUtil.isEmpty(line)) || (line.startsWith("/*"))) {
                sb.append(line + "\n");
            } else
                sb.append(arr[i] + ";\n");
        }
        return sb.toString();
    }

    public static void addIndexes(StringBuilder sb, String tableName, IDBType dbType) {
        Schema schema = SchemaUtil.findSchema(tableName);
        String indexInfoStr = "";// schema.getIndexInfo();
        if (ObjectUtil.notEmpty(indexInfoStr)) {
            String[] indexArray = indexInfoStr.split(";");
            for (int i = 0; i < indexArray.length; i++) {
                String indexInfo = indexArray[i];
                String sql = createIndex(tableName, indexInfo, i, dbType);
                if (StringUtil.isNotEmpty(sql))
                    sb.append(sql + "\n");
            }
        }
    }

    public static void addPrimaryKey(StringBuilder sb, String tableName, IDBType dbType) {
        try {
            SchemaColumn[] scs = SchemaUtil.findSchema(tableName).Columns;
            List pkList = SchemaUtil.getPrimaryKeyColumnsList(scs);
            AlterKeyInfo info = new AlterKeyInfo();
            info.TableName = tableName;
            info.NewKeys = StringUtil.join(pkList);
            String[] arr = info.toSQLArray(dbType);
            for (int i = 0; i < arr.length; i++)
                sb.append(arr[i] + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String createIndex(String tableName, String indexInfo, int i, IDBType dbType) {
        int p1 = indexInfo.indexOf(":");
        if (p1 < 0) {
            return null;
        }
        String name = indexInfo.substring(0, p1);
        indexInfo = indexInfo.substring(p1 + 1, indexInfo.length());
        String[] cs = indexInfo.split(",");
        StringBuffer sb = new StringBuffer();
        if (((dbType.getExtendItemID().equalsIgnoreCase("ORACLE"))
                || (dbType.getExtendItemID().equalsIgnoreCase("DB2"))) && (name.length() > 15)) {
            name = name.substring(0, 15);
        }

        sb.append("create index " + name + " on " + tableName + " (");
        boolean first = true;
        for (int j = 0; j < cs.length; j++) {
            if (StringUtil.isEmpty(cs[j])) {
                continue;
            }
            if (!first) {
                sb.append(",");
            }
            sb.append(cs[j]);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }

    public static class AlterKeyInfo extends TableUpdateInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        public String TableName;
        public String NewKeys;
        public boolean DropFlag;

        public String[] toSQLArray(IDBType dbType) {
            if (this.DropFlag) {
                String sql = "alter table " + this.TableName + " drop primary key";
                if ("MSSQL".equalsIgnoreCase(dbType.getExtendItemID())) {
                    sql = "alter table " + this.TableName + " drop constraint PK_" + this.TableName;
                }
                return new String[]{sql};
            }
            String sql = "alter table " + this.TableName + " add primary key (" + this.NewKeys + ")";
            if ("MSSQL".equalsIgnoreCase(dbType.getExtendItemID())) {
                sql = "alter table " + this.TableName + " add constraint PK_" + this.TableName
                        + " primary key  NONCLUSTERED(" + this.NewKeys + ")";
            }
            return new String[]{sql};
        }
    }

    public static class AlterTableInfo extends TableUpdateInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        public String TableName;
        public String Action;
        public int ColumnType;
        public String OldColumnName;
        public String NewColumnName;
        public String AfterColumn;
        public int Length;
        public int Precision;
        public boolean Mandatory;

        public String[] toSQLArray(IDBType dbType) {
            return toSQLArray(dbType, new ArrayList(), new CaseIgnoreMapx());
        }

        public String[] toSQLArray(IDBType dbType, ArrayList<TableUpdateInfo> togetherColumns,
                Mapx<String, String> exclusiveMapx) {
            exclusiveMapx.put(this.OldColumnName, "1");
            if (!togetherColumns.contains(this)) {
                togetherColumns.add(0, this);
            }
            if (dbType.getExtendItemID().equalsIgnoreCase("MYSQL")) {
                if (this.Action.equalsIgnoreCase("add")) {
                    String[] arr = new String[togetherColumns.size()];
                    for (int i = 0; i < togetherColumns.size(); i++) {
                        AlterTableInfo info = (AlterTableInfo) togetherColumns.get(i);
                        String sql = "alter table " + info.TableName + " add column " + info.OldColumnName + " "
                                + TableCreator.toSQLType(info.ColumnType, info.Length, info.Precision, dbType);
                        if (info.Mandatory) {
                            sql = sql + " not null";
                        }
                        if (StringUtil.isNotEmpty(info.AfterColumn)) {
                            sql = sql + " after " + info.AfterColumn;
                        }
                        arr[i] = sql;
                    }
                    return arr;
                }
                if (this.Action.equalsIgnoreCase("drop")) {
                    String sql = "alter table " + this.TableName + " drop column " + this.OldColumnName;
                    return new String[]{sql};
                }
                if ((this.Action.equalsIgnoreCase("modify")) || (this.Action.equalsIgnoreCase("change"))) {
                    String sql = "alter table " + this.TableName + " change column " + this.OldColumnName;
                    if (StringUtil.isNotEmpty(this.NewColumnName))
                        sql = sql + " " + this.NewColumnName;
                    else {
                        sql = sql + " " + this.OldColumnName;
                    }
                    if (this.ColumnType == -1) {
                        System.out.println(1);
                    }
                    sql = sql + " " + TableCreator.toSQLType(this.ColumnType, this.Length, this.Precision, dbType);
                    if (this.Mandatory) {
                        sql = sql + " not null";
                    }
                    return new String[]{sql};
                }
            }
            if (dbType.getExtendItemID().equalsIgnoreCase("ORACLE")) {
                if (this.Action.equalsIgnoreCase("add")) {
                    StringBuilder sb = new StringBuilder();

                    if (StringUtil.isNotEmpty(this.AfterColumn)) {
                        Schema schema = SchemaUtil.findSchema(this.TableName);
                        SchemaColumn[] scs = (SchemaColumn[]) schema.Columns.clone();
                        ArrayList list = new ArrayList();
                        for (int i = 0; i < scs.length; i++) {
                            if ((exclusiveMapx == null) || (!exclusiveMapx.containsKey(scs[i].getColumnName()))) {
                                list.add(scs[i].getColumnName());
                            }
                            for (int j = togetherColumns.size() - 1; j >= 0; j--) {
                                AlterTableInfo info = (AlterTableInfo) togetherColumns.get(j);
                                String columnName = info.AfterColumn;
                                if (scs[i].getColumnName().equalsIgnoreCase(columnName)) {
                                    list.add("'0' as " + info.OldColumnName);
                                }
                            }

                        }

                        sb.append("create table " + this.TableName + "_TMP as select " + StringUtil.join(list)
                                + " from " + this.TableName + "\n");
                        for (int j = 0; j < togetherColumns.size(); j++) {
                            AlterTableInfo info = (AlterTableInfo) togetherColumns.get(j);
                            if (this.Mandatory) {
                                if (info.ColumnType == 1) {
                                    sb.append("update " + info.TableName + "_TMP set " + info.OldColumnName + "='0'\n");
                                } else if (info.ColumnType == 12) {
                                    sb.append(
                                            "update " + info.TableName + "_TMP set " + info.OldColumnName + "=null\n");
                                    sb.append("alter table "
                                            + info.TableName + "_TMP modify " + info.OldColumnName + " " + TableCreator
                                                    .toSQLType(info.ColumnType, info.Length, info.Precision, dbType)
                                            + "\n");
                                    sb.append("update " + info.TableName + "_TMP set " + info.OldColumnName
                                            + "='1970-01-01 00:00:00'\n");
                                } else {
                                    sb.append(
                                            "update " + info.TableName + "_TMP set " + info.OldColumnName + "=null\n");
                                    sb.append("alter table "
                                            + info.TableName + "_TMP modify " + info.OldColumnName + " " + TableCreator
                                                    .toSQLType(info.ColumnType, info.Length, info.Precision, dbType)
                                            + "\n");
                                    sb.append("update " + info.TableName + "_TMP set " + info.OldColumnName + "=0\n");
                                }
                            } else
                                sb.append("update " + info.TableName + "_TMP set " + info.OldColumnName + "=null\n");

                            sb.append("alter table " + info.TableName + "_TMP modify " + info.OldColumnName + " "
                                    + TableCreator.toSQLType(info.ColumnType, info.Length, info.Precision, dbType)
                                    + (info.Mandatory ? " not null" : "") + "\n");
                        }
                        sb.append("drop table " + this.TableName + "\n");
                        sb.append("rename " + this.TableName + "_TMP to " + this.TableName + "\n");
                        TableUpdater.addPrimaryKey(sb, this.TableName, dbType);
                        TableUpdater.addIndexes(sb, this.TableName, dbType);
                    } else {
                        for (int j = 0; j < togetherColumns.size(); j++) {
                            AlterTableInfo info = (AlterTableInfo) togetherColumns.get(j);
                            sb.append("alter table " + info.TableName + " add " + info.OldColumnName + " "
                                    + TableCreator.toSQLType(info.ColumnType, info.Length, info.Precision, dbType));
                            if (info.Mandatory) {
                                sb.append(" not null");
                            }
                            sb.append("\\n");
                        }
                    }
                    return sb.toString().split("\\n");
                }
                if (this.Action.equalsIgnoreCase("drop")) {
                    String sql = "alter table " + this.TableName + " drop column " + this.OldColumnName;
                    return new String[]{sql};
                }
                if ((this.Action.equalsIgnoreCase("modify")) || (this.Action.equalsIgnoreCase("change"))) {
                    if ((StringUtil.isNotEmpty(this.NewColumnName))
                            && (!this.NewColumnName.equalsIgnoreCase(this.OldColumnName))) {
                        Schema schema = SchemaUtil.findSchema(this.TableName);
                        SchemaColumn[] scs = (SchemaColumn[]) schema.Columns.clone();
                        ArrayList list = new ArrayList();
                        for (int i = 0; i < scs.length; i++) {
                            if ((exclusiveMapx == null) || (!exclusiveMapx.containsKey(scs[i].getColumnName()))) {
                                if (scs[i].getColumnName().equalsIgnoreCase(this.NewColumnName))
                                    list.add(this.OldColumnName + " as " + this.NewColumnName);
                                else {
                                    list.add(scs[i].getColumnName());
                                }
                            }
                        }
                        StringBuilder sb = new StringBuilder();
                        sb.append("create table " + this.TableName + "_TMP as select " + StringUtil.join(list)
                                + " from " + this.TableName + "\n");
                        sb.append("drop table " + this.TableName + "\n");
                        sb.append("rename " + this.TableName + "_TMP to " + this.TableName + "\n");
                        TableUpdater.addPrimaryKey(sb, this.TableName, dbType);
                        TableUpdater.addIndexes(sb, this.TableName, dbType);
                        return sb.toString().split("\\n");
                    }
                    String sql = "alter table " + this.TableName + " modify " + this.OldColumnName;
                    sql = sql + " " + TableCreator.toSQLType(this.ColumnType, this.Length, this.Precision, dbType);
                    if (this.Mandatory) {
                        sql = sql + " not null";
                    }
                    return new String[]{sql};
                }
            }
            if (dbType.getExtendItemID().equalsIgnoreCase("MSSQL")) {
                if (this.Action.equalsIgnoreCase("add")) {
                    ArrayList sqlList = new ArrayList();
                    if (StringUtil.isNotEmpty(this.AfterColumn)) {
                        Schema schema = SchemaUtil.findSchema(this.TableName);
                        SchemaColumn[] scs = (SchemaColumn[]) schema.Columns.clone();
                        ArrayList listInsert = new ArrayList();
                        ArrayList listSelect = new ArrayList();
                        ArrayList pkList = new ArrayList();
                        for (int i = scs.length - 1; i >= 0; i--) {
                            if ((exclusiveMapx == null) || (!exclusiveMapx.containsKey(scs[i].getColumnName()))) {
                                listInsert.add(scs[i].getColumnName());
                                listSelect.add(scs[i].getColumnName());
                            }
                            if (scs[i].isPrimaryKey()) {
                                pkList.add(scs[i].getColumnName());
                            }
                            for (int j = 0; j < togetherColumns.size(); j++) {
                                AlterTableInfo info = (AlterTableInfo) togetherColumns.get(j);
                                String columnName = info.AfterColumn;
                                if (scs[i].getColumnName().equalsIgnoreCase(columnName)) {
                                    SchemaColumn sc = new SchemaColumn(info.OldColumnName, info.ColumnType, i,
                                            info.Length, info.Precision, info.Mandatory, false, "");
                                    scs = (SchemaColumn[]) ArrayUtils.add(scs, i + 1, sc);

                                    if (info.Mandatory) {
                                        listInsert.add(info.OldColumnName);
                                        listSelect.add("''0'' as " + info.OldColumnName);
                                    }
                                }
                            }
                        }

                        for (int i = scs.length - 1; i > 0; i--) {
                            boolean duplicateFlag = false;
                            for (int j = i - 1; j >= 0; j--) {
                                if (scs[i].getColumnName().equalsIgnoreCase(scs[j].getColumnName())) {
                                    scs = (SchemaColumn[]) ArrayUtils.remove(scs, i);
                                    duplicateFlag = true;
                                    break;
                                }
                            }
                            if (duplicateFlag) {
                                continue;
                            }
                            if ((exclusiveMapx != null) && (exclusiveMapx.containsKey(scs[i].getColumnName()))) {
                                boolean removeFlag = true;
                                for (int j = 0; j < togetherColumns.size(); j++) {
                                    AlterTableInfo info = (AlterTableInfo) togetherColumns.get(j);
                                    String columnName = info.OldColumnName;
                                    if (scs[i].getColumnName().equalsIgnoreCase(columnName)) {
                                        removeFlag = false;
                                    }
                                }
                                if (removeFlag)
                                    scs = (SchemaColumn[]) ArrayUtils.remove(scs, i);
                            }
                        }
                        try {
                            sqlList.add(TableCreator.dropTable(this.TableName + "_TMP", dbType));
                            String sql = TableCreator.createTable(scs, this.TableName + "_TMP", dbType);

                            int index = sql.indexOf(",\n\tconstraint");
                            sql = sql.substring(0, index) + ")";
                            sqlList.add(sql);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        sqlList.add("if exists(select * from " + this.TableName + ") exec ('insert into "
                                + this.TableName + "_TMP (" + StringUtil.join(listInsert) + ") select "
                                + StringUtil.join(listSelect) + " from " + this.TableName + "')");
                        sqlList.add("drop table " + this.TableName);
                        sqlList.add("sp_rename '" + this.TableName + "_TMP', '" + this.TableName + "', 'OBJECT'");
                        StringBuilder sb = new StringBuilder();
                        TableUpdater.addPrimaryKey(sb, this.TableName, dbType);
                        TableUpdater.addIndexes(sb, this.TableName, dbType);
                        String[] arr = sb.toString().split("\\n");
                        for (int i = 0; i < arr.length; i++)
                            sqlList.add(arr[i]);
                    } else {
                        for (int j = 0; j < togetherColumns.size(); j++) {
                            AlterTableInfo info = (AlterTableInfo) togetherColumns.get(j);
                            String sql = "alter table " + info.TableName + " add " + info.OldColumnName + " "
                                    + TableCreator.toSQLType(info.ColumnType, info.Length, info.Precision, dbType);
                            if (info.Mandatory) {
                                sql = sql + " not null";
                            }
                            sqlList.add(sql);
                        }
                    }
                    String[] arr = new String[sqlList.size()];
                    for (int i = 0; i < arr.length; i++) {
                        arr[i] = ((String) sqlList.get(i));
                    }
                    return arr;
                }
                if (this.Action.equalsIgnoreCase("drop")) {
                    String sql = "alter table " + this.TableName + " drop column " + this.OldColumnName;
                    return new String[]{sql};
                }
                if ((this.Action.equalsIgnoreCase("modify")) || (this.Action.equalsIgnoreCase("change"))) {
                    ArrayList sqlList = new ArrayList();
                    SchemaColumn column = SchemaUtil.findColumn(this.TableName, this.OldColumnName);
                    if (column == null) {
                        column = SchemaUtil.findColumn(this.TableName, this.NewColumnName);
                    }
                    if (column.isPrimaryKey()) {
                        sqlList.add("alter table " + this.TableName + " drop constraint PK_" + this.TableName);
                    }
                    String sql = "alter table " + this.TableName + " alter column " + this.OldColumnName;
                    sql = sql + " " + TableCreator.toSQLType(this.ColumnType, this.Length, this.Precision, dbType);
                    if (this.Mandatory) {
                        sql = sql + " not null";
                    }
                    sqlList.add(sql);
                    if ((StringUtil.isNotEmpty(this.NewColumnName))
                            && (!this.NewColumnName.equalsIgnoreCase(this.OldColumnName))) {
                        sqlList.add(" sp_rename '" + this.TableName + "." + this.OldColumnName + "','"
                                + this.NewColumnName + "','column'");
                    }
                    if (column.isPrimaryKey()) {
                        SchemaColumn[] scs = SchemaUtil.findSchema(this.TableName).Columns;
                        List pkList = SchemaUtil.getPrimaryKeyColumnsList(scs);
                        sqlList.add("alter table " + this.TableName + " add constraint PK_" + this.TableName
                                + " primary key NONCLUSTERED(" + StringUtil.join(pkList) + ")");
                    }
                    String[] arr = new String[sqlList.size()];
                    for (int i = 0; i < arr.length; i++) {
                        arr[i] = ((String) sqlList.get(i));
                    }
                    return arr;
                }
            }
            return null;
        }
    }

    public static class CommentInfo extends TableUpdateInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        public String Comment;

        public String[] toSQLArray(IDBType dbType) {
            return new String[]{this.Comment};
        }
    }

    public static class CreateTableInfo extends TableUpdateInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        public String TableName;
        public ArrayList<SchemaColumn> Columns = new ArrayList();

        public String[] toSQLArray(IDBType dbType) {
            try {
                SchemaColumn[] scs = new SchemaColumn[this.Columns.size()];
                for (int i = 0; i < scs.length; i++) {
                    scs[i] = ((SchemaColumn) this.Columns.get(i));
                }
                return new String[]{TableCreator.createTable(scs, this.TableName, dbType)};
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class DropTableInfo extends TableUpdateInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        public String TableName;

        public String[] toSQLArray(IDBType dbType) {
            return new String[]{TableCreator.dropTable(this.TableName, dbType)};
        }
    }

    public static class SQLInfo extends TableUpdateInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        public String SQL;

        public String[] toSQLArray(IDBType dbType) {
            if (this.SQL.trim().endsWith(";")) {
                this.SQL = this.SQL.substring(0, this.SQL.length() - 1).trim();
            }
            return new String[]{this.SQL};
        }
    }
}
