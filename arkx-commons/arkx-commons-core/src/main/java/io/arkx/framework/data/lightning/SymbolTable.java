package io.arkx.framework.data.lightning;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;

/**
 *
 * @author Darkness
 * @date 2015年12月13日 下午12:52:52
 * @version V1.0
 * @since infinity 1.0
 */
public class SymbolTable extends LightningDataTable {

    private static final long serialVersionUID = 1L;

    public final static String TABLE_SYMBOL = "symbol";

    public static final String ColumnType = "type";
    public static final String ColumnCode = "code";
    public static final String ColumnName = "name";
    public static final String ColumnStatus = "status";
    public static final String ColumnPublishDate = "publishDate";
    public static final String ColumnDislosureDate = "dislosureDate";

    public SymbolTable() {
        super(TABLE_SYMBOL, preparedDataColumns());
    }

    public static DataColumn[] preparedDataColumns() {
        DataColumn typeColumn = DataColumn.fixedStringColumn(ColumnType, 2);// sh/sz
        DataColumn codeColumn = DataColumn.fixedStringColumn("code", 6);// 002312
        DataColumn nameColumn = DataColumn.stringColumn("name", 12);// 三泰控股
        DataColumn statusColumn = DataColumn.stringColumn("status", 10);// New Normal ST Disclosure

        // 成立日期
        DataColumn publishDate = DataColumn.dateColumn("publishDate");// 上市日期
        DataColumn dislosureDate = DataColumn.dateColumn("dislosureDate");// 退市日期
        return new DataColumn[]{typeColumn, codeColumn, nameColumn, statusColumn, publishDate, dislosureDate};
    }

    public List<String> codeList() {
        return toMapx(ColumnCode, ColumnName).keyArray();
    }

    public List<String> fullCodeList() {
        List<String> symbols = new ArrayList<>();
        for (DataRow row : this) {
            symbols.add(row.getString(ColumnType) + row.getString(ColumnCode));
        }

        return symbols;
    }

    public DataTable filterCode(String code) {
        return filter("code='" + code + "'");
    }

    public String getName(int rowIndex) {
        DataRow dataRow = get(rowIndex);
        return dataRow.getString("name");
    }

    public String getCode(int rowIndex) {
        DataRow dataRow = get(rowIndex);
        return dataRow.getString("code");
    }

    public String getStatus(int rowIndex) {
        DataRow dataRow = get(rowIndex);
        return dataRow.getString("status");
    }

    public String getType(int rowIndex) {
        DataRow dataRow = get(rowIndex);
        return dataRow.getString("type");
    }

    public DataTable newSymbols() {
        return filter("status='New'");
    }

    public DataTable normalSymbols() {
        return filter("status='Normal'");
    }

    public DataTable stSymbols() {
        return filter("status='ST'");
    }

    public DataTable disclosureSymbols() {
        return filter("status='Disclosure'");
    }

    public DataTable avaliableSymbols() {
        DataTable newTable = newSymbols();
        DataTable normalTable = normalSymbols();
        DataTable stTable = stSymbols();

        DataTable result = new DataTable(newTable.getDataColumns());
        result.union(newTable);
        result.union(normalTable);
        result.union(stTable);

        return result;
    }

}
