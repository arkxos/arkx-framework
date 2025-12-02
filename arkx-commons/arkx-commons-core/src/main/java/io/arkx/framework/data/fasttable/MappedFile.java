package io.arkx.framework.data.fasttable;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * https://github.com/apache/incubator-rocketmq/blob/0c5e53db6f4d0ed9f25747379a8b679e2da5392d/store/src/main/java/org/apache/rocketmq/store/MappedFile.java
 *
 * @author Darkness
 * @date 2015年12月5日 上午10:36:48
 * @version V1.0
 * @since infinity 1.0
 */
public class MappedFile extends BufferReader {

    public static String PATH_SEPERATOR = File.separator;

    private String path;
    private long fileLength;
    private long writePosition;
    private long readPosition;

    public MappedFile(String path) {
        this(path, true);
    }

    public MappedFile(String path, boolean isAppend) {
        this.path = path;

        checkAndMakeParentDirecotry(this.path);
        File file = new File(this.path);

        if (!isAppend) {
            if (file.exists()) {
                file.delete();
            }
        }

        createIfNotExist();
    }

    public void createIfNotExist() {
        File file = new File(this.path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String path() {
        return path;
    }

    public void checkAndMakeParentDirecotry(String fullName) {
        int index = fullName.lastIndexOf(PATH_SEPERATOR);
        if (index > 0) {
            String path = fullName.substring(0, index);
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    public boolean exists() {
        File file = new File(path());
        return file.exists();
    }

    RandomAccessFile braf;
    protected FileChannel fileChannel;

    protected long getRealFileLength() {
        try {
            return braf.length();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isReadEnd() {
        return this.readPosition == this.fileLength;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void openFileChannel() throws IOException {
        this.braf = new RandomAccessFile(path(), "rw");
        this.fileChannel = braf.getChannel();

        this.writePosition = braf.length();
        this.fileLength = this.braf.length();
    }

    // public void openReadFileChannel() throws IOException {
    //// TimeWatch timeWatch = new TimeWatch();
    //// timeWatch.startWithTaskName("open file");
    // this.braf = new RandomAccessFile(path(), "r");
    // this.fileChannel = braf.getChannel();
    //
    // this.readPosition = 0;
    // this.fileLength = this.braf.length();
    //
    //// timeWatch.stopAndPrint();
    // }

    public void close() {
        try {
            if (fileChannel != null) {
                fileChannel.close();
            }
            if (braf != null) {
                braf.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long readLong() throws IOException {
        this.readPosition += 8;
        return readLong(fileChannel);
    }

    public String readString(int length) throws IOException {
        readPosition += length;
        return readString(fileChannel, length);
    }

    public long writeInt(int value) throws IOException {
        long start = this.writePosition;

        writeInt(fileChannel, value);

        return start;
    }

    public void writeLong(long value) throws IOException {
        writeLong(fileChannel, value);
    }

    public void write(ByteBuffer buffer) throws IOException {
        int length = buffer.limit();
        MappedByteBuffer rowMappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, writePosition, length);
        buffer.flip();
        rowMappedByteBuffer.put(buffer);

        closeDirectBuffer(rowMappedByteBuffer);

        writePosition += length;
    }

    public MappedByteBuffer readBuffer(int length) throws IOException {
        if (readPosition >= fileLength) {
            return null;
        }
        long readLength = length;
        if ((readPosition + length) >= fileLength) {
            readLength = fileLength - readPosition;
        }
        MappedByteBuffer rowBuffer = readBuffer(readPosition, readLength);
        readPosition += readLength;
        return rowBuffer;
    }

    public MappedByteBuffer readBuffer(long position, long length) throws IOException {
        if (position > this.fileLength - 1) {
            return null;
        }
        if ((position + length) > fileLength) {
            length = fileLength - position;
        }
        MappedByteBuffer rowBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, position, length);
        return rowBuffer;
    }

    protected void closeDirectBuffer(ByteBuffer cb) {
        if (cb == null || !cb.isDirect())
            return;

        // we could use this type cast and call functions without reflection code,
        // but static import from sun.* package is risky for non-SUN virtual machine.
        // try { ((sun.nio.ch.DirectBuffer)cb).cleaner().clean(); } catch (Exception ex)
        // { }
        try {
            Method cleaner = cb.getClass().getMethod("cleaner");
            cleaner.setAccessible(true);
            Method clean = Class.forName("sun.misc.Cleaner").getMethod("clean");
            clean.setAccessible(true);
            clean.invoke(cleaner.invoke(cb));
        } catch (Exception ex) {
        }
        cb = null;
    }
}
