package io.arkx.framework.data.fasttable;

import java.nio.ByteBuffer;

public class FastTableHeaderDecoder extends BufferReader {

    /**
     * [IDT][rowSize][tableNameLength][tableName][columnSize]([columnNameLength][columnName][columnType][columnLength]...)
     *
     * @author Darkness
     * @date 2015年12月5日 上午11:38:28
     * @version V1.0
     * @since infinity 1.0
     */
    public static FastTableHeader decode(ByteBuffer buffer) {

        {// 验证文件头
         // [AFT]
            byte fileTypeI = buffer.get();
            byte fileTypeD = buffer.get();
            byte fileTypeT = buffer.get();

            if (fileTypeI == 'I' && fileTypeD == 'D' && fileTypeT == 'T') {
            } else if (fileTypeI == 'L' && fileTypeD == 'T' && fileTypeT == 'F') {
            } else if (fileTypeI == 'L' && fileTypeD == 'T' && fileTypeT == 'D') {
            } else if (fileTypeI == 'L' && fileTypeD == 'T' && fileTypeT == 'R') {
            } else {
                // return null;
            }
        }

        FastTableHeader tableInfo = new FastTableHeader();

        // [version]
        @SuppressWarnings("unused")
        int version = buffer.getInt();

        // [自定义数据区域，128 byte]
        byte[] customData = new byte[128];
        buffer.get(customData);

        // [rowSize]
        long rowSize = buffer.getLong();
        tableInfo.setRowSize(rowSize);

        // [tableNameLength][tableName]
        int tableNameLength = buffer.getInt();
        String tableName = ByteBufferUtil.readString(buffer, tableNameLength);
        tableInfo.setTableName(tableName);

        // [columnSize]
        int columnSize = buffer.getInt();

        int columnDataIndex = 0;
        for (int i = 0; i < columnSize; i++) {
            /**
             * [columnNameLength][columnName][columnType][columnLength][columnIndexType]...)
             */
            // [columnNameLength][columnName]
            int columnNameLength = buffer.getInt();
            String columnName = ByteBufferUtil.readString(buffer, columnNameLength);

            tableInfo.setColumnDataIndex(columnName, columnDataIndex);

            // [columnType]
            byte columnTypeCode = buffer.get();
            FastColumnType columnType = FastColumnType.valueOf(columnTypeCode);

            // [columnLength]
            int columnLength = columnType.length();
            columnDataIndex += columnLength;

            if (columnType == FastColumnType.String || columnType == FastColumnType.FixedString) {
                columnLength = buffer.getInt();

                if (columnType == FastColumnType.String) {
                    columnDataIndex += INT_LENGTH;
                }
            }

            // [columnIndexType]
            byte columnIndexTypeValue = buffer.get();
            FastColumnIndexType columnIndexType = FastColumnIndexType.valueOf(columnIndexTypeValue);

            FastColumn column = new FastColumn(columnName, columnType, columnLength, columnIndexType);

            tableInfo.addColumn(column);
        }

        // tableInfo.length = buffer.limit();

        return tableInfo;
    }

}
