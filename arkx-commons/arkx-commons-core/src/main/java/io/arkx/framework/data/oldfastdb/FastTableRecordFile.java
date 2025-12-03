package io.arkx.framework.data.oldfastdb;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.arkx.framework.commons.collection.ThreeTuple;
import io.arkx.framework.commons.collection.TwoTuple;
import io.arkx.framework.commons.util.TimeWatch;
import io.arkx.framework.data.fasttable.FastColumn;
import io.arkx.framework.data.fasttable.FastTableHeader;
import io.arkx.framework.data.fasttable.MappedFile;
import io.arkx.framework.data.oldfastdb.BatchUtil.PageInfo;

import com.google.common.collect.Multimap;

/**
 * @author Darkness
 * @date 2015年12月19日 下午5:10:11
 * @version V1.0
 * @since infinity 1.0
 */
public class FastTableRecordFile extends MappedFile {

    protected static final byte[] FILE_TYPE = new byte[]{'L', 'T', 'R'};

    public FastTableRecordFile(String path) {
        super(path);
    }

    public FastTableRecordFile(String path, boolean isAppend) {
        super(path, isAppend);
    }

    public <T extends IFastTable> T readDataTable(FastTableHeader tableInfo, Class<T> lightningTableType) {
        return readDataTable(tableInfo, lightningTableType, null, new ArrayList<Integer>());
    }

    public <T extends IFastTable> T readDataTable(FastTableHeader tableInfo, Class<T> lightningTableType,
            FilterInfo filterInfo, List<Integer> loadRecords) {
        if (!exists()) {
            try {
                return lightningTableType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        IFastTableConvertor<T> lightningTableConvertor = FastTableConvertRegister.get(lightningTableType);

        T dt = null;
        try {
            // openReadFileChannel();

            long rowSizeLength = readLong(fileChannel);

            List<FastColumn> columns = tableInfo.columns();

            dt = lightningTableConvertor.createTable(lightningTableType, tableInfo.getTableName(),
                    columns.toArray(new FastColumn[0]));

            // long rowSizeLength = tableInfo.rowSize;

            int rowDataLength = FastTableHeader.caculateRowLength(tableInfo.columns());
            if (!loadRecords.isEmpty()) {
                for (int i : loadRecords) {
                    RecordUnit recordUnit = readRow(tableInfo, fileChannel, rowDataLength, i - 1, filterInfo);
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

    protected RecordUnit readRow(FastTableHeader tableInfo, FileChannel fileChannel, int rowDataLength, int recordStart,
            FilterInfo whereString) throws IOException {
        TimeWatch timeWatch = new TimeWatch();
        // timeWatch.startWithTaskName("read row:recordStart:"+recordStart);
        // int headerLength = tableInfo.length;
        // fileChannel.position(INT_LENGTH + headerLength + recordStart *
        // rowDataLength);
        fileChannel.position(LONG_LENGTH + (recordStart) * rowDataLength);
        // timeWatch.stopAndPrint();
        MappedByteBuffer rowBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY,
                LONG_LENGTH + (recordStart) * rowDataLength, rowDataLength);
        // ByteBuffer rowBuffer = ByteBuffer.allocate(rowDataLength);
        // fileChannel.read(rowBuffer);
        // rowBuffer.position(0);

        RecordUnit recordUnit = new RecordUnit(rowBuffer);
        boolean isNeedRead = true;// new RecordFilter().filter(recordUnit, tableInfo,
                                  // whereString);

        if (!isNeedRead) {
            return null;
        }

        return recordUnit;
    }

    @SuppressWarnings("unchecked")
    public <T extends IFastTable> TwoTuple<PkList, Map<String, Multimap<Object, Integer>>> save(T dt) {
        Map<String, Multimap<Object, Integer>> allIndexs = new HashMap<>();

        FastColumn[] columns = dt.getLightningColumns();

        FastColumn rowIndexColumn = FastColumn.intColumn("rowIndex");
        List<FastColumn> fixColumns = new ArrayList<>(columns.length + 1);
        fixColumns.add(rowIndexColumn);
        for (int i = 0; i < columns.length; i++) {
            fixColumns.add(columns[i]);
        }

        PkList pksBuffer = null;
        try {
            long existRowCount = 0;
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
                existRowCount = mappedByteBuffer.getLong();
                mappedByteBuffer.position(0);
                mappedByteBuffer.putLong(existRowCount + dt.getRowCount());
            }

            int rowDataLength = FastTableHeader.caculateRowLength(fixColumns);

            IFastTableConvertor<T> lightningTableConvertor = (IFastTableConvertor<T>) FastTableConvertRegister
                    .get(dt.getClass());

            // 第一列为主键
            int typeLength = fixColumns.get(0).getLength();
            if (typeLength == -1) {
                String value = ((FastDataTable) dt).getString(0, 0);
                byte[] stringBytes = value.getBytes();
                typeLength = stringBytes.length;
            }

            PageInfo pageInfo = BatchUtil.caculateBatchs(dt.getRowCount(), 10000);
            // System.out.println("all:" + dt.getRowCount());
            while (pageInfo.hasNext()) {
                TwoTuple<Integer, Integer> startLimit = pageInfo.next();
                int start = startLimit.first - 1;
                int limit = startLimit.second;

                FastDataTable fastDataTable = (FastDataTable) dt;
                // System.out.println("start: " + start + ", limit: " + limit);
                ThreeTuple<PkList, List<ByteBuffer>, Map<String, Multimap<Object, Integer>>> temp = lightningTableConvertor
                        .buildRowByteBuffers((int) existRowCount, fixColumns, fastDataTable.getDataRows(start, limit),
                                typeLength);
                existRowCount += limit;

                if (pksBuffer == null) {
                    pksBuffer = temp.first;
                } else {
                    PkList temppksBuffer = temp.first;
                    pksBuffer.union(temppksBuffer);
                }

                Map<String, Multimap<Object, Integer>> indexes = temp.third;
                for (String indexKey : indexes.keySet()) {
                    Multimap<Object, Integer> indexValues = indexes.get(indexKey);

                    Multimap<Object, Integer> allIndexValues = allIndexs.get(indexKey);
                    if (allIndexValues == null) {
                        allIndexs.put(indexKey, indexValues);
                    } else {
                        allIndexValues.putAll(indexValues);
                    }
                }

                List<ByteBuffer> rowBuffers = temp.second;

                ByteBuffer allRowBuffer = ByteBuffer.allocate(limit * rowDataLength);
                for (ByteBuffer rowBuffer : rowBuffers) {
                    allRowBuffer.put(rowBuffer);
                }
                MappedByteBuffer rowMappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, fileLength,
                        limit * rowDataLength);
                allRowBuffer.flip();
                rowMappedByteBuffer.put(allRowBuffer);

                fileLength += limit * rowDataLength;
            }

            fileChannel.close();
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new TwoTuple<PkList, Map<String, Multimap<Object, Integer>>>(pksBuffer, allIndexs);
    }

}
