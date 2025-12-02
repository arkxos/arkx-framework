package com.github.dreamroute.common.util.test;

import static com.github.dreamroute.common.util.CollectionUtil.isNotEmpty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * <p>
 * 描述：抓取logbak打印的控制台日志信息，使用方式见测试用例：
 * <p>
 * <a href=
 * "https://stackoverflow.com/questions/1827677/how-to-do-a-junit-assert-on-a-message-in-a-logger/50268580">参考地址</a>
 *
 * @author w.dehi
 */
public class Appender {

	ListAppender<ILoggingEvent> apd = new ListAppender<>();

	public Appender(Class<?> cls) {
		Logger logger = (Logger) LoggerFactory.getLogger(cls);
		apd.start();
		logger.addAppender(apd);
	}

	/**
	 * 打印的第index行信息是否包含相应的字符串
	 * @param index 第index行
	 * @param str 目标字符串
	 * @return true-包含; false-不包含
	 */
	public boolean contains(int index, String str) {
		return apd.list.get(index).getFormattedMessage().contains(str);
	}

	/**
	 * 打印的第一行信息是否包含相应的字符串
	 * @param str 目标字符串
	 * @return true-包含; false-不包含
	 */
	public boolean contains(String str) {
		return contains(0, str);
	}

	/**
	 * 获取全部打印信息
	 */
	public List<String> getMessages() {
		return ofNullable(apd.list).orElseGet(ArrayList::new).stream().map(ILoggingEvent::getMessage).collect(toList());
	}

	/**
	 * 获取第一行打印信息，从第0行开始
	 */
	public String getMessage() {
		List<String> messages = getMessages();
		if (isNotEmpty(messages)) {
			return messages.get(0);
		}
		return null;
	}

	/**
	 * 获取第index行打印信息，从第0行开始
	 * @param index 第index行打印信息
	 */
	public String getMessage(int index) {
		List<String> messages = getMessages();
		if (isNotEmpty(messages) && messages.size() > index) {
			return messages.get(index);
		}
		return null;
	}

}
