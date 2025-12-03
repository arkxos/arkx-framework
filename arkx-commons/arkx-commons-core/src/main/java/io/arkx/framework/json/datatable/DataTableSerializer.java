package io.arkx.framework.json.datatable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;

import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.commons.util.DateUtil;
import io.arkx.framework.commons.util.StringUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

/**
 * @author darkness
 * @date 2018-10-09 22:40:46
 * @version 1.0
 * @since 4.0
 */
public class DataTableSerializer implements ObjectSerializer {

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
            throws IOException {
        SerializeWriter out = serializer.getWriter();
        // if (object == null) {
        // serializer.getWriter().writeNull();
        // return;
        // }
        // TestEnum testEnum = (TestEnum)object;
        // out.write(testEnum.getName());
        JSONObject jsonObject = new JSONObject();
        // jsonObject.put("a", "b");
        //// out.write(JSON.toJSONString(jsonObject));
        // out.write("{\"Columns\":[{\"Type\":1,\"Name\":\"id\"},{\"Type\":1,\"Name\":\"name\"}],\"Values\":[[\"id1\",\"ark1\"],[\"id2\",\"ark2\"]],\"@type\":\"DataTable\"}");

        DataTable dt = (DataTable) object;
        jsonObject.put("Columns", dt.getDataColumns());
        Object[][] vs = new Object[dt.getRowCount()][dt.getColumnCount()];
        for (int j = 0; j < dt.getRowCount(); j++) {
            DataRow dr = dt.getDataRow(j);
            vs[j] = dr.getDataValues();
            if (j == 0) {
                for (int i = 0; i < dr.getColumnCount(); i++) {
                    if ((dr.get(i) instanceof Date)) {
                        dr.getDataColumn(i).setColumnType(DataTypes.DATETIME);
                    }
                }
            }
        }
        for (int i = 0; i < dt.getColumnCount(); i++) {
            DataColumn dc = dt.getDataColumn(i);
            if (dc.getColumnType() == DataTypes.DATETIME) {// 日期字段应该自动格式化
                for (int j = 0; j < dt.getRowCount(); j++) {
                    Object v = vs[j][i];
                    if (v instanceof Date) {
                        if (StringUtil.isNotEmpty(dc.getDateFormat())) {
                            v = DateUtil.toString((Date) v, dc.getDateFormat());
                        } else {
                            v = DateUtil.toDateTimeString((Date) v);
                        }
                    }
                    vs[j][i] = v;
                }
            } else if (dc.getColumnType() == DataTypes.BIGDECIMAL) {// 日期字段应该自动格式化
                for (int j = 0; j < dt.getRowCount(); j++) {
                    Object v = vs[j][i];
                    if (v instanceof BigDecimal) {
                        v = ((BigDecimal) v).toString();
                    }
                    vs[j][i] = v;
                }
            }
        }

        jsonObject.put("Values", vs);
        jsonObject.put("@arktype", "DataTable");
        // jsonObject.put("@type", "collection.io.arkx.framework.commons.DataTable");

        out.write(JSON.toJSONString(jsonObject));
    }

}
