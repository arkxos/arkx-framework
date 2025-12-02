package io.arkx.framework.data.db.core.provider.transform.handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.arkx.framework.data.db.core.util.ColumHandler;
import io.arkx.framework.data.db.core.util.EmptyUtils;
import io.arkx.framework.data.db.core.util.EncrypAES;
import io.arkx.framework.data.db.core.util.MultiFormatDateParser;

public class HandlerMap {

	private static final HashMap<String, ColumHandler> MIDDLEMEN = new HashMap<>();

	static {
		// 加解密
		MIDDLEMEN.put("decrypt", (var1, var2) -> var1[var2] = EncrypAES.decrypt((String) var1[var2]));
		// 去除空格和指定字符如: "undefined"
		MIDDLEMEN.put("removeSpace", (originalResult,
				index) -> originalResult[index] = EmptyUtils.removeSpace((String) originalResult[index]));
		// varchar时间类型转成timestamp
		MIDDLEMEN.put("varchar_to_timestamp",
				(var1, var2) -> var1[var2] = MultiFormatDateParser.parseToDate((String) var1[var2]));
		// 处理1898年数据
		MIDDLEMEN.put("years_from_1898", (var1, var2) -> {
			String wjwfsj = (String) var1[var2];
			if (StringUtils.isBlank(wjwfsj)) {
				var1[var2] = null;
			}
			else {
				String[] yearIndex = wjwfsj.split(",");
				List<String> probYear = Arrays.stream(yearIndex).map(index -> {
					int intIndex = Integer.parseInt(index);
					if (0 == intIndex) {
						return "不详";
					}
					else if (1 == intIndex) {
						return "1990年以前";
					}
					else {
						return String.valueOf(intIndex + 1898);
					}
				}).collect(Collectors.toList());
				var1[var2] = String.join(",", probYear);
			}
		});
		// float_to_varchar
		MIDDLEMEN.put("float_to_varchar",
				(var1, var2) -> var1[var2] = MultiFormatDateParser.floatToVarchar((Double) var1[var2]));
	}

	public static ColumHandler getHandlerWithName(String name) {
		return MIDDLEMEN.get(name);
	}

	public static HashMap<String, ColumHandler> getHandlers() {
		return MIDDLEMEN;
	}

}
