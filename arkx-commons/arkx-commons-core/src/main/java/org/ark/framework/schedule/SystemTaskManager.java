package org.ark.framework.schedule;

import java.util.List;

import io.arkx.framework.commons.collection.Mapx;

/**
 * @class org.ark.framework.schedule.SystemTaskManager
 * @author Darkness
 * @date 2013-1-31 下午12:22:03
 * @version V1.0
 */
public class SystemTaskManager extends AbstractTaskManager {

    public static final String ID = "SYSTEM";

    public synchronized void execute(final String id) {
        new Thread() {
            public void run() {
                SystemTask gt = (SystemTask) SystemTaskService.getInstance().get(id);
                if (gt != null) {
                    if ((!gt.isRunning()) && (!gt.isRunning())) {
                        gt.setRunning(true);
                        gt.execute();
                        gt.setRunning(false);
                    }
                } else
                    throw new RuntimeException("Task not found:" + id);
            }
        }.start();
    }

    public boolean isRunning(String id) {
        SystemTask gt = (SystemTask) SystemTaskService.getInstance().get(id);
        if (gt != null) {
            return gt.isRunning();
        }
        throw new RuntimeException("Task not found:" + id);
    }

    @Override
    public String getExtendItemID() {
        return "SYSTEM";
    }

    @Override
    public String getExtendItemName() {
        return "System Task";
    }

    public SystemTask getTask(String id) {
        return (SystemTask) SystemTaskService.getInstance().get(id);
    }

    public List<SystemTask> getAllTask() {
        return SystemTaskService.getInstance().getAll();
    }

    public Mapx<String, String> getUsableTasks() {
        Mapx map = new Mapx();
        for (SystemTask gt : SystemTaskService.getInstance().getAll()) {
            map.put(gt.getExtendItemID(), gt.getExtendItemName());
        }
        return map;
    }

    public String getTaskCronExpression(String id) {
        SystemTask gt = (SystemTask) SystemTaskService.getInstance().get(id);
        return gt.getCronExpression();
    }

    public Mapx<String, String> getConfigEnableTasks() {
        return null;
    }

}
