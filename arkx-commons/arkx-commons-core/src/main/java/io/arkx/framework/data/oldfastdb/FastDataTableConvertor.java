package io.arkx.framework.data.oldfastdb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.*;

import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.commons.collection.ThreeTuple;
import io.arkx.framework.data.fasttable.FastColumn;
import io.arkx.framework.data.fasttable.FastColumnIndexType;
import io.arkx.framework.data.fasttable.FastColumnType;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * 文件头
 * [IDT][tableNameLength][tableName][rowSize][columnSize]([columnNameLength][columnName][columnType][columnLength]...)
 * 数据
 * [intValue][floatValue][doubleValue][fixedStringValue]([stringLength][stringValue][empty])...
 *
 * 数据行加一个标志，删除的时候，设置当前标识为删除状态，数据库引擎定时清除删除状态的数据，重新构建文件
 *
 * @author Darkness
 * @date 2015年11月8日 下午4:14:02
 * @version V1.0
 * @since infinity 1.0
 */
public class FastDataTableConvertor implements IFastTableConvertor<FastDataTable> {

    @Override
    public FastDataTable createTable(Class<FastDataTable> type, String tableName, FastColumn[] columns) {
        List<DataColumn> dataColumns = new ArrayList<>();

        for (FastColumn lightningColumn : columns) {
            DataTypes dataColumnType = DataTypes.valueOf(lightningColumn.getType().code());

            DataColumn dataColumn = new DataColumn(lightningColumn.getName(), dataColumnType,
                    lightningColumn.getLength());
            dataColumns.add(dataColumn);
        }

        try {
            FastDataTable instance = type.newInstance();
            instance.init(tableName, dataColumns.toArray(new DataColumn[0]));
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onRecordReaded(List<FastColumn> columns, FastDataTable dt, RecordUnit rowBuffer) throws IOException {
        Object[] rowDatas = rowBuffer.readDatas(columns);

        if (rowDatas != null) {
            dt.insertRow(rowDatas);
        }
    }

    /**
     * 计算行长度
     *
     * @author Darkness
     * @date 2015年12月5日 下午1:31:53
     * @version V1.0
     * @since infinity 1.0
     */
    protected static int caculateRowLength(List<FastColumn> columns) {
        int result = 0;
        for (FastColumn column : columns) {
            if (column.getType() == FastColumnType.String) {
                result += 4;// INT_LENGTH;
            }
            result += column.getLength();
        }
        return result;
    }

    protected static String readString(ByteBuffer buffer, int length) {
        byte[] columnNameBytes = new byte[length];
        buffer.get(columnNameBytes);

        return new String(columnNameBytes);
    }

    /**
     * [intValue][floatValue][doubleValue][fixedStringValue]([stringLength][stringValue][empty])...
     *
     * @author Darkness
     * @date 2015年12月5日 下午1:11:15
     * @version V1.0
     * @since infinity 1.0
     */
    private Multimap<Object, Integer> getIndex(String columnName,
            Map<String, Multimap<Object, Integer>> columnIndexes) {
        if (columnIndexes.containsKey(columnName)) {
            return columnIndexes.get(columnName);
        }
        Multimap<Object, Integer> indexes = HashMultimap.create();
        columnIndexes.put(columnName, indexes);
        return indexes;
    }
    @Override
    public ThreeTuple<PkList, List<ByteBuffer>, Map<String, Multimap<Object, Integer>>> buildRowByteBuffers(
            int existRowCount, List<FastColumn> columns, DataRow[] rows, int typeLength) {
        // DataRow[] rows = dt.getDataRows();

        List<ByteBuffer> rowBuffers = new ArrayList<>();

        int rowDataLength = caculateRowLength(columns);

        Map<String, Multimap<Object, Integer>> columnIndexes = new HashMap<>();

        int index = 1;
        for (DataRow row : rows) {
            ByteBuffer rowbuffer = ByteBuffer.allocate(rowDataLength);
            rowBuffers.add(rowbuffer);
            boolean isFirst = true;

            int currentRowIndex = existRowCount + index++;
            for (FastColumn column : columns) {
                if (isFirst) {
                    isFirst = false;
                    rowbuffer.putInt(currentRowIndex);
                    continue;
                }

                if (column.getType() == FastColumnType.Int) {
                    int value = row.getInt(column.getName());
                    rowbuffer.putInt(value);
                } else if (column.getType() == FastColumnType.Float) {
                    float value = row.getFloat(column.getName());
                    rowbuffer.putFloat(value);
                } else if (column.getType() == FastColumnType.Double) {
                    double value = row.getDouble(column.getName());
                    rowbuffer.putDouble(value);
                } else if (column.getType() == FastColumnType.Long) {
                    long value = row.getLong(column.getName());
                    rowbuffer.putLong(value);
                } else if (column.getType() == FastColumnType.Date) {
                    LocalDate value = row.getLocalDate(column.getName());
                    rowbuffer.putLong(value.toEpochDay());

                    if (column.getIndexType() == FastColumnIndexType.Index) {
                        getIndex(column.getName(), columnIndexes).put(value.toEpochDay(), currentRowIndex);
                    }
                } else if (column.getType() == FastColumnType.DateTime) {
                    Date value = row.getDate(column.getName());
                    rowbuffer.putLong(value.getTime());
                } else if (column.getType() == FastColumnType.FixedString) {
                    String value = row.getString(column.getName());
                    byte[] stringBytes = value.getBytes();
                    if (stringBytes.length < column.getLength()) {
                        rowbuffer.put(stringBytes);
                        int emptyCount = column.getLength() - stringBytes.length;
                        byte emptyByte = 32;// " ".getBytes()
                        for (int i = 0; i < emptyCount; i++) {
                            rowbuffer.put(emptyByte);
                        }
                    } else {
                        if (stringBytes.length > column.getLength()) {
                            for (int i = 0; i < column.getLength(); i++) {
                                rowbuffer.put(stringBytes[i]);
                            }
                        } else {
                            rowbuffer.put(stringBytes);
                        }
                    }

                    if (column.getIndexType() == FastColumnIndexType.Index) {
                        getIndex(column.getName(), columnIndexes).put(value, currentRowIndex);
                    }
                } else if (column.getType() == FastColumnType.String) {
                    String value = row.getString(column.getName());
                    byte[] stringBytes = value.getBytes();
                    if (stringBytes.length < column.getLength()) {
                        rowbuffer.putInt(stringBytes.length);
                        rowbuffer.put(stringBytes);
                        int emptyCount = column.getLength() - stringBytes.length;
                        byte emptyByte = 32;// " ".getBytes()
                        for (int i = 0; i < emptyCount; i++) {
                            rowbuffer.put(emptyByte);
                        }
                    } else {
                        rowbuffer.putInt(column.getLength());
                        if (stringBytes.length > column.getLength()) {
                            for (int i = 0; i < column.getLength(); i++) {
                                rowbuffer.put(stringBytes[i]);
                            }
                        } else {
                            rowbuffer.put(stringBytes);
                        }
                    }
                }
            }

            rowbuffer.position(0);
        }

        PkList pkList = new PkList(columns.get(0), typeLength, rows.length);
        pkList.addAll(rows, columns.get(0));

        return new ThreeTuple<>(pkList, rowBuffers, columnIndexes);
    }

}
