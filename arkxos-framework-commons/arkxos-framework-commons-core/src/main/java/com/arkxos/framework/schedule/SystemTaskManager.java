package com.arkxos.framework.schedule;

import java.util.List;

import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.core.FrameworkException;
import com.rapidark.framework.Config;

/**
 * 系统定时任务管理器,执行计划由任务实现类提供,不保存执行计划到持久层
 * 
 */
public class SystemTaskManager extends AbstractTaskManager {
	public static final String ID = "SYSTEM";

	@Override
	public void execute(final String id) {
		SystemTask gt = SystemTaskService.getInstance().get(id);
		if (gt != null) {
			gt.execute();
		} else {
			throw new FrameworkException("Task not found:" + id);
		}
	}

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.SystemTask}";
	}

	public SystemTask getTask(String id) {
		return SystemTaskService.getInstance().get(id);
	}

	public List<SystemTask> getAllTask() {
		return SystemTaskService.getInstance().getAll();
	}

	@Override
	public Mapx<String, String> getUsableTasks() {
		Mapx<String, String> map = new Mapx<String, String>();
		for (SystemTask gt : SystemTaskService.getInstance().getAll()) {
			if (Config.isFrontDeploy() && !gt.enable4Front()) {
				continue;
			}
			if (!gt.isDisabled()) {
				map.put(gt.getExtendItemID(), gt.getExtendItemName());
			}
		}
		return map;
	}

	@Override
	public String getTaskCronExpression(String id) {
		SystemTask gt = SystemTaskService.getInstance().get(id);
		if (gt == null) {
			return null;
		}
		return gt.getCronExpression();
	}

	@Override
	public Mapx<String, String> getConfigEnableTasks() {
		return null;
	}

	@Override
	public boolean enable4Front() {
		return true;
	}

}
