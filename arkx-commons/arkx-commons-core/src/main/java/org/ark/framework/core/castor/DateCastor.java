package org.ark.framework.core.castor;

import java.util.Date;

import io.arkx.framework.commons.util.DateUtil;

/**
 * 日期类型转换器
 *
 * @author Darkness
 * @date 2013-3-26 下午08:39:22
 * @version V1.0
 */
public class DateCastor extends AbstractInnerCastor {

	private static DateCastor singleton = new DateCastor();

	public static DateCastor getInstance() {
		return singleton;
	}

	@Override
	public boolean canCast(Class<?> type) {
		return Date.class == type;
	}

	@Override
	public Object cast(Object obj, Class<?> type) {
		if (obj == null) {
			return null;
		}
		if ((obj instanceof Date))
			return (Date) obj;
		if ((obj instanceof Long)) {
			return new Date(((Long) obj).longValue());
		}
		return DateUtil.parseDateTime(obj.toString());
	}

}
