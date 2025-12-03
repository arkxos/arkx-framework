package io.arkx.framework.data.db.command;

import java.util.HashMap;

import io.arkx.framework.json.JSON;
import io.arkx.framework.json.JSONObject;

/**
 * 自由SQL指令
 *
 */
public class SQLCommand implements IDBCommand {

    // NO_UCD
    /**
     * 指令对应的SQL
     */
    public String SQL;

    public static final String Prefix = "SQL:";

    @Override
    public String getPrefix() {
        return Prefix;
    }

    @Override
    public String[] getDefaultSQLArray(String dbType) {
        return new String[]{SQL};
    }

    @Override
    public void parse(String ddl) {
        ddl = ddl.substring(Prefix.length());
        JSONObject map = (JSONObject) JSON.parse(ddl);
        SQL = map.getString("SQL");
    }

    @Override
    public String toJSON() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("SQL", SQL);
        return Prefix + JSON.toJSONString(map);
    }

}
