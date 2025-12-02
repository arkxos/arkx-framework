package io.arkx.framework.commons.collection;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import org.apache.commons.lang3.ArrayUtils;

import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.commons.util.UtilityException;

/**
 * @class org.ark.framework.collection.DataTable 数据表格 使用方式： DataColumn
 *        dcUserName = new DataColumn("UserName", DataColumn.STRING); DataColumn
 *        dcRealName = new DataColumn("RealName", DataColumn.STRING); DataColumn
 *        dcStatusName = new DataColumn("StatusName", DataColumn.STRING);
 *        DataColumn dcBranchInnercodeName = new
 *        DataColumn("BranchInnercodeName", DataColumn.STRING); DataColumn
 *        dcRoleNames = new DataColumn("RoleNames", DataColumn.STRING);
 *
 *        DataTable dt = new DataTable(new DataColumn[] { dcUserName,
 *        dcRealName, dcStatusName, dcBranchInnercodeName, dcRoleNames }, null);
 *        dt.insertRow(new Object[] { "darkness1", "暗之幻影1", "1", "008", "admin"
 *        }); dt.insertRow(new Object[] { "darkness2", "暗之幻影2", "1", "008",
 *        "admin" });
 *
 * @author Darkness
 * @date 2012-8-8 上午10:28:19
 * @version V1.0
 * @description 数据表格
 */
public class DataTable implements Serializable, Cloneable, RandomAccess, Iterable<DataRow> {

    private static final long serialVersionUID = 1L;

    private boolean isWebMode;// 默认值为false，True表示getString的结果是null或者""时转成&nbsp;
    protected ArrayList<DataRow> rows = new ArrayList<>();// 数据表中的所有行
    protected DataColumn[] columns;// 所有字段

    /**
     * 构造器
     */
    public DataTable() {
        this.rows = new ArrayList<>();
        this.columns = new DataColumn[0];
    }

    /**
     * 创建表格，包含指定列
     *
     * @param columnNames
     *            列名称
     */
    public DataTable(String... columnNames) {

        this();

        if (columnNames != null) {
            for (String columnName : columnNames) {
                insertColumn(columnName);
            }
        }
    }

    /**
     * 构造器
     *
     * @param types
     *            竖列名表
     */
    public DataTable(DataColumn... types) {
        this(types, null);
    }

    /**
     * 构造器
     *
     * @param columns
     *            字段列表
     * @param values
     *            各行的值
     */
    public DataTable(DataColumn[] columns, Object[][] values) {
        if (columns == null) {
            columns = new DataColumn[0];
        }
        this.columns = columns;
        this.rows = new ArrayList<>();
        renameAmbiguousColumns();// 将名称相同的列重命名
        if (values != null) {
            for (Object[] value : values) {
                rows.add(new DataRow(this, value));
            }
        }
    }

    public void setDataColumns(DataColumn[] types) {
        this.columns = types;
    }

    /**
     * 将同名的字段重命名，规则是在原字段名后加“_1”之类的后缀
     */
    protected void renameAmbiguousColumns() {
        if (columns == null) {
            return;
        }
        for (int i = 0; i < columns.length; i++) {
            String columnName = columns[i].getColumnName();
            int count = 1;
            for (int j = i + 1; j < columns.length; j++) {
                if (columnName == null) {
                    throw new UtilityException("Column name cann't be null,index is " + i);
                }
                if (columnName.equalsIgnoreCase(columns[j].getColumnName())) {
                    columns[j].setColumnName(columnName + "_" + String.valueOf(++count));
                }
            }
        }
    }

    /**
     * 删除指定顺序的字段
     *
     * @param columnIndex
     *            字段顺序
     * @return DataTable本身
     */
    public DataTable deleteColumn(int columnIndex) {
        if (columns.length == 0) {
            return this;
        }
        if (columnIndex < 0 || columnIndex >= columns.length) {
            throw new UtilityException("Index is out of range：" + columnIndex);
        }
        columns = ArrayUtils.remove(columns, columnIndex);
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).values.remove(columnIndex);
        }
        return this;
    }

    /**
     * 删除字段名称对应的字段
     *
     * @param columnName
     *            字段名称
     * @return DataTable本身
     */
    public DataTable deleteColumn(String columnName) {
        int i = getColumnIndex(columnName);
        if (i >= 0) {
            deleteColumn(i);
        }
        return this;
    }

    /**
     * 插入一个字段，字段类型为DataTypes.STRING
     *
     * @param columnName
     *            字段名称
     * @return DataTable本身
     */
    public DataTable insertColumn(String columnName) {
        return insertColumn(new DataColumn(columnName, DataTypes.STRING, 2000), null, this.columns.length);
    }

    /**
     * 插入一个字段，字段类型为DataTypes.STRING，并将此字段在所有行中的值置为columnValue
     *
     * @param columnName
     *            字段名称
     * @param columnValue
     *            字段值
     * @return DataTable本身
     */
    public DataTable insertColumn(String columnName, Object columnValue) {
        Object[] cv = new Object[this.rows.size()];
        for (int i = 0; i < cv.length; i++) {
            cv[i] = columnValue;
        }
        return insertColumn(new DataColumn(columnName, DataTypes.STRING, 2000), cv, this.columns.length);
    }

    /**
     * 一次插入多个字段，字段类型都为DataTypes.STRING
     *
     * @param columnNames
     *            字段名称数组
     * @return DataTable本身
     */
    public DataTable insertColumns(String... columnNames) {
        for (String columnName : columnNames) {
            insertColumn(new DataColumn(columnName, DataTypes.STRING, 2000), null, this.columns.length);
        }
        return this;
    }

    /**
     * 插入一个字段，字段类型为DataTypes.STRING，此字段各行的值由values数组指定
     *
     * @param columnName
     *            字段名称
     * @param values
     *            值数组
     * @return DataTable本身
     */
    public DataTable insertColumn(String columnName, Object[] values) {
        return insertColumn(new DataColumn(columnName, DataTypes.STRING, 2000), values, this.columns.length);
    }

    /**
     * 插入一个字段
     *
     * @param dc
     *            字段信息
     * @return DataTable本身
     */
    public DataTable insertColumn(DataColumn dc) {
        return insertColumn(dc, null, this.columns.length);
    }

    /**
     * 插入一个字段，此字段在各行的值由values数组指定
     *
     * @param dc
     *            字段信息
     * @param values
     *            各行的值
     * @return DataTable本身
     */
    public DataTable insertColumn(DataColumn dc, Object[] values) {
        return insertColumn(dc, values, this.columns.length);
    }

    // public void insertColumn(String columnName, Object[] columnValue, int index)
    // {
    // insertColumn(new DataColumn(columnName, DataTypes.STRING), columnValue,
    // index);
    // }

    /**
     * 在指定位置上插入一个字段，字段类型为DataTypes.STRING,此字段在各行的值由values数组指定
     *
     * @param columnName
     *            字段名称
     * @param values
     *            各行的值
     * @param index
     *            字段位置
     * @return DataTable本身
     */
    public DataTable insertColumn(String columnName, Object[] values, int index) {// NO_UCD
        return insertColumn(new DataColumn(columnName, DataTypes.STRING, 2000), values, index);
    }

    /**
     * 在指定位置上插入一个字段，此字段在各行的值由values数组指定
     *
     * @param dc
     *            字段信息
     * @param values
     *            各行的值
     * @param index
     *            字段位置
     * @return DataTable本身
     */
    public DataTable insertColumn(DataColumn dc, Object[] values, int index) {
        if (index > columns.length) {
            throw new UtilityException("Index is out of range:" + index);
        }
        if (getDataColumn(dc.getColumnName()) != null) {
            return this;
        }
        columns = ArrayUtils.add(columns, index, dc);
        if (values == null) {
            values = new Object[rows.size()];
        }
        for (int i = 0; i < rows.size() && i < values.length; i++) {
            rows.get(i).values.add(index, values[i]);
        }
        return this;
    }

    /**
     * 插入一个数据行
     *
     * @param dr
     *            数据行
     * @return DataTable本身
     */
    public DataTable insertRow(DataRow dr) {
        return insertRow(dr, rows.size());
    }

    public DataTable insertRows(List<DataRow> drs) {
        // for (DataRow dataRow : drs) {
        // insertRow(dataRow, this.rows.size());
        // }
        this.rows.addAll(drs);
        return this;
    }

    /**
     * 在指定位置上插入一个数据行
     *
     * @param dr
     *            数据行
     * @param index
     *            行顺序
     * @return DataTable本身
     */
    public DataTable insertRow(DataRow dr, int index) {
        if (columns == null || columns.length == 0) {
            columns = dr.table.columns;
        }
        dr = dr.clone();
        dr.table = this;
        rows.add(index, dr);
        return this;
    }

    /**
     * 插入一个数据行，此数据行的各个字段的值由columnValues指定
     *
     * @param columnValues
     *            各个字段的值
     * @return DataTable本身
     */
    public DataTable insertRow(Object... columnValues) {
        return insertRow(columnValues, rows.size());
    }

    /**
     * 在指定位置上插入一个数据行，此数据行的各个字段的值由columnValues指定
     *
     * @param columnValues
     *            各个字段的值
     * @param index
     *            行顺序
     * @return DataTable本身
     */
    public DataTable insertRow(Object[] columnValues, int index) {
        DataRow dr = new DataRow(this, columnValues);
        rows.add(index, dr);
        return this;
    }

    /**
     * 删除指定位置的数据行
     *
     * @param index
     *            行顺序
     * @return DataTable本身
     */
    public DataTable deleteRow(int index) {
        rows.remove(index);
        return this;
    }

    /**
     * 删除指定的数据行
     *
     * @param dr
     *            数据行
     * @return DataTable本身
     */
    public DataTable deleteRow(DataRow dr) {
        rows.remove(dr);
        return this;
    }

    /**
     * 获得指定顺序的数据行
     *
     * @param rowIndex
     *            行顺序
     * @return 数据行
     */
    public DataRow get(int rowIndex) {
        return getDataRow(rowIndex);
    }

    /**
     * 设置指定数据行上的指定顺序的字段的值
     *
     * @param rowIndex
     *            行顺序
     * @param columnIndex
     *            字段顺序
     * @param value
     *            字段值
     * @return DataTable本身
     */
    public DataTable set(int rowIndex, int columnIndex, Object value) {
        getDataRow(rowIndex).set(columnIndex, value);
        return this;
    }

    /**
     * 设置指定数据行上的指定字段名的字段的值
     *
     * @param rowIndex
     *            行顺序
     * @param columnName
     *            字段名称
     * @param value
     *            字段值
     * @return DataTable本身
     */
    public DataTable set(int rowIndex, String columnName, Object value) {
        getDataRow(rowIndex).set(columnName, value);
        return this;
    }

    /**
     * 获取指定顺序的行上的指定顺序的字段的值
     *
     * @param rowIndex
     *            行顺序
     * @param columnIndex
     *            字段顺序
     * @return 字段值
     */
    public Object get(int rowIndex, int columnIndex) {
        return getDataRow(rowIndex).get(columnIndex);
    }

    /**
     * 获取指定顺序的行上的指定字段的值
     *
     * @param rowIndex
     *            行顺序
     * @param columnName
     *            字段名称
     * @return 字段值
     */
    public Object get(int rowIndex, String columnName) {
        return getDataRow(rowIndex).get(columnName);
    }

    /**
     * 获取指定顺序的行上的指定顺序的字段的值，并转化为String。
     * 如果isWebMode()为true，则会将null值和空字符串转为&amp;nbsp;返回
     *
     * @param rowIndex
     *            行顺序
     * @param columnIndex
     *            字段顺序
     * @return 字段值转换成的String实例
     */
    public String getString(int rowIndex, int columnIndex) {
        return getDataRow(rowIndex).getString(columnIndex);
    }

    /**
     * 获取指定顺序的行上的指定字段的值，并转化为String。 如果isWebMode()为true，则会将null值和空字符串转为&amp;nbsp;返回
     *
     * @param rowIndex
     *            行顺序
     * @param columnName
     *            字段名称
     * @return 字段值转换成的String实例
     */
    public String getString(int rowIndex, String columnName) {
        return getDataRow(rowIndex).getString(columnName);
    }

    /**
     * 获取指定顺序的行上的指定顺序的字段的值，并转化为整型。 如果字段值为null，则返回0
     *
     * @param rowIndex
     *            行顺序
     * @param columnIndex
     *            字段顺序
     * @return 字段值转换成的整型
     */
    public int getInt(int rowIndex, int columnIndex) {
        return getDataRow(rowIndex).getInt(columnIndex);
    }

    /**
     * 获取指定顺序的行上的指定字段的值，并转化为整型。 如果字段值为null，则返回0
     *
     * @param rowIndex
     *            行顺序
     * @param columnName
     *            字段名称
     * @return 字段值转换成的整型
     */
    public int getInt(int rowIndex, String columnName) {
        return getDataRow(rowIndex).getInt(columnName);
    }

    /**
     * 获取指定顺序的行上的指定顺序的字段的值，并转化为长整型。 如果字段值为null，则返回0
     *
     * @param rowIndex
     *            行顺序
     * @param columnIndex
     *            字段顺序
     * @return 字段值转换成的长整型
     */
    public long getLong(int rowIndex, int columnIndex) {
        return getDataRow(rowIndex).getLong(columnIndex);
    }

    /**
     * 获取指定顺序的行上的指定字段的值，并转化为长整型。 如果字段值为null，则返回0
     *
     * @param rowIndex
     *            行顺序
     * @param columnName
     *            字段名称
     * @return 字段值转换成的长整型
     */
    public long getLong(int rowIndex, String columnName) {
        return getDataRow(rowIndex).getLong(columnName);
    }

    /**
     * 获取指定顺序的行上的指定顺序的字段的值，并转化为双字节浮点型。 如果字段值为null，则返回0
     *
     * @param rowIndex
     *            行顺序
     * @param columnIndex
     *            字段顺序
     * @return 字段值转换成的双字节浮点型
     */
    public double getDouble(int rowIndex, int columnIndex) {// NO_UCD
        return getDataRow(rowIndex).getDouble(columnIndex);
    }

    /**
     * 获取指定顺序的行上的指定字段的值，并转化为双字节浮点型。 如果字段值为null，则返回0
     *
     * @param rowIndex
     *            行顺序
     * @param columnName
     *            字段名称
     * @return 字段值转换成的双字节浮点型
     */
    public double getDouble(int rowIndex, String columnName) {// NO_UCD
        return getDataRow(rowIndex).getDouble(columnName);
    }
    /**
     * 获取指定顺序的行上的指定顺序的字段的值，并转化为浮点型。 如果字段值为null，则返回0
     *
     * @param rowIndex
     *            行顺序
     * @param columnIndex
     *            字段顺序
     * @return 字段值转换成的浮点型
     */
    public float getFloat(int rowIndex, int columnIndex) {// NO_UCD
        return getDataRow(rowIndex).getFloat(columnIndex);
    }

    /**
     * 获取指定顺序的行上的指定字段的值，并转化为浮点型。 如果字段值为null，则返回0
     *
     * @param rowIndex
     *            行顺序
     * @param columnName
     *            字段名称
     * @return 字段值转换成的浮点型
     */
    public float getFloat(int rowIndex, String columnName) {// NO_UCD
        return getDataRow(rowIndex).getFloat(columnName);
    }
    /**
     * 获取指定顺序的行上的指定顺序的字段的值，并转化为日期类型。
     *
     * @param rowIndex
     *            行顺序
     * @param columnIndex
     *            字段顺序
     * @return 字段值转换成的日期类型
     */
    public Date getDate(int rowIndex, int columnIndex) {// NO_UCD
        return getDataRow(rowIndex).getDate(columnIndex);
    }

    /**
     * 获取指定顺序的行上的指定字段的值，并转化为日期类型。
     *
     * @param rowIndex
     *            行顺序
     * @param columnName
     *            字段名称
     * @return 字段值转换成的日期类型
     */
    public Date getDate(int rowIndex, String columnName) {
        return getDataRow(rowIndex).getDate(columnName);
    }

    public LocalDate getLocalDate(int rowIndex, String columnName) {
        return getDataRow(rowIndex).getLocalDate(columnName);
    }

    /**
     * @param rowIndex
     *            行顺序
     * @return 指定行顺序上的数据行实例
     */
    public DataRow getDataRow(int rowIndex) {
        if (rowIndex >= rows.size() || rowIndex < 0) {
            throw new UtilityException("Index is out of range:" + rowIndex);
        }
        return rows.get(rowIndex);
    }

    /**
     * @param columnIndex
     *            字段顺序
     * @return 指定顺序的字段信息实例
     */
    public DataColumn getDataColumn(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columns.length) {
            throw new UtilityException("Index is out of range:" + columnIndex);
        }
        return columns[columnIndex];
    }

    /**
     * @param columnName
     *            字段名称
     * @return 指定字段的字段信息实例
     */
    public DataColumn getDataColumn(String columnName) {
        int i = getColumnIndex(columnName);
        if (i == -1) {
            return null;
        }
        return columns[i];
    }

    /**
     * 获取指定顺序的字段在所有行上的值
     *
     * @param columnIndex
     *            字段顺序
     * @return 各行的值组成的数组
     */
    public Object[] getColumnValues(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columns.length) {
            throw new UtilityException("Index is out of range:" + columnIndex);
        }
        Object[] arr = new Object[getRowCount()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rows.get(i).values.get(columnIndex);
        }
        return arr;
    }

    /**
     * 获取指定字段在所有行上的值
     *
     * @param columnName
     *            字段名称
     * @return 各行的值组成的数组
     */
    public Object[] getColumnValues(String columnName) {
        int i = getColumnIndex(columnName);
        if (i < 0) {
            return null;
        }
        return getColumnValues(i);
    }

    /**
     * 根据比较器对数据行进行排序
     *
     * @param c
     *            比较器
     */
    public void sort(Comparator<DataRow> c) {
        Collections.sort(rows, c);
    }

    /**
     * 将DataTable中的数据行按指定字段进行逆序排列
     *
     * @param columnName
     *            字段名称
     */
    public void sort(String columnName) {
        sort(columnName, "desc", false);
    }

    /**
     * 将DataTable中的数据行按指定字段和排序方向进行排序
     *
     * @param columnName
     *            字段名称
     * @param order
     *            排序方向，ASC顺序，DESC逆序
     */
    public void sort(String columnName, String order) {
        sort(columnName, order, false);
    }

    /**
     * 将DataTable中的数据行按指定字段和排序方向进行排序
     *
     * @param columnName
     *            字段名称
     * @param order
     *            排序方向，ASC顺序，DESC逆序
     * @param isNumber
     *            将字段的值作为数字进行排序
     */
    public void sort(String columnName, String order, final boolean isNumber) {
        final String cn = columnName;
        final String od = order;
        sort(new Comparator<DataRow>() {
            @Override
            public int compare(DataRow dr1, DataRow dr2) {
                Object v1 = dr1.get(cn);
                Object v2 = dr2.get(cn);
                if (v1 instanceof Number && v2 instanceof Number) {
                    double d1 = ((Number) v1).doubleValue();
                    double d2 = ((Number) v2).doubleValue();
                    if (d1 == d2) {
                        return 0;
                    } else if (d1 > d2) {
                        return "asc".equalsIgnoreCase(od) ? 1 : -1;
                    } else {
                        return "asc".equalsIgnoreCase(od) ? -1 : 1;
                    }
                } else if (v1 instanceof Date && v2 instanceof Date) {
                    Date d1 = (Date) v1;
                    Date d2 = (Date) v2;
                    if ("asc".equalsIgnoreCase(od)) {
                        return d1.compareTo(d2);
                    } else {
                        return -d1.compareTo(d2);
                    }
                } else if (isNumber) {
                    double d1 = 0, d2 = 0;
                    try {
                        d1 = Double.parseDouble(String.valueOf(v1));
                        d2 = Double.parseDouble(String.valueOf(v2));
                    } catch (Exception e) {
                    }
                    if (d1 == d2) {
                        return 0;
                    } else if (d1 > d2) {
                        return "asc".equalsIgnoreCase(od) ? -1 : 1;
                    } else {
                        return "asc".equalsIgnoreCase(od) ? 1 : -1;
                    }
                } else {
                    int c = dr1.getString(cn).compareTo(dr2.getString(cn));
                    if ("asc".equalsIgnoreCase(od)) {
                        return c;
                    } else {
                        return -c;
                    }
                }
            }
        });
    }

    /**
     * 过滤掉部分记录后生成一个新的DataTable
     *
     * @param filter
     *            过滤器
     * @return 过滤后的新的DataTable实例
     */
    public DataTable filter(Filter<DataRow> filter) {
        DataTable dt = new DataTable(columns, null);
        dt.setWebMode(isWebMode);
        for (DataRow row : rows) {
            if (filter.filter(row)) {
                dt.insertRow(row.clone());
            }
        }
        return dt;
    }

    /**
     * 克隆DataTable
     */
    @Override
    public DataTable clone() {
        DataColumn[] dcs = new DataColumn[columns.length];
        for (int i = 0; i < columns.length; i++) {
            dcs[i] = (DataColumn) columns[i].clone();
        }
        DataTable dt = new DataTable(dcs, null);
        for (DataRow dr : rows) {
            @SuppressWarnings("unchecked")
            ArrayList<Object> values = (ArrayList<Object>) dr.values.clone();
            dt.insertRow(new DataRow(dt, values));
        }
        dt.setWebMode(isWebMode);
        return dt;
    }

    /**
     * 以指定名称对应的字段的值为key,以另一名称对应的字段的值为value,填充到一个Mapx中，并返回此Mapx
     *
     * @param keyColumnName
     *            作为键的字段的名称
     * @param valueColumnName
     *            作为值的字段的名称
     * @return Mapx
     */
    public Mapx<String, Object> toMapx(String keyColumnName, String valueColumnName) {
        int keyIndex = 0, valueIndex = 0;
        if ((keyIndex = getColumnIndex(keyColumnName)) == -1) {
            throw new UtilityException("Key column name not found:" + keyColumnName);
        }
        if ((valueIndex = getColumnIndex(valueColumnName)) == -1) {
            throw new UtilityException("Value column name not found:" + valueColumnName);
        }
        return toMapx(keyIndex, valueIndex);
    }

    /**
     * 以指定顺序的字段的值为key,以另一指定顺序的字段的值为value,填充到一个Mapx中，并返回此Mapx
     *
     * @param keyColumnIndex
     *            作为键的字段的顺序
     * @param valueColumnIndex
     *            作为值的字段的顺序
     * @return Mapx
     */
    public Mapx<String, Object> toMapx(int keyColumnIndex, int valueColumnIndex) {
        if (keyColumnIndex < 0 || keyColumnIndex >= columns.length) {
            throw new UtilityException("Key index is out of range:" + keyColumnIndex);
        }
        if (valueColumnIndex < 0 || valueColumnIndex >= columns.length) {
            throw new UtilityException("Value index is out of range:" + valueColumnIndex);
        }
        Mapx<String, Object> map = new CaseIgnoreMapx<String, Object>();
        for (DataRow row : rows) {
            Object key = row.values.get(keyColumnIndex);
            if (key == null) {
                map.put(null, row.values.get(valueColumnIndex));
            } else {
                map.put(key.toString(), row.values.get(valueColumnIndex));
            }
        }
        return map;
    }

    /**
     * 以字段名对应的字段的值为key，去map中寻找对应的值，并把值置到新增的列中，新增列的列名=指定列列名+"Name"
     *
     * @param columnName
     *            字段名称
     * @param map
     *            Map
     * @return DataTable本身
     */
    public DataTable decodeColumn(String columnName, Map<?, ?> map) {
        return decodeColumn(getColumnIndex(columnName), map);
    }

    /**
     * 以指定顺序的字段的值为key，去map中寻找对应的值，并把值置到新增的列中，新增列的列名=指定列列名+"Name"
     *
     * @param columnIndex
     *            字段顺序
     * @param map
     *            Map
     * @return DataTable本身
     */
    public DataTable decodeColumn(int columnIndex, Map<?, ?> map) {
        if (columnIndex < 0 || columnIndex > columns.length) {
            return this;
        }
        String newName = columns[columnIndex].getColumnName() + "Name";
        int addIndex = columns.length;
        insertColumn(newName);
        for (int i = 0; i < getRowCount(); i++) {
            String v = getString(i, columnIndex);

            if (!StringUtil.isEmpty(v)) {
                String[] values = v.split(",");
                String decodeValue = "";
                int index = 0;
                for (String string : values) {
                    if (index++ != 0) {
                        decodeValue += ",";
                    }
                    decodeValue += map.get(string);
                }
                set(i, addIndex, decodeValue);
            }

        }
        return this;
    }

    /**
     * 将指定DataTable中的数据行合并到本实例中
     *
     * @param anotherDT
     *            待合并的DataTable
     */
    public void union(DataTable anotherDT) {
        if (anotherDT.getRowCount() == 0) {
            return;
        }
        if (getColumnCount() != anotherDT.getColumnCount()) {
            throw new UtilityException("This's column count is " + getColumnCount()
                    + " ,but parameter's column column count is " + anotherDT.getColumnCount());
        }
        rows.addAll(anotherDT.rows);
        for (DataRow dr : anotherDT.rows) {
            dr.table = this;
        }
    }

    /**
     * 将DataTable分页
     *
     * @param pageSize
     *            分页大小
     * @param pageIndex
     *            第几页，0为第一页
     * @return 分页后的DataTable
     */
    public DataTable getPagedDataTable(int pageSize, int pageIndex) {
        DataTable dt = new DataTable(columns, null);
        for (int i = pageIndex * pageSize; i < (pageIndex + 1) * pageSize && i < rows.size(); i++) {
            dt.insertRow(rows.get(i));
        }
        return dt;
    }

    public boolean isEmpty() {
        return getRowCount() == 0;
    }

    /**
     * @return 数据行数量
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * 等同于getColumnCount()
     *
     * @return 字段数量
     */
    @Deprecated
    public int getColCount() {
        return columns.length;
    }

    /**
     * @return 字段数量
     */
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * @return 所有的字段信息
     */
    public DataColumn[] getDataColumns() {
        return columns;
    }

    /**
     * @return 是否是web模式。在web模式下getString()方法会将null或空字符串转化成&amp;nbsp;输出
     */
    public boolean isWebMode() {
        return isWebMode;
    }

    /**
     * 设置是否是web模式，在web模式下getString()方法会将null或空字符串转化成&amp;nbsp;输出
     *
     * @param isWebMode
     *            web模式
     * @return DataTable本身
     */
    public DataTable setWebMode(boolean isWebMode) {
        this.isWebMode = isWebMode;
        return this;
    }

    // public String __toString() {
    // return DataTableUtil.dataTableToTxt(this, null, "\t", "\n");
    // }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String[] columnNames = new String[getColumnCount()];
        for (int i = 0; i < columnNames.length; i++) {
            if (i != 0) {
                sb.append("\t");
            }
            sb.append(columns[i].getColumnName());
        }
        sb.append("\n");
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                if (j != 0) {
                    sb.append("\t");
                }
                sb.append(StringUtil.javaEncode(getString(i, j)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * @param columnName
     *            字段名称
     * @return 是否包含指定的字段名称
     */
    public boolean containsColumn(String columnName) {
        return getColumnIndex(columnName) != -1;
    }

    /**
     * 数据行遍历器
     */
    @Override
    public Iterator<DataRow> iterator() {
        final DataTable dt = this;
        return new Iterator<DataRow>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return dt.getRowCount() > i;
            }

            @Override
            public DataRow next() {
                return dt.getDataRow(i++);
            }

            @Override
            public void remove() {
                dt.deleteRow(i);
            }
        };
    }

    public DataRow[] select(String whereString) {
        return filter(whereString).getDataRows();
    }

    /**
     * 根据where条件过滤数据
     *
     * @param whereString
     *            如：“ name='darkness' and password='sky' ”
     * @return
     */
    public DataTable filter(String whereString) {
        final String[] params = whereString.split("and");

        return filter(new Filter<DataRow>() {

            public boolean filter(DataRow dr) {
                for (int i = 0; i < params.length; i++) {
                    String[] paramInfo = params[i].split("=");
                    if (!dr.getString(paramInfo[0]).equals(paramInfo[1].replaceAll("\'", ""))) {
                        return false;
                    }
                }

                return true;

            }

        });
    }

    public DataRow[] Select(String m_where, String m_order) {
        return rows.toArray(new DataRow[0]);
    }
    public DataRow[] getDataRows() {
        return rows.toArray(new DataRow[0]);
    }

    public DataRow[] getDataRows(int start, int limit) {
        DataRow[] result = new DataRow[limit];

        int currentIndex = 0;
        for (int i = start; i < (start + limit); i++) {
            result[currentIndex++] = rows.get(i);
        }

        return result;
    }

    /**
     * 清空数据
     */
    public void clear() {
        rows = new ArrayList<>();
    }

    /**
     * 获得字段名对应的字段的顺序
     *
     * @param columnName
     *            字段名
     * @return 字段顺序
     */
    public int getColumnIndex(String columnName) {
        int hash = CaseIgnoreMapx.caseIgnoreHash(columnName);
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].hash == hash) {
                return i;
            }
        }
        return -1;
    }

}
