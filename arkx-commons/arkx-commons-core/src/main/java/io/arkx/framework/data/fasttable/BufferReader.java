package io.arkx.framework.data.fasttable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BufferReader {

	public static int BYTE_LENGTH = 1;

	public static int INT_LENGTH = 4;

	public static int FLOAT_LENGTH = 4;

	public static int LONG_LENGTH = 8;

	public static int DOUBLE_LENGTH = 8;

	private ByteBuffer longBuffer = ByteBuffer.allocate(LONG_LENGTH);

	private ByteBuffer doubleBuffer = ByteBuffer.allocate(DOUBLE_LENGTH);

	private ByteBuffer intBuffer = ByteBuffer.allocate(INT_LENGTH);

	private ByteBuffer floatBuffer = ByteBuffer.allocate(FLOAT_LENGTH);

	private ByteBuffer longRBuffer = ByteBuffer.allocate(LONG_LENGTH);

	private ByteBuffer doubleRBuffer = ByteBuffer.allocate(DOUBLE_LENGTH);

	private ByteBuffer intRBuffer = ByteBuffer.allocate(INT_LENGTH);

	private ByteBuffer floatRBuffer = ByteBuffer.allocate(FLOAT_LENGTH);

	public void writeInt(FileChannel fileChannel, int value) throws IOException {
		intRBuffer.clear();
		intRBuffer.putInt(value);
		intRBuffer.position(0);

		fileChannel.write(intRBuffer);
	}

	public void writeLong(FileChannel fileChannel, long value) throws IOException {
		longRBuffer.clear();
		longRBuffer.putLong(value);
		longRBuffer.position(0);

		fileChannel.write(longRBuffer);
	}

	public long readLong(FileChannel fileChannel) throws IOException {
		longBuffer.clear();

		fileChannel.read(longBuffer);
		longBuffer.position(0);

		return longBuffer.getLong();
	}

	public int readInt(FileChannel fileChannel) throws IOException {
		intBuffer.clear();

		fileChannel.read(intBuffer);
		intBuffer.position(0);

		return intBuffer.getInt();
	}

	public float readFloat(FileChannel fileChannel) throws IOException {
		floatBuffer.clear();

		fileChannel.read(floatBuffer);
		floatBuffer.position(0);

		return floatBuffer.getFloat();
	}

	public double readDouble(FileChannel fileChannel) throws IOException {
		doubleBuffer.clear();

		fileChannel.read(doubleBuffer);
		doubleBuffer.position(0);

		return doubleBuffer.getDouble();
	}

	public String readString(FileChannel fileChannel, int length) throws IOException {
		ByteBuffer stringBuffer = ByteBuffer.allocate(length);

		fileChannel.read(stringBuffer);
		stringBuffer.position(0);

		return ByteBufferUtil.readString(stringBuffer, length);
	}

}
