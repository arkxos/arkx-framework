package io.arkx.framework.commons.util.log;

/**
 * 日志记录器
 * 
 */
public interface ILogger {

	public void trace(Object paramObject);

	public void debug(Object paramObject);

	public void info(Object paramObject);

	public void warn(Object paramObject);

	public void error(Object paramObject);

	public void fatal(Object paramObject);
}
