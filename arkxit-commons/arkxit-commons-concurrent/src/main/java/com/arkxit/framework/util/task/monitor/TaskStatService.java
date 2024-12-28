package com.arkxit.framework.util.task.monitor;

import com.arkxit.framework.util.task.util.Json;
import com.arkxit.framework.util.task.TaskEngine;
import com.arkxit.framework.util.task.TaskGroup;
import com.arkxit.framework.util.task.util.Utils;

import java.util.Collection;

public final class TaskStatService {

    public static final TaskStatService INSTANCE = new TaskStatService();

    private TaskEngine taskEngine;

    private TaskStatService() {
    }

    public static void setTaskEngine(TaskEngine taskEngine) {
        INSTANCE.taskEngine = taskEngine;
    }

    public String getBasicInfo() {
        Json.JsonObject object = Json.createObject();
        object.put("version", Utils.VERSION);
        object.put("java_version", Utils.getJavaVersion());
        object.put("os_info", Utils.getOsInfo());
        return object.end().toString();
    }

    public String getTaskInfo() {
        Json.JsonObject object = Json.createObject();
        object.put("count_task", taskEngine.getTotalNumberOfTask());
        object.put("count_running_task", taskEngine.getRunningNumberofTask());
        object.put("count_completed_task", taskEngine.getCompletedNumberOfTask());
        Collection<TaskGroup> taskGroups = taskEngine.getRunningTaskGroups();
        object.put("count_running_taskgroup", taskGroups.size());
        Json.JsonArray groups = Json.createArray();
        for (TaskGroup group : taskGroups) {
            Json.JsonObject groupObject = Json.createObject()
                .put("name", group.getName())
                .put("id", group.getId())
                .put("count_running_task", group.getRunningTasks().size())
                .end();
            groups.add(groupObject);
        }
        object.put("groups", groups.end());
        return object.end().toString();
    }

}
