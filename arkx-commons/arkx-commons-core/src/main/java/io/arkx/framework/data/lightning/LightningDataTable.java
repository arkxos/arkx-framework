package io.arkx.framework.data.lightning;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.DataTypes;

public class LightningDataTable extends DataTable implements ILightningTable {

    private static final long serialVersionUID = 1L;

    private String tableName;

    public LightningDataTable() {
    }

    public LightningDataTable(String tableName, DataColumn... dataColumns) {
        this.init(tableName, dataColumns);
    }

    public void init(String tableName, DataColumn... dataColumns) {
        this.tableName = tableName;
        this.setDataColumns(dataColumns);
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public LightningColumn[] getLightningColumns() {
        List<LightningColumn> lightningColumns = new ArrayList<>();

        for (DataColumn dataColumn : columns) {
            DataTypes dataColumnType = dataColumn.getColumnType();
            LightningColumnType lightningColumnType = LightningColumnType.valueOf(dataColumnType.code());

            LightningColumn lightningColumn = new LightningColumn(dataColumn.getColumnName(), lightningColumnType,
                    dataColumn.length);
            lightningColumns.add(lightningColumn);
        }

        return lightningColumns.toArray(new LightningColumn[0]);
    }
}
