package io.arkx.framework.commons.util.log;

/**
 * 日志管理器
 *
 */
public interface ILogManager {

    /**
     * 输出到控制台的日志记录器
     */
    public ILogger getConsoleLogger();

    /**
     * 输出到错误日志的日志记录器
     */
    public ILogger getErrorLogger();

    /**
     * 输出到定时任务日志的日志记录器
     */
    public ILogger getCronLogger();

}
