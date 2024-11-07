package com.rapidark.framework.util.task;

import com.rapidark.framework.util.task.exception.ExecutionException;
import com.tuples.Tuple;

public final class TaskContext {

    private final Task task;
    private final Group group;
    private final Tuple result = new Tuple();

    public static final class Group {

        private final TaskGroup group;

        public Group(TaskGroup group) {
            this.group = group;
        }

        public int incrementCounter() {
            if (group != null) {
                return group.counter.incrementAndGet();
            }
            return -1;
        }

        public void addData(String key, Object value) {
            if (group != null) {
                group.data.add(key, value);
            }
        }

        public void addData(Object value) {
            if (group != null) {
                group.data.add(value);
            }
        }

    }

    public TaskContext(Task task) {
        this.task = task;
        this.group = new Group(null);
    }

    public TaskContext(Task task, TaskGroup group) {
        this.task = task;
        this.group = new Group(group);
    }

    public void onProgress(int progress) {
        if (task.getProgress() != null) {
            task.getProgress().call(progress);
        }
    }

    public void onSuccess(Object... objs) {
        if (((AbstractTask) task).setStatus(TaskStatus.RUNNING, TaskStatus.SUCCESS)
            && task.getCallback() != null) {
            this.toResult(objs);
            task.getCallback().call(this, null);
        }
    }

    public void onError(String message, Object obj) {
        onError(new ExecutionException(message, obj));
    }

    public void onError(Exception error) {
        if (((AbstractTask) task).setStatus(TaskStatus.RUNNING, TaskStatus.ERROR)
            && task.getCallback() != null) {
            task.getCallback().call(null, error);
        }
    }

    public String getId() {
        return task.getId();
    }

    public TaskStatus getStatus() {
        return task.getStatus();
    }

    public String getType() {
        return task.getType();
    }

    public void toResult(Object... objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                result.add(obj);
            }
        }
    }

    public Tuple getResult() {
        return this.result;
    }


    public Group group() {
        return this.group;
    }


}
