package io.arkx.framework.commons.util;

import java.util.Arrays;

/**
 * @author Darkness
 * @date 2016年10月29日 下午6:29:53
 * @version V1.0
 */
public class Bitmap {

	public static void main(String[] args) {
		Bitmap bitmap = new Bitmap(18);

		bitmap.set(5); // 0000 0100 0000 0000 0000 0000
		bitmap.set(10); // 0000 0100 0000 1000 0100 0000
		bitmap.set(15); // 0000 0100 0000 0000 0100 0000
		bitmap.set(17);
		bitmap.dump();

		bitmap.set(12);
		bitmap.dump();
		bitmap.clear(12);
		bitmap.dump();
		System.out.println(bitmap.get(10));
		System.out.println(bitmap.get(12));
		System.out.println(bitmap.get(17));
	}

	public static final int TRUE = 1;

	public static final int FALSE = 0;

	/**
	 * Byte-shifted position represents index of the bitmap array
	 */
	protected final int BYTE_SHIFT = 3;

	/**
	 * Values are stored in a byte array
	 */
	protected final byte[] bitmap;

	/**
	 * Number of elements in bitmap
	 */
	protected final int size;

	public Bitmap() {
		this(8, FALSE);
	}

	public Bitmap(int size) {
		this(size, FALSE);
	}

	public Bitmap(byte[] bitmap) {
		this.bitmap = Arrays.copyOf(bitmap, bitmap.length);
		size = bitmap.length * 8; // length << BYTE_SHIFT
	}

	/**
	 * Create a bitmap
	 * @param size number of elements in bitmap
	 * @param value TRUE : set all available bits to 1 FALSE : all available bits remain 0
	 */
	public Bitmap(int size, int value) {
		int bytes = (int) Math.ceil((double) size / 8);// number of bytes

		bitmap = new byte[bytes];
		this.size = size;
		if (value == TRUE)
			for (int i = 0; i < size; i++)
				set(i);
	}

	/**
	 * Set the position on bitmap to 1
	 * @param position position on bitmap
	 */
	public synchronized void set(int position) {
		if (position >= size)
			return;

		int bitOffset = position % 8;
		int byteOffset = getByteOffset(position);

		bitmap[byteOffset] |= (byte) (1 << bitOffset);
	}

	public synchronized void clear(int position) {
		if (position >= size)
			return;

		int bitOffset = position % 8;
		int byteOffset = getByteOffset(position);
		byte complement = (byte) ~(1 << bitOffset);

		bitmap[byteOffset] &= (byte) complement;
	}

	/**
	 * Get the value of the position on bitmap
	 * @param position position on bitmap
	 * @return true if the position is 1,false else
	 */
	public boolean get(int position) {
		if (position >= size)
			return false;

		int bitOffset = position % 8;
		int byteOffset = getByteOffset(position);

		return (byte) (bitmap[byteOffset] & (1 << bitOffset)) != 0;
		// this value may return -128 if the highest bit is 1(signed)
	}

	protected int getByteOffset(int position) {
		return bitmap.length - 1 - (position >> BYTE_SHIFT);
	}

	public Bitmap not() {
		for (int i = 0; i < bitmap.length; i++)
			bitmap[i] = (byte) ~bitmap[i];
		return this;
	}

	public Bitmap and(Bitmap map) {
		if (map.getSize() != size)
			return null;
		Bitmap result = new Bitmap(map.getBitmap());
		;
		byte[] byteArray = result.getBitmap();

		for (int i = 0; i < byteArray.length; i++)
			byteArray[i] &= bitmap[i];

		return result;
	}

	public Bitmap or(Bitmap map) {
		if (map.getSize() != size)
			return null;
		Bitmap result = new Bitmap(map.getBitmap());
		byte[] byteArray = result.getBitmap();

		for (int i = 0; i < byteArray.length; i++)
			byteArray[i] |= bitmap[i];

		return result;
	}

	public Bitmap xor(Bitmap map) {
		if (map.getSize() != size)
			return null;
		Bitmap result = new Bitmap(map.getBitmap());
		;
		byte[] byteArray = result.getBitmap();

		for (int i = 0; i < byteArray.length; i++)
			byteArray[i] ^= bitmap[i];

		return result;
	}

	public Bitmap nor(Bitmap map) {
		if (map.getSize() != size)
			return null;
		int size = map.getSize();
		Bitmap result = new Bitmap(size);

		for (int i = 0; i < size; i++)
			if (!get(i) && !map.get(i))
				result.set(i);

		return result;
	}

	public Bitmap xnor(Bitmap map) {
		if (map.getSize() != size)
			return null;
		int size = map.getSize();
		Bitmap result = new Bitmap(size);

		for (int i = 0; i < size; i++)
			if (get(i) == map.get(i))
				result.set(i);

		return result;
	}

	public Bitmap nand(Bitmap map) {
		if (map.getSize() != size)
			return null;
		int size = map.getSize();
		Bitmap result = new Bitmap(size);

		for (int i = 0; i < size; i++) {
			if (!(get(i) && map.get(i)))
				result.set(i);
		}

		return result;
	}

	/**
	 * NOT complement_a[i] := not a[i]
	 * @param a
	 * @return
	 */
	public static Bitmap complement(Bitmap a) {
		int size = a.getSize();

		for (int i = 0; i < size; i++)
			if (a.get(i))
				a.clear(i);
			else
				a.set(i);

		return a;
	}

	/**
	 * OR union[i] := a[i] or b[i]
	 * @param a
	 * @param b
	 * @return
	 */
	public static Bitmap union(Bitmap a, Bitmap b) {
		if (a.getSize() != b.getSize())
			return null;

		Bitmap map = new Bitmap(a.getSize());
		int size = a.getSize();

		for (int i = 0; i < size; i++)
			if (a.get(i) || b.get(i))
				map.set(i);

		return map;
	}

	/**
	 * AND intersection[i] := a[i] and b[i]
	 * @param a
	 * @param b
	 * @return
	 */
	public static Bitmap intersection(Bitmap a, Bitmap b) {
		if (a.getSize() != b.getSize())
			return null;

		Bitmap map = new Bitmap(a.getSize());
		int size = a.getSize();

		for (int i = 0; i < size; i++)
			if (a.get(i) && b.get(i))
				map.set(i);

		return map;
	}

	/**
	 * XOR difference[i] := a[i] and (not b[i])
	 * @param a
	 * @param b
	 * @return
	 */
	public static Bitmap difference(Bitmap a, Bitmap b) {
		if (a.getSize() != b.getSize())
			return null;

		Bitmap map = new Bitmap(a.getSize());
		int size = a.getSize();

		for (int i = 0; i < size; i++)
			if (a.get(i) != b.get(i))
				map.set(i);

		return map;
	}

	/**
	 * Returns the binary representation of bitmap.
	 * @return binary represetation of bitmap as String
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < size; i++) {
			if (get(i)) {
				result.append("1");
			}
			else {
				result.append("0");
			}
			if ((i + 1) % 8 == 0) {
				result.append(" ");
			}
		}

		return result.toString();
	}

	public void dump() {
		System.out.println(toString());
	}

	public byte[] getBitmap() {
		return bitmap;
	}

	public int getSize() {
		return size;
	}

}
