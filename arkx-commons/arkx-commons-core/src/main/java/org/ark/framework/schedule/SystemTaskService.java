package org.ark.framework.schedule;

import io.arkx.framework.Config;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.data.xml.XMLElement;
import io.arkx.framework.extend.AbstractExtendService;

import java.util.List;

/**
 * @class org.ark.framework.schedule.SystemTaskService
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:22:20 
 * @version V1.0
 */
public class SystemTaskService extends AbstractExtendService<SystemTask> {
	public static final String ID = "org.ark.framework.schedule.SystemTaskService";
	private static boolean cronConfigLoaded = false;

	public static SystemTaskService getInstance() {
		if (!cronConfigLoaded) {
			loadCronConfig();
			cronConfigLoaded = true;
		}
		return (SystemTaskService) findInstance(SystemTaskService.class);
	}

	private static void loadCronConfig() {
		List<XMLElement> datas = Config.getElements("*.cron.task");
		for (int i = 0; (datas != null) && (i < datas.size()); i++) {
			String id = datas.get(i).getAttributes().getString("id");
			String time = datas.get(i).getAttributes().getString("time");
			if ((ObjectUtil.empty(id)) && (ObjectUtil.empty(time))) {
				continue;
			}
			SystemTask st = (SystemTask) getInstance().get(id);
			st.setCronExpression(time);
		}
	}
}