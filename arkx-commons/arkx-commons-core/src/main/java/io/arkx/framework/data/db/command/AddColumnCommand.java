package io.arkx.framework.data.db.command;

import java.util.HashMap;

import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.data.db.dbtype.DBTypeService;
import io.arkx.framework.data.db.dbtype.IDBType;
import io.arkx.framework.data.db.dbtype.Sybase;
import io.arkx.framework.json.JSON;
import io.arkx.framework.json.JSONObject;

/**
 * 添加字段指令
 *
 */
public class AddColumnCommand implements IDBCommand {

    /**
     * 字段名
     */
    public String Column;

    /**
     * 数据类型
     */
    public int DataType;

    /**
     * 所在数据表
     */
    public String Table;

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

    public static final String Prefix = "AddColumn:";

    @Override
    public String[] getDefaultSQLArray(String dbType) {
        IDBType db = DBTypeService.getInstance().get(dbType);
        String sql = "alter table " + Table + " add column " + db.maskColumnName(Column) + " "
                + db.toSQLType(DataTypes.valueOf(DataType), Length, Precision);
        if (Mandatory) {
            sql += " not null";
        } else if (dbType.equalsIgnoreCase(Sybase.ID)) {
            sql += " null";
        }
        return new String[]{sql};
    }

    @Override
    public void parse(String ddl) {
        ddl = ddl.substring(Prefix.length());
        JSONObject map = (JSONObject) JSON.parse(ddl);
        Column = map.getString("Column");
        DataType = map.getInt("DataType");
        Table = map.getString("Table");
        Length = map.getInt("Length");
        Precision = map.getInt("Precision");
        Mandatory = map.getBoolean("Mandatory");
    }

    @Override
    public String toJSON() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Column", Column);
        map.put("DataType", DataType);
        map.put("Table", Table);
        map.put("Length", Length);
        map.put("Precision", Precision);
        map.put("Mandatory", Mandatory);
        return Prefix + JSON.toJSONString(map);
    }

    @Override
    public String getPrefix() {
        return Prefix;
    }

}
