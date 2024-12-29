package com.arkxos.framework.misc;

import java.io.File;

import com.arkxos.framework.commons.util.FileUtil;
import com.arkxos.framework.schedule.SystemTask;
import com.rapidark.framework.Config;
/**
 * @class org.ark.framework.FrameworkTask
 * 定时清空Debug模式下的Session缓存
 * @author Darkness
 * @date 2013-1-31 下午01:00:34 
 * @version V1.0
 */
public class FrameworkTask extends SystemTask {
	public static final String ID = "com.rapidark.framework.FrameworkTask";

	@Override
	public void execute() {
		// 清除缓存
		if (!Config.isDebugMode()) {
			return;
		}
		File dir = new File(Config.getContextRealPath() + "WEB-INF/cache/");
		File[] fs = dir.listFiles();
		for (File f : fs) {
			if (f.isFile()) {
				FileUtil.delete(f);
			}
		}
	}

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "@{Platform.FrameworkTask}";
	}

	@Override
	public String getDefaultCronExpression() {
		return "30 10,16 * * *";
	}

	@Override
	public boolean enable4Front() {
		return true;
	}
}
