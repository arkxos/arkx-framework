package io.arkx.framework.cosyui.control.grid;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.cosyui.control.DataGridAction;
import io.arkx.framework.cosyui.zhtml.ZhtmlManagerContext;

/**
 * DataGrid标签体管理器
 *
 */
public class DataGridBodyManager {
    static Mapx<String, DataGridBody> map = new Mapx<>();

    public static DataGridBody get(DataGridAction dga, String uid, String html) {
        if (!map.containsKey(uid)) {
            DataGridBody body = new DataGridBody(uid, html);
            body.compile(dga);
            map.put(uid, body);
        }
        return map.get(uid);
    }

    public static DataGridBody get(String uid) {
        DataGridBody body = map.get(uid);
        if (body == null) {// debug模式下使用loadData()会导致此处得到null值
            String fileName = uid.substring(0, uid.indexOf('#'));
            try {
                ZhtmlManagerContext.getInstance().getTemplateManager().getExecutor(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            body = map.get(uid);
        }
        return body;
    }
}
