package io.arkx.framework.data.oldfastdb;

import io.arkx.framework.data.fasttable.FastColumn;

/**
 * @author Darkness
 * @date 2015年12月19日 下午5:12:47
 * @version V1.0
 * @since infinity 1.0
 */
public interface IFastTable {

	String getTableName();

	int getRowCount();

	int getColumnCount();

	FastColumn[] getLightningColumns();

	FastColumn getFastColumn(String columnName);

}
