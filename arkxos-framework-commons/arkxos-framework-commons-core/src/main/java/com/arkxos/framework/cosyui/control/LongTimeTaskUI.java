package com.arkxos.framework.cosyui.control;

import com.arkxos.framework.annotation.Priv;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.cosyui.web.UIFacade;
import com.arkxos.framework.i18n.LangMapping;

/**
 * 长时间任务UI类，用于向前台的进度条控件提供进度数据。
 * 
 */
public class LongTimeTaskUI extends UIFacade {
	
	@Priv(login = false)
	public void getInfo() {
		long id = $L("TaskID");
		LongTimeTask ltt = LongTimeTask.getInstanceById(id);
		if (ltt != null && ltt.isAlive()) {
			$S("CurrentInfo", StringUtil.isNotEmpty(ltt.getCurrentInfo()) ? ltt.getCurrentInfo() + "..." : "");
			$S("Messages", StringUtil.join(ltt.getMessages()));
			$S("Percent", "" + ltt.getPercent());
		} else {
			$S("CompleteFlag", "1");
			String finishInfo = LangMapping.get("Framework.TaskFinished");
			if (ltt != null) {
				String errors = ltt.getAllErrors();
				if (StringUtil.isNotEmpty(errors)) {
					$S("CurrentInfo", errors);
					$S("ErrorFlag", "1");
				} else {
					finishInfo = StringUtil.isNotEmpty(ltt.getFinishedInfo()) ? ltt.getFinishedInfo() : finishInfo;
					$S("CurrentInfo", finishInfo);
				}
			} else {
				$S("CurrentInfo", finishInfo);
			}
			LongTimeTask.removeInstanceById(id);
		}
	}

	@Priv(login = false)
	public void stop(Mapx<String, String> params) {
		long id = params.getLong("TaskID");
		LongTimeTask ltt = LongTimeTask.getInstanceById(id);
		if (ltt != null) {
			ltt.stopTask();
		}
	}

	@Priv(login = false)
	public void stopComplete(Mapx<String, String> params) {
		long id = params.getLong("TaskID");
		LongTimeTask ltt = LongTimeTask.getInstanceById(id);
		if (ltt == null || !ltt.isAlive()) {
			LongTimeTask.removeInstanceById(id);
		} else {
			Response.setStatus(0);
		}
	}
}
