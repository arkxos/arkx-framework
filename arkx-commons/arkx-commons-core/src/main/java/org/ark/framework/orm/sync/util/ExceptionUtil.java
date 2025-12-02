package org.ark.framework.orm.sync.util;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:31
 * @since 1.0
 */

/**
 * @author: Zhoulanzhen
 * @description:
 * @date: 2025/5/18 17:36
 * @version: 1.0
 */

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 异常工具类，用于获取异常的详细信息
 */
public class ExceptionUtil {

	/**
	 * 获取异常的完整detailMessage文本，包括所有嵌套的原因异常
	 * @param e 异常对象
	 * @return 完整的异常详细信息
	 */
	public static String getFullDetailMessage(Throwable e) {
		if (e == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		// 获取当前异常的detailMessage
		String detailMessage = getExceptionDetailMessage(e);
		if (detailMessage != null && !detailMessage.isEmpty()) {
			sb.append(e.getClass().getName()).append(": ").append(detailMessage);
		}
		else {
			sb.append(e.getClass().getName());
		}

		// 获取所有嵌套的原因异常
		Throwable cause = e.getCause();
		while (cause != null) {
			String causeMessage = getExceptionDetailMessage(cause);
			if (causeMessage != null && !causeMessage.isEmpty()) {
				sb.append("\nCaused by: ").append(cause.getClass().getName()).append(": ").append(causeMessage);
			}
			else {
				sb.append("\nCaused by: ").append(cause.getClass().getName());
			}
			cause = cause.getCause();
		}

		return sb.toString();
	}

	/**
	 * 通过反射获取异常的detailMessage字段
	 * @param throwable 异常对象
	 * @return detailMessage内容
	 */
	private static String getExceptionDetailMessage(Throwable throwable) {
		try {
			// 使用反射获取私有的detailMessage字段
			Field detailMessageField = Throwable.class.getDeclaredField("detailMessage");
			detailMessageField.setAccessible(true);
			return (String) detailMessageField.get(throwable);
		}
		catch (Exception ex) {
			// 如果反射失败，则回退到使用getMessage()方法
			return throwable.getMessage();
		}
	}

	/**
	 * 获取异常及其所有嵌套异常的列表
	 * @param e 异常对象
	 * @return 异常列表
	 */
	public static List<Throwable> getAllExceptions(Throwable e) {
		List<Throwable> exceptions = new ArrayList<>();

		Throwable current = e;
		while (current != null) {
			exceptions.add(current);
			current = current.getCause();
		}

		return exceptions;
	}

	/**
	 * 示例使用方法
	 */
	public static void main(String[] args) {
		try {
			// 创建一个嵌套的异常示例
			try {
				int result = 10 / 0;
			}
			catch (ArithmeticException ex) {
				throw new IllegalStateException("计算错误", ex);
			}
		}
		catch (Exception e) {
			// 打印完整的异常详细信息
			System.out.println("完整的异常信息:");
			System.out.println(getFullDetailMessage(e));

			System.out.println("\n所有异常列表:");
			List<Throwable> allExceptions = getAllExceptions(e);
			for (int i = 0; i < allExceptions.size(); i++) {
				Throwable t = allExceptions.get(i);
				System.out.println(i + ": " + t.getClass().getName() + " - " + t.getMessage());
			}
		}
	}

}
