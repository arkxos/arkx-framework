package io.arkx.framework.util.task;

import io.arkx.framework.util.task.callback.Progress;
import io.arkx.framework.util.task.callback.TaskListener;
import io.arkx.framework.util.task.util.Assert;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface Task {

    String DEFAULT_TYPE_NAME = "DEFAULT";

    /**
     * 当前任务的唯一 ID
     *
     * @return 任务 ID
     */
    String getId();

    /**
     * 获取任务的类型
     *
     * @return 任务类型
     */
    String getType();

    /**
     * 获取当前任务的状态
     *
     * @return 任务状态
     * @see TaskStatus
     */
    TaskStatus getStatus();

    boolean isFinished();

    /**
     * 获取当前任务设置的进度回调，使用 {@code TaskContext.onProgress(int)} 设置进度。可能为 {@code null}。
     *
     * @return {@link Progress}
     * @see TaskContext#onProgress(int)
     * @see Progress
     */
    Progress getProgress();

    double getProgressPercent();

    /**
     * 获取任务创建时间
     *
     * @return 任务创建时间
     */
    long getCreateTime();

    /**
     * 获取任务开始执行时间
     *
     * @return 任务开始执行的时间
     */
    long getStartTime();

    /**
     * 获取任务结束时间
     *
     * @return 任务结束时间
     */
    long getEndTime();

    /**
     * 获取当前任务设置的完成时的回调，可能为 {@code null}
     *
     * @return {@link TaskListener}
     * @see TaskContext#onSuccess(Object...)
     * @see TaskContext#onError(Exception)
     * @see TaskContext#onError(String, Object)
     * @see TaskListener
     */
    List<TaskListener> getTaskListeners();

    /**
     * 取消当前任务
     *
     * @param mayInterruptIfRunning {@code true} 如果执行当前任务的线程需要被中断。否则任务可能会执行完成
     * @return {@code true} 如果当前任务被取消；否则返回 {@code false}
     */
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     * 等待当前任务执行完成，会阻断当前线程继续执行，直到任务完成
     */
    void await();

    /**
     * 在指定的时间内等待当前任务执行完成，会阻断当前线程继续执行，直到任务完成或达到了指定的时间。
     * 如果到达指定时间，任务仍未完成，将会抛出异常
     *
     * @param timeout 超时时间
     * @param unit    时间单位
     * @throws TimeoutException 如果到达指定时间，任务仍未完成，将会抛出此异常
     */
    void await(long timeout, TimeUnit unit) throws TimeoutException;

    void triggerCompleted();

    class Builder {

        protected TaskRunner taskRunner;
        protected String type;
        protected String id;
        protected Progress progress;
        protected TaskListener taskListener;

        protected Builder(TaskRunner taskRunner) {
            Assert.notNull(taskRunner);

            this.taskRunner = taskRunner;
        }

        /**
         * 设置任务的类型，如果不设置，默认为 {@code DEFAULT}
         *
         * @param type 任务类型
         * @return {@link Builder}
         */
        public Builder type(String type) {
            this.type = type;
            return this;
        }

        /**
         * 设置任务的进度回调。使用 {@link TaskContext#onProgress(int)} 会触发该回调
         *
         * @param progress 进度回调
         * @return {@link Builder}
         */
        public Builder progress(Progress progress) {
            this.progress = progress;
            return this;
        }

        /**
         * 设置任务完成时的回调，可以使用 {@link TaskContext#onSuccess(Object...)}、{@link TaskContext#onError(String, Object)} 或
         * {@link TaskContext#onError(Exception)} 触发该回调。
         * <p>
         * 如果调用 {@link TaskContext#onSuccess(Object...)} 触发回调，任务状态为成功 {@link TaskStatus#SUCCESS}，并且回调函数的第二个参数
         * {@code Exception} 将为 {@code null}
         * </p>
         * <p>
         * 如果调用 {@link TaskContext#onError(String, Object)} 或 {@link TaskContext#onError(Exception)} 触发回调，认为的状态为错误 {@link TaskStatus#ERROR}，
         * 并且回调函数的第二个参数不为 {@code null}
         * </p>
         *
         * @param taskListener 任务完成时的回调
         * @return {@link Builder}
         * @see TaskListener#onExecuteFinish(TaskContext, Exception)
         */
        public Builder end(TaskListener taskListener) {
            this.taskListener = taskListener;
            return this;
        }

        public Task build() {
            BaseTask task = new BaseTask(this.type, this.id, taskRunner);
            task.setProgress(progress);
            task.addListener(taskListener);
            return task;
        }

    }

}
