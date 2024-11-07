package com.rapidark.framework.util.task;

public class BaseTask extends AbstractTask {

    private TaskRunner taskRunner;

    protected BaseTask(TaskRunner taskRunner) {
        this(null, null, taskRunner);
    }

    protected BaseTask(String type, String id, TaskRunner taskRunner) {
        super(type, id);
        this.taskRunner = taskRunner;
    }

    public TaskRunner getExecutor() {
        return taskRunner;
    }
}
