package io.arkx.framework.data.lightning;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import io.arkx.framework.data.fasttable.BufferReader;
import io.arkx.framework.data.fasttable.ByteBufferUtil;

public class TableInfoReader extends BufferReader {

    /**
     * [IDT][rowSize][tableNameLength][tableName][columnSize]([columnNameLength][columnName][columnType][columnLength]...)
     *
     * @author Darkness
     * @date 2015年12月5日 上午11:38:28
     * @version V1.0
     * @since infinity 1.0
     */
    public TableInfo buildTableInfo(ByteBuffer buffer) {

        {// 验证文件头
         // [IDT]
            byte fileTypeI = buffer.get();
            byte fileTypeD = buffer.get();
            byte fileTypeT = buffer.get();

            if (fileTypeI == 'I' && fileTypeD == 'D' && fileTypeT == 'T') {
                // 兼容老版本 'IDT;
            } else if (fileTypeI == 'L' && fileTypeD == 'T' && fileTypeT == 'F') {
            } else if (fileTypeI == 'L' && fileTypeD == 'T' && fileTypeT == 'D') {
            } else if (fileTypeI == 'L' && fileTypeD == 'T' && fileTypeT == 'R') {
            } else {
                // return null;
            }

        }

        TableInfo tableInfo = new TableInfo();

        // [rowSize]
        long rowSize = buffer.getLong();
        tableInfo.rowSize = rowSize;

        // [tableNameLength][tableName]
        int tableNameLength = buffer.getInt();
        String tableName = ByteBufferUtil.readString(buffer, tableNameLength);
        tableInfo.tableName = tableName;

        // [columnSize]
        int columnSize = buffer.getInt();

        int columnDataIndex = 0;
        for (int i = 0; i < columnSize; i++) {
            /**
             * [columnNameLength][columnName][columnType][columnLength]...)
             */
            // [columnNameLength][columnName]
            int columnNameLength = buffer.getInt();
            String columnName = ByteBufferUtil.readString(buffer, columnNameLength);

            tableInfo.setColumnDataIndex(columnName, columnDataIndex);

            // [columnType]
            byte columnTypeCode = buffer.get();
            LightningColumnType columnType = LightningColumnType.valueOf(columnTypeCode);

            // [columnLength]
            int columnLength = columnType.length();
            columnDataIndex += columnLength;

            if (columnType == LightningColumnType.STRING || columnType == LightningColumnType.FIXED_STRING) {
                columnLength = buffer.getInt();

                if (columnType == LightningColumnType.STRING) {
                    columnDataIndex += INT_LENGTH;
                }
            }

            LightningColumn column = new LightningColumn(columnName, columnType, columnLength);

            tableInfo.addColumn(column);
        }

        tableInfo.length = buffer.limit();

        return tableInfo;
    }

    public TableInfo readTableInfo(FileChannel fileChannel) {
        try {
            int tableInfoLength = readInt(fileChannel);
            ByteBuffer tableInfoByteBuffer = ByteBuffer.allocate(tableInfoLength);
            fileChannel.read(tableInfoByteBuffer);
            tableInfoByteBuffer.flip();
            return this.buildTableInfo(tableInfoByteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
