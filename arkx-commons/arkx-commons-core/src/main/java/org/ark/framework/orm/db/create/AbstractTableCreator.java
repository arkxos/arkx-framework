package org.ark.framework.orm.db.create;

import org.ark.framework.orm.SchemaColumn;
import org.ark.framework.orm.db.util.ColumnTypeConverter;

import io.arkx.framework.commons.util.ObjectUtil;

/**
 * 数据库表创建器抽象类
 *
 * @class org.ark.framework.orm.db.create.AbstractTableCreator
 * @author Darkness
 * @date 2013-1-29 下午04:20:26
 * @version V1.0
 */
public abstract class AbstractTableCreator {

	/**
	 * 列类型转换工具，用于处理字段类型转换和阈值判断
	 */
	protected final ColumnTypeConverter columnTypeConverter = new ColumnTypeConverter();

	/**
	 * 根据字段配置获取列的SQL类型
	 * @param columnType 列类型代码
	 * @param length 列长度
	 * @param precision 列精度
	 * @return SQL类型
	 */
	protected abstract String convert(int columnType, int length, int precision);

	/**
	 * 创建表的SQL语句
	 * @param scs 字段配置
	 * @param tableCode 表名
	 * @return 创建表的SQL语句
	 */
	public abstract String createTableSql(SchemaColumn[] scs, String tableCode);

	/**
	 * 生成SQL类型声明，包括长度和精度
	 * @param columnType 列类型代码
	 * @param length 列长度
	 * @param precision 列精度
	 * @return 完整的SQL类型声明
	 */
	public String toSQLType(int columnType, int length, int precision) {
		String type = convert(columnType, length, precision);
		if ("CLOB".equals(type)) {
			length = 0;
		}
		if ("BLOB".equals(type)) {
			length = 0;
		}
		if ("DATE".equals(type)) {
			length = 0;
		}
		if ("INTEGER".equals(type)) {
			length = 0;
		}
		if ("TEXT".equals(type)) {
			length = 0;
		}
		if (ObjectUtil.empty(type)) {
			throw new RuntimeException("Unknown DataType" + columnType);
		}
		if ((length == 0) && (columnType == 1)) {
			throw new RuntimeException("varchar's length can't be empty!");
		}
		return type + getFieldExtDesc(length, precision);
	}

	/**
	 * 获取字段的长度和精度描述部分 如(50) 或 (10,2)
	 * @param length 长度
	 * @param precision 精度
	 * @return 格式化后的长度精度声明
	 */
	public String getFieldExtDesc(int length, int precision) {
		if (length != 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append(length);
			if (precision != 0) {
				sb.append(",");
				sb.append(precision);
			}
			sb.append(") ");
			return sb.toString();
		}
		return "";
	}

	/**
	 * 设置VARCHAR转TEXT的阈值
	 * @param threshold 阈值长度，超过此长度的VARCHAR将被转换为TEXT
	 * @return 当前实例，支持链式调用
	 */
	public AbstractTableCreator setVarcharToTextThreshold(int threshold) {
		this.columnTypeConverter.setVarcharToTextThreshold(threshold);
		return this;
	}

	/**
	 * 获取当前配置的VARCHAR转TEXT阈值
	 * @return 当前阈值
	 */
	public int getVarcharToTextThreshold() {
		return this.columnTypeConverter.getVarcharToTextThreshold();
	}

}
