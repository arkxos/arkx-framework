package org.ark.framework.schedule;

import java.util.Timer;

import io.arkx.framework.Config;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.i18n.LangUtil;

/**
 * @class org.ark.framework.schedule.CronManager
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:21:18 
 * @version V1.0
 */
public class CronManager {
	
	private Timer mTimer;
	private CronMonitor mMonitor;
	private static CronManager instance;
	public static final long SCAN_INTERVAL = 1000L;
	private static Object mutex = new Object();

	public static synchronized CronManager getInstance() {
		if (instance == null) {
			synchronized (mutex) {
				if (instance == null) {
					instance = new CronManager();
				}
			}
		}
		return instance;
	}

	private CronManager() {
		init();
	}

	public void init() {
		if (!Config.isInstalled()) {
			return;
		}
		this.mTimer = new Timer("Cron Manager Timer", true);
		this.mMonitor = new CronMonitor();
		this.mTimer.schedule(this.mMonitor, System.currentTimeMillis() % 1000L, 1000L);
		LogUtil.info("----" + Config.getAppCode() + "(" + LangUtil.get(Config.getAppName()) + "): CronManager Initialized----");
	}

	public Mapx<String, String> getTaskTypes() {
		Mapx<String, String> rmap = new Mapx<String, String>();
		for (AbstractTaskManager ctm : CronTaskManagerService.getInstance().getAll()) {
			if ((ctm instanceof SystemTaskManager)) {
				continue;
			}
			rmap.put(ctm.getExtendItemID(), ctm.getExtendItemName());
		}
		return rmap;
	}

	public Mapx<String, String> getConfigEnableTasks(String id) {
		AbstractTaskManager ctm = (AbstractTaskManager) CronTaskManagerService.getInstance().get(id);
		if (ctm == null) {
			return null;
		}
		return ctm.getConfigEnableTasks();
	}

	protected AbstractTaskManager getCronTaskManager(String id) {
		return (AbstractTaskManager) CronTaskManagerService.getInstance().get(id);
	}

	public void destory() {
		if (this.mMonitor != null) {
			this.mMonitor.destory();
			this.mTimer.cancel();
			this.mTimer = null;
		}
	}
}