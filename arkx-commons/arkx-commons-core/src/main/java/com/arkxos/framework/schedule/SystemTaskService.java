package com.arkxos.framework.schedule;

import java.util.List;

import com.arkxos.framework.Config;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.data.xml.XMLElement;
import com.arkxos.framework.extend.AbstractExtendService;
import com.arkxos.framework.extend.IExtendItem;

/**
 * 系统定时任务扩展服务类
 * 
 */
public class SystemTaskService extends AbstractExtendService<SystemTask> {

	public static SystemTaskService getInstance() {
		return findInstance(SystemTaskService.class);
	}

	@Override
	public void register(IExtendItem item) {
		super.register(item);
		loadCronConfig((SystemTask) item);
	}

	public static void loadCronConfig(SystemTask task) {
		List<XMLElement> datas = Config.getElements("*.cron.task");
		for (XMLElement data : datas) {
			String id = data.getAttributes().get("id");
			String time = data.getAttributes().get("time");
			String disabled = data.getAttributes().get("disabled");
			if (ObjectUtil.empty(id)) {
				continue;
			}
			if (task.getExtendItemID().equals(id)) {
				if (!ObjectUtil.empty(time)) {
					task.setCronExpression(time);
				}
				task.setDisabled("true".equals(disabled));
				break;
			}
		}
	}
}
