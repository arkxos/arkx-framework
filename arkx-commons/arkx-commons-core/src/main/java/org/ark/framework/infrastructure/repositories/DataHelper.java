package org.ark.framework.infrastructure.repositories;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.arkxos.framework.commons.util.DateUtil;
import com.arkxos.framework.data.jdbc.Entity;


/**
 * @class org.ark.framework.infrastructure.repositories.DataHelper
 * @author Darkness
 * @date 2012-10-13 下午10:22:23
 * @version V1.0
 */
public class DataHelper {

	public static int GetInteger(Object value) {
		if (value != null) {
			return Integer.parseInt(value.toString());
		}
		return 0;
	}

	public static BigDecimal GetDecimal(Object value) {
		if (value != null) {
			return new BigDecimal(value.toString());
		}
		return new BigDecimal(0);
	}

	public static Date GetNullableDateTime(Object value) {
		if (value != null) {
			DateUtil.parse(value.toString());
		}
		return new Date("1970-01-01");
	}

	/**
	 * 将list对象转化成逗号分隔的sql in子句
	 * 
	 * @author Darkness
	 * @date 2013-3-17 下午11:04:18 
	 * @version V1.0
	 */
	public static <T extends Entity> String EntityListToDelimited(List<T> entities) {
		StringBuilder builder = new StringBuilder(20);
		if (entities != null) {
			for (int i = 0; i < entities.size(); i++) {
				if (i > 0) {
					builder.append(",");
				}
				builder.append(entities.get(i).getId().toString());
			}
		}
		return builder.toString();
	}

	public static Object GetSqlValue(Object key) {
		return null;
	}
}
