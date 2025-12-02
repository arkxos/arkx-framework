package io.arkx.framework.data.lightning;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.arkx.framework.commons.collection.TwoTuple;
import io.arkx.framework.data.fasttable.MappedFile;

/**
 *
 * @author Darkness
 * @date 2015年12月19日 下午5:10:11
 * @version V1.0
 * @since infinity 1.0
 */
public class LightningTableRecordFile extends MappedFile {

    protected static final byte[] FILE_TYPE = new byte[]{'L', 'T', 'R'};

    public LightningTableRecordFile(String path) {
        super(path);
    }

    public LightningTableRecordFile(String path, boolean isAppend) {
        super(path, isAppend);
    }

    public <T extends ILightningTable> T readDataTable(TableInfo tableInfo, Class<T> lightningTableType) {
        return readDataTable(tableInfo, lightningTableType, null, new ArrayList<Integer>());
    }

    public <T extends ILightningTable> T readDataTable(TableInfo tableInfo, Class<T> lightningTableType,
            FilterInfo filterInfo, List<Integer> loadRecords) {
        if (!exists()) {
            try {
                return lightningTableType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        ILightningTableConvertor<T> lightningTableConvertor = LightningTableConvertRegister.get(lightningTableType);

        T dt = null;
        try {
            // openReadFileChannel();

            long rowSizeLength = readLong(fileChannel);

            List<LightningColumn> columns = Arrays.asList(tableInfo.columns());

            dt = lightningTableConvertor.createTable(lightningTableType, tableInfo.tableName,
                    columns.toArray(new LightningColumn[0]));

            // long rowSizeLength = tableInfo.rowSize;

            int rowDataLength = TableInfo.caculateRowLength(tableInfo.columns());
            if (!loadRecords.isEmpty()) {
                for (int i : loadRecords) {
                    RecordUnit recordUnit = readRow(tableInfo, fileChannel, rowDataLength, i, filterInfo);
                    if (recordUnit == null) {
                        continue;
                    }
                    lightningTableConvertor.onRecordReaded(columns, dt, recordUnit);
                }
            } else {
                for (int i = 0; i < rowSizeLength; i++) {
                    RecordUnit recordUnit = readRow(tableInfo, fileChannel, rowDataLength, i, filterInfo);
                    if (recordUnit == null) {
                        continue;
                    }
                    lightningTableConvertor.onRecordReaded(columns, dt, recordUnit);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return dt;
    }

    protected RecordUnit readRow(TableInfo tableInfo, FileChannel fileChannel, int rowDataLength, int recordStart,
            FilterInfo whereString) throws IOException {

        // int headerLength = tableInfo.length;
        // fileChannel.position(INT_LENGTH + headerLength + recordStart *
        // rowDataLength);
        fileChannel.position(LONG_LENGTH + recordStart * rowDataLength);

        ByteBuffer rowBuffer = ByteBuffer.allocate(rowDataLength);
        fileChannel.read(rowBuffer);
        rowBuffer.position(0);

        RecordUnit recordUnit = new RecordUnit(rowBuffer);
        boolean isNeedRead = new RecordFilter().filter(recordUnit, tableInfo, whereString);

        if (!isNeedRead) {
            return null;
        }

        return recordUnit;
    }

    @SuppressWarnings("unchecked")
    public <T extends ILightningTable> PkList save(T dt) {
        LightningColumn[] columns = dt.getLightningColumns();

        PkList pksBuffer = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(path(), "rw");
            FileChannel fileChannel = raf.getChannel();
            long fileLength = raf.length();
            if (fileLength == 0) {// 文件不存在，写入文件头
                int rowCount = dt.getRowCount();
                MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, LONG_LENGTH);
                mappedByteBuffer.putLong(rowCount);

                fileLength = LONG_LENGTH;
            } else {// 文件存在，修改记录数
                MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, LONG_LENGTH);
                long existRowCount = mappedByteBuffer.getLong();
                mappedByteBuffer.position(0);
                mappedByteBuffer.putLong(existRowCount + dt.getRowCount());
            }

            int rowDataLength = TableInfo.caculateRowLength(columns);
            ByteBuffer allRowBuffer = ByteBuffer.allocate(dt.getRowCount() * rowDataLength);

            ILightningTableConvertor<T> lightningTableConvertor = (ILightningTableConvertor<T>) LightningTableConvertRegister
                    .get(dt.getClass());

            TwoTuple<PkList, List<ByteBuffer>> temp = lightningTableConvertor.buildRowByteBuffers(columns, dt);

            pksBuffer = temp.first;

            List<ByteBuffer> rowBuffers = temp.second;

            for (ByteBuffer rowBuffer : rowBuffers) {
                allRowBuffer.put(rowBuffer);
            }
            MappedByteBuffer rowMappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, fileLength,
                    dt.getRowCount() * rowDataLength);
            allRowBuffer.flip();
            rowMappedByteBuffer.put(allRowBuffer);

            fileLength += rowDataLength;

            fileChannel.close();
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pksBuffer;
    }

}
