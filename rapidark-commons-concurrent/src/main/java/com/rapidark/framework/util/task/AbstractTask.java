package com.rapidark.framework.util.task;

import com.rapidark.framework.util.task.exception.TaskException;
import com.rapidark.framework.util.task.callback.TaskListener;
import com.rapidark.framework.util.task.callback.Progress;
import com.rapidark.framework.util.task.util.Assert;
import com.rapidark.framework.util.task.util.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

@Setter
@Getter
public abstract class AbstractTask implements Task {

    private String id;// 任务的 ID
    @Setter
    private String type;// 任务的类型
    private Progress progress;// 进度回调函数
    @Getter
    private List<TaskListener> taskListeners = new ArrayList<>();
    private final AtomicReference<TaskStatus> statusReference = new AtomicReference<>(TaskStatus.INIT);// 任务的状态
    protected Future<?> future;

    private final long createTime;

    private long startTime;
    private long endTime;

    protected AbstractTask(String type, String id) {
        if (Utils.isEmpty(type)) {
            this.type = Task.DEFAULT_TYPE_NAME;
        } else {
            this.type = type;
        }
        if (Utils.isEmpty(id)) {
            this.id = Utils.generateId();
        } else {
            this.id = id;
        }
        createTime = System.currentTimeMillis();
    }

    public final boolean setStatus(TaskStatus expect, TaskStatus update) {
        Assert.notNull(statusReference);

        return this.statusReference.compareAndSet(expect, update);
    }

    private void setStatusToCancel() {
        this.statusReference.set(TaskStatus.CANCEL);
    }

    @Override
    public TaskStatus getStatus() {
        return this.statusReference.get();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (future == null) {
            setStatusToCancel();
            return true;
        }
        if (future.cancel(mayInterruptIfRunning)) {
            setStatusToCancel();
            return true;
        }
        return false;
    }

    @Override
    public void await() {
        if (future != null) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new TaskException(e);
            }
        }
    }

    @Override
    public void await(long timeout, TimeUnit unit) throws TimeoutException {
        if (future != null) {
            try {
                future.get(timeout, unit);
            } catch (InterruptedException | ExecutionException e) {
                throw new TaskException(e);
            }
        }
    }

    public void addListener(TaskListener listener) {
        taskListeners.add(listener);
    }

}
