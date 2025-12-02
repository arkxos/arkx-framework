package io.arkx.data.lightning.query;

import io.arkx.framework.commons.utils2.StringUtil;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class DynamicFieldRule extends Rule {

	private String label;

	private String field;

	private String operator;

	private String type; // number/string/date

	private String value;

	@Override
	public String toSql() {
		String columnName = StringUtil.camelToUnderline(field);
		String columnValue = value;
		if ("string".equals(type)) {
			columnValue = "'" + columnValue + "'";
		}
		switch (operator) {
			case "equals":
				return columnName + " = " + columnValue;
			case "great_than":
				return columnName + " > " + columnValue;
			case "less_than":
				return columnName + " < " + columnValue;
			case "great_than_equals":
				return columnName + " >= " + columnValue;
			case "less_than_equals":
				return columnName + " <= " + columnValue;
			case "not_equals":
				return columnName + " != " + columnValue;
			case "like":
				return columnName + " like '%" + value + "%'";
			case "before_like":
				return columnName + " like '" + value + "%'";
			case "end_like":
				return columnName + " like '%" + value + "'";
			case "in":
				return columnName + " in(" + value + ")";
			case "not_in":
				return columnName + " not in(" + value + ")";
			case "between":
				String[] temp = value.split(",");
				return columnName + " between '" + temp[0] + "' and '" + temp[1] + "'";

			default:
				throw new RuntimeException("操作符错误：" + operator);
		}
	}

}
