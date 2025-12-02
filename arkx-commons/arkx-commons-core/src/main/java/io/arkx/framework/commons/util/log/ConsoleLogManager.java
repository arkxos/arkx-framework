package io.arkx.framework.commons.util.log;

/**
 * 将日志全部输出到控制台的日志管理器
 *
 */
public class ConsoleLogManager implements ILogManager {

	@Override
	public ILogger getConsoleLogger() {
		return new ConsoleLogger(false);
	}

	@Override
	public ILogger getErrorLogger() {
		return new ConsoleLogger(true);
	}

	@Override
	public ILogger getCronLogger() {
		return new ConsoleLogger(false);
	}

}
