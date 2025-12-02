package org.ark.framework.schedule;

import io.arkx.framework.extend.AbstractExtendService;

/**
 * @class org.ark.framework.schedule.CronTaskManagerService
 *
 * @author Darkness
 * @date 2013-1-31 下午12:21:42
 * @version V1.0
 */
public class CronTaskManagerService extends AbstractExtendService<AbstractTaskManager> {
    public static CronTaskManagerService getInstance() {
        return (CronTaskManagerService) findInstance(CronTaskManagerService.class);
    }
}
