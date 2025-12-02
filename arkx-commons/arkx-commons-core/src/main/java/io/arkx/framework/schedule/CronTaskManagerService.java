package io.arkx.framework.schedule;

import io.arkx.framework.extend.AbstractExtendService;

/**
 * 任务管理器扩展服务类
 *
 */
public class CronTaskManagerService extends AbstractExtendService<AbstractTaskManager> {

    public static CronTaskManagerService getInstance() {
        return findInstance(CronTaskManagerService.class);
    }
}
