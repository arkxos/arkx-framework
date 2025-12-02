package org.ark.framework.orm.db.create;

import org.ark.framework.orm.SchemaColumn;

/**
 * @class org.ark.framework.orm.db.create.SYBASETableCreator
 * @author Darkness
 * @date 2013-1-29 下午04:28:51
 * @version V1.0
 */
public class SYBASETableCreator extends AbstractTableCreator {

    @Override
    protected String convert(int columnType, int length, int precision) {
        if (columnType == 3)
            return "numeric";
        else if (columnType == 2)
            return "varbinary(MAX)";
        else if (columnType == 12)
            return "datetime";
        else if (columnType == 4)
            return "decimal";
        else if (columnType == 6)
            return "numeric";
        else if (columnType == 5)
            return "numeric";
        else if (columnType == 8)
            return "int";
        else if (columnType == 7)
            return "numeric(20)";
        else if (columnType == 9)
            return "int";
        else if (columnType == 1)
            return "varchar";
        else if (columnType == 10) {
            return "text";
        }
        return null;
    }

    @Override
    public String createTableSql(SchemaColumn[] scs, String tableCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table " + tableCode + "(\n");
        StringBuilder ksb = new StringBuilder();
        for (int i = 0; i < scs.length; i++) {
            SchemaColumn sc = scs[i];
            if (i != 0) {
                sb.append(",\n");
            }
            if (sc.getColumnName().equalsIgnoreCase("Count"))
                sb.append("\t\"" + sc.getColumnName() + "\" ");
            else if (sc.getColumnName().equalsIgnoreCase("Scroll"))
                sb.append("\t\"" + sc.getColumnName() + "\" ");
            else {
                sb.append("\t" + sc.getColumnName() + " ");
            }
            String sqlType = toSQLType(sc.getColumnType(), sc.getLength(), sc.getPrecision());
            sb.append(sqlType + " ");
            if (sc.isMandatory())
                sb.append("not null");
            else {
                sb.append("null");
            }
            if (sc.isPrimaryKey()) {
                if (ksb.length() == 0)
                    ksb.append("\tconstraint PK_" + tableCode + " primary key nonclustered (");
                else {
                    ksb.append(",");
                }
                ksb.append(sc.getColumnName());
            }
        }
        if (ksb.length() != 0) {
            ksb.append(")");
            sb.append(",\n" + ksb);
        }
        sb.append("\n)");
        return sb.toString();
    }

}
