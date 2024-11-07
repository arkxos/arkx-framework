package com.rapidark.framework.util.task;

class BaseTask extends AbstractTask {

    private final Executor executor;

    protected BaseTask(Executor executor) {
        this(null, null, executor);
    }

    protected BaseTask(String type, String id, Executor executor) {
        super(type, id);
        this.executor = executor;
    }

    public Executor getExecutor() {
        return executor;
    }
}
