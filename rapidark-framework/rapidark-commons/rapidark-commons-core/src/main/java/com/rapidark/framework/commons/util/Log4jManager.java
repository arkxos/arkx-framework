package com.rapidark.framework.commons.util;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import com.rapidark.framework.Config;
import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.commons.util.log.ILogManager;
import com.rapidark.framework.commons.util.log.ILogger;
import com.rapidark.framework.core.FrameworkException;

public class Log4jManager implements ILogManager {
	ILogger console = null;
	ILogger error = null;
	ILogger cron = null;

	public Log4jManager() {
		InputStream fileName = Log4jManager.class.getClassLoader().getResourceAsStream("log4j.config");
		String txt = null;
		if (fileName != null) {
			txt = FileUtil.readText(fileName, "utf-8");
		} 
//		else if (new File(fileName.replace("classes", "test-classes")).exists()) {
//			fileName = fileName.replace("classes", "test-classes");
//			txt = FileUtil.readText(fileName);
//		}
		if (StringUtil.isNotEmpty(txt)) {
			txt = StringUtil.replaceEx(txt, "%{ContextRealPath}", Config.getContextRealPath());
			Mapx<String, String> map = StringUtil.splitToMapx(txt, "\n", "=");
			Properties ps = new Properties();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String key = (String) entry.getKey();
				if ((!StringUtil.isEmpty(key)) && (!key.startsWith("#"))) {
					ps.put(key, ((String) entry.getValue()).trim());
				}
			}
			PropertyConfigurator.configure(ps);
			this.cron = new Log4jLogger(LogFactory.getLog("cronLogger"));
			this.console = new Log4jLogger(LogFactory.getLog("consoleLogger"));
			this.error = new Log4jLogger(LogFactory.getLog("errorLogger"));
		} else {
			throw new FrameworkException("Load Log4jManager failed!");
		}
	}

	public ILogger getConsoleLogger() {
		return this.console;
	}

	public ILogger getErrorLogger() {
		return this.error;
	}

	public ILogger getCronLogger() {
		return this.cron;
	}
}
