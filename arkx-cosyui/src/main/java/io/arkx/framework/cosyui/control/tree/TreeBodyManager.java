package io.arkx.framework.cosyui.control.tree;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.cosyui.control.TreeAction;
import io.arkx.framework.cosyui.zhtml.ZhtmlManagerContext;

/**
 * 树标签体管理器
 *
 */
public class TreeBodyManager {

	private static Mapx<String, TreeBody> map = new Mapx<>();

	public static TreeBody get(TreeAction dla, String uid, String html) {
		if (!map.containsKey(uid)) {
			TreeBody body = new TreeBody(uid, html);
			body.compile(dla);
			map.put(uid, body);
		}
		return map.get(uid);
	}

	public static TreeBody get(String uid) {
		TreeBody body = map.get(uid);
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
