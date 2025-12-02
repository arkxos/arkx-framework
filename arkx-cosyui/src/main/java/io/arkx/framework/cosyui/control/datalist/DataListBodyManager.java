package io.arkx.framework.cosyui.control.datalist;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.cosyui.control.DataListAction;
import io.arkx.framework.cosyui.zhtml.ZhtmlManagerContext;

/**
 * DataList标签体管理器
 *
 */
public class DataListBodyManager {

	static Mapx<String, DataListBody> map = new Mapx<String, DataListBody>();

	public static DataListBody get(DataListAction dla, String uid, String html) {
		if (!map.containsKey(uid)) {
			DataListBody body = new DataListBody(uid, html);
			body.compile(dla);
			map.put(uid, body);
		}
		return map.get(uid);
	}

	public static DataListBody get(String uid) {
		DataListBody body = map.get(uid);
		if (body == null) {// debug模式下使用loadData()会导致此处得到null值
			String fileName = uid.substring(0, uid.indexOf('#'));
			try {
				ZhtmlManagerContext.getInstance().getTemplateManager().getExecutor(fileName);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			body = map.get(uid);
		}
		return body;
	}

}
