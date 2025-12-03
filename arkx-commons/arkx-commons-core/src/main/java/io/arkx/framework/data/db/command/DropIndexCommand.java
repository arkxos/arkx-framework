package io.arkx.framework.data.db.command;

import java.util.HashMap;

import io.arkx.framework.json.JSON;
import io.arkx.framework.json.JSONObject;

/**
 * 删除索引指令
 *
 */
public class DropIndexCommand implements IDBCommand {

    /**
     * 所在数据表
     */
    public String Table;

    /**
     * 索引名称
     */
    public String Name;

    public static final String Prefix = "DropIndex:";

    @Override
    public String getPrefix() {
        return Prefix;
    }

    @Override
    public void parse(String ddl) {
        ddl = ddl.substring(Prefix.length());
        JSONObject map = (JSONObject) JSON.parse(ddl);
        Table = map.getString("Table");
        Name = map.getString("Name");
    }

    @Override
    public String toJSON() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("Table", Table);
        map.put("Name", Name);
        return Prefix + JSON.toJSONString(map);
    }

    @Override
    public String[] getDefaultSQLArray(String dbType) {
        return new String[]{"drop index " + Name};
    }

}
