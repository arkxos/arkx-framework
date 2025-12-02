package io.arkx.framework.data.fasttable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Darkness
 * @date 2017年7月13日 下午4:13:50
 * @version 1.0
 * @since 1.0
 */
public class FastTableBuilder {

    private String tableName;
    private List<FastColumn> columns = new ArrayList<>();

    public FastTableBuilder() {
    }

    public FastTableBuilder tableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public FastTableBuilder addColumns(List<FastColumn> columns) {
        this.columns = columns;
        return this;
    }

    public FastTableBuilder addDateColumn(String columnName) {
        columns.add(FastColumn.dateColumn(columnName));
        return this;
    }

    public FastTableBuilder addStringColumn(String columnName, int length) {
        columns.add(FastColumn.stringColumn(columnName, length));
        return this;
    }

    public FastTableBuilder addFixedStringColumn(String columnName, int length) {
        columns.add(FastColumn.fixedStringColumn(columnName, length));
        return this;
    }

    public FastTableBuilder addIntColumn(String columnName) {
        columns.add(FastColumn.intColumn(columnName));
        return this;
    }

    public FastTableBuilder addFloatColumn(String columnName) {
        columns.add(FastColumn.floatColumn(columnName));
        return this;
    }

    public FastTableBuilder addDoubleColumn(String columnName) {
        columns.add(FastColumn.doubleColumn(columnName));
        return this;
    }

    public FastTableBuilder addLongColumn(String columnName) {
        columns.add(FastColumn.longColumn(columnName));
        return this;
    }

    public FastTable create(String path) {
        return FastTable.create(path, tableName, columns);
    }

}
