package io.arkx.framework.data.oldfastdb;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.data.fasttable.FastColumn;
import io.arkx.framework.data.fasttable.FastColumnIndexType;
import io.arkx.framework.data.fasttable.FastColumnType;

public class FastDataTable extends DataTable implements IFastTable {

	private static final long serialVersionUID = 1L;
	
	private String tableName;
	
	public FastDataTable() {}
	
	public FastDataTable(String tableName, DataColumn... dataColumns) {
		this(tableName, true, dataColumns);
	}
	
	public FastDataTable(String tableName, boolean fix, DataColumn... dataColumns) {
//		if(fix) {
//			this.init(tableName, dataColumns);
//		} else {
		init(tableName, dataColumns);
//		}
	}
	
//	public void init(String tableName, DataColumn... dataColumns) {
//		this.tableName = tableName;
//		DataColumn rowIndex = intColumn("rowIndex");
//		DataColumn[] columns = new DataColumn[dataColumns.length+1];
//		columns[0] = rowIndex;
//		for (int i=0;i<dataColumns.length;i++) {
//			columns[i+1] = dataColumns[i];
//		}
//		this.setDataColumns(columns);
//	}
	
	public void init(String tableName, DataColumn... dataColumns) {
		this.tableName = tableName;
		this.setDataColumns(dataColumns);
	}
	
	public FastDataTable copy() {
		return new FastDataTable(this.getTableName(), false, this.getDataColumns());
	}

	@Override
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public FastColumn[] getLightningColumns() {
		List<FastColumn> lightningColumns = new ArrayList<>();
		
		for (DataColumn dataColumn : columns) {
			DataTypes dataColumnType = dataColumn.getColumnType();
			FastColumnType lightningColumnType = FastColumnType.valueOf(dataColumnType.code());
			
			FastColumnIndexType type = FastColumnIndexType.Normal;
			if(dataColumn.isPk()) {
				type = FastColumnIndexType.PrimaryKey;
			}
			if(dataColumn.isIndex()) {
				type = FastColumnIndexType.Index;
			}
			FastColumn lightningColumn = new FastColumn(dataColumn.getColumnName(), lightningColumnType, dataColumn.length, type);
			lightningColumns.add(lightningColumn);
		}
		
		return lightningColumns.toArray(new FastColumn[0]);
	}
	
	public FastColumn getFastColumn(String columnName) {
		for (FastColumn column : getLightningColumns()) {
			if(column.getName().equals(columnName)) {
				return column;
			}
		}
		return null;
	}

}
