package io.arkx.framework.data.db.command;

import java.util.HashMap;

import io.arkx.framework.data.db.dbtype.DBTypeService;
import io.arkx.framework.data.db.dbtype.IDBType;
import io.arkx.framework.json.JSON;
import io.arkx.framework.json.JSONObject;

/**
 * 重命名字段指令
 *
 */
public class RenameColumnCommand implements IDBCommand {
    /**
     * 要重命名的字段名称
     */
    public String Column;
    /**
     * 新的字段名称
     */
    public String NewColumn;
    /**
     * 字段数据类型
     */
    public int DataType;
    /**
     * 字段长度
     */
    public int Length;
    /**
     * 字段精度
     */
    public int Precision;
    /**
     * 是否必填
     */
    public boolean Mandatory;
    /**
     * 所在数据表
     */
    public String Table;
    public static final String Prefix = "RenameColumn:";

    @Override
    public String getPrefix() {
        return Prefix;
    }

    @Override
    public String[] getDefaultSQLArray(String dbType) {
        IDBType db = DBTypeService.getInstance().get(dbType);
        return new String[]{"alter table " + Table + " rename column " + db.maskColumnName(Column) + " to "
                + db.maskColumnName(NewColumn)};
    }

    @Override
    public void parse(String ddl) {
        ddl = ddl.substring(Prefix.length());
        JSONObject map = (JSONObject) JSON.parse(ddl);
        Table = map.getString("Table");
        Column = map.getString("Column");
        NewColumn = map.getString("NewColumn");
        DataType = map.getInt("DataType");
        Length = map.getInt("Length");
        Precision = map.getInt("Precision");
        Mandatory = "true".equals(map.getString("Mandatory"));
    }

    @Override
    public String toJSON() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("Table", Table);
        map.put("Column", Column);
        map.put("NewColumn", NewColumn);
        map.put("DataType", DataType);
        map.put("Length", Length);
        map.put("Precision", Precision);
        map.put("Mandatory", Mandatory);
        return Prefix + JSON.toJSONString(map);
    }
}
