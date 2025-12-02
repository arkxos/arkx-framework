package io.arkx.framework.commons.collection;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;

import io.arkx.framework.commons.util.BufferedRandomAccessFile;
import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.NumberUtil;
import io.arkx.framework.commons.util.ZipUtil;

/**
 * @class org.ark.framework.collection.FileCachedMapx 文件缓存列表
 * @author Darkness
 * @date 2012-8-6 下午10:00:53
 * @version V1.0
 */
public class FileCachedMapx {

	private Mapx<String, Object> map;

	private boolean compressible;

	private int total;

	private int size;

	private int modCount;

	private short maxFileIndex;

	private int maxItemInMemory = 1000;

	private String cacheDirectory;

	private BufferedRandomAccessFile[] addressFiles = null;

	private BufferedRandomAccessFile[] keyFiles = null;

	private BufferedRandomAccessFile[] valueFiles = null;

	private static final int AddressCountInOneFile = 268435456;

	private static final int MaxFileSize = 2097152000;

	private static final int DefaultCountInMemory = 100;

	private int addressFileCount;

	public FileCachedMapx(String cacheDir) {
		this(cacheDir, 65536, 100);
	}

	public FileCachedMapx(String cacheDir, boolean compressiable) {
		this(cacheDir, 65536, compressiable, 100);
	}

	public FileCachedMapx(String cacheDir, int initEntrySize) {
		this(cacheDir, initEntrySize, true, 100);
	}

	public FileCachedMapx(String cacheDir, boolean compressiable, int maxItemInMemory) {
		this(cacheDir, 65536, compressiable, maxItemInMemory);
	}

	public FileCachedMapx(String cacheDir, int initEntrySize, int maxItemInMemory) {
		this(cacheDir, initEntrySize, true, maxItemInMemory);
	}

	public FileCachedMapx(String cacheDir, int initEntrySize, boolean compressiable, int maxItemInMemory) {
		this.cacheDirectory = cacheDir;
		this.total = Double.valueOf(Math.pow(2.0D, Math.ceil(Math.log(initEntrySize) / Math.log(2.0D)))).intValue();
		this.compressible = compressiable;
		this.maxItemInMemory = maxItemInMemory;
		this.map = new Mapx(maxItemInMemory);
		loadInfo();
	}

	private void initFiles() {
		try {
			this.addressFileCount = Double.valueOf(Math.ceil(this.total * 1.0D / 268435456.0D)).intValue();
			this.addressFiles = new BufferedRandomAccessFile[this.addressFileCount];
			int prefix = Double.valueOf(Math.log(this.total / 16) / Math.log(2.0D)).intValue();
			for (int i = 0; i < this.addressFileCount; i++) {
				this.addressFiles[i] = new BufferedRandomAccessFile(this.cacheDirectory + prefix + "key" + i + ".idx",
						"rw");
				if (i == this.addressFileCount - 1) {
					this.addressFiles[i].setLength((this.total - (this.addressFileCount - 1) * 268435456) * 9);
				}
			}
			this.keyFiles = new BufferedRandomAccessFile[this.maxFileIndex + 1];
			this.valueFiles = new BufferedRandomAccessFile[this.maxFileIndex + 1];
			for (int i = 0; i <= this.maxFileIndex; i++) {
				this.keyFiles[i] = new BufferedRandomAccessFile(this.cacheDirectory + "key" + i + ".dat", "rw");
				this.valueFiles[i] = new BufferedRandomAccessFile(this.cacheDirectory + "value" + i + ".dat", "rw");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeData(String k, Object v) {
		int index = hash(k);
		int c = index % 268435456;
		index /= 268435456;
		if (this.addressFiles == null)
			initFiles();
		try {
			BufferedRandomAccessFile f = this.addressFiles[index];
			f.seek(c * 9);
			Key key = getKey(f);
			if (key == null) {
				f.seek(c * 9);
				writeFile(f, k, v);
			}
			else {
				if (key.KeyString.equals(k)) {
					updateByKey(key, v);
					return;
				}
				while (true) {
					f = this.keyFiles[key.KeyFileIndex];
					int pos = key.keyAddress + key.KeyLength - 9;
					f.seek(pos);
					key = getKey(f);
					if (key == null) {
						f.seek(pos);
						writeFile(f, k, v);
					}
					else if (key.KeyString.equals(k)) {
						updateByKey(key, v);
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeFile(BufferedRandomAccessFile f, String k, Object v) throws IOException {
		f.writeByte(1);
		f.writeShort(this.maxFileIndex);

		byte[] bk = k.getBytes();
		byte[] bv = toBytes(v);

		f.writeShort(22 + bk.length);
		BufferedRandomAccessFile kf = this.keyFiles[this.maxFileIndex];
		BufferedRandomAccessFile vf = this.valueFiles[this.maxFileIndex];
		int kpos = (int) kf.length();

		long kNewSize = kpos + 22 + bk.length;
		if (kNewSize > 2097152000L) {
			this.maxFileIndex = (short) (this.maxFileIndex + 1);
			BufferedRandomAccessFile fk = new BufferedRandomAccessFile(
					this.cacheDirectory + "key" + this.maxFileIndex + ".dat", "rw");
			BufferedRandomAccessFile fv = new BufferedRandomAccessFile(
					this.cacheDirectory + "value" + this.maxFileIndex + ".dat", "rw");
			this.keyFiles = ((BufferedRandomAccessFile[]) ArrayUtils.add(this.keyFiles, fk));
			this.valueFiles = ((BufferedRandomAccessFile[]) ArrayUtils.add(this.valueFiles, fv));
			kf = this.keyFiles[this.maxFileIndex];
			kNewSize = 22 + bk.length;
			kpos = 0;
		}

		int vpos = (int) vf.length();
		int vNewSize = vpos + 9 + bk.length + bv.length;
		if (vNewSize > 2097152000) {
			this.maxFileIndex = (short) (this.maxFileIndex + 1);
			BufferedRandomAccessFile fk = new BufferedRandomAccessFile(
					this.cacheDirectory + "key" + this.maxFileIndex + ".dat", "rw");
			BufferedRandomAccessFile fv = new BufferedRandomAccessFile(
					this.cacheDirectory + "value" + this.maxFileIndex + ".dat", "rw");
			this.keyFiles = ((BufferedRandomAccessFile[]) ArrayUtils.add(this.keyFiles, fk));
			this.valueFiles = ((BufferedRandomAccessFile[]) ArrayUtils.add(this.valueFiles, fv));
			vf = this.valueFiles[this.maxFileIndex];
			vNewSize = 9 + bk.length + bv.length;
			vpos = 0;
		}
		f.writeInt(kpos);

		kf.setLength(kNewSize);
		kf.seek(kpos);
		kf.writeByte(1);
		kf.writeShort(this.maxFileIndex);

		kf.writeInt(9 + bk.length + bv.length);
		kf.writeInt(vpos);
		kf.writeShort(bk.length);
		kf.write(bk);

		vf.setLength(vNewSize);
		vf.seek(vpos);
		vf.writeByte(1);
		vf.writeInt(bk.length);
		vf.writeInt(bv.length);
		vf.write(bk);
		vf.write(bv);
	}

	private void updateByKey(Key key, Object v) throws IOException {
		byte[] bv = toBytes(v);
		BufferedRandomAccessFile f = this.valueFiles[key.DataFileIndex];
		int pos = (int) f.length();
		int newDataLength = key.KeyLength - 13 + bv.length;
		if (newDataLength > key.DataLength) {
			if (key.DataFileIndex < this.maxFileIndex) {
				key.DataFileIndex = this.maxFileIndex;
				f = this.valueFiles[this.maxFileIndex];
				pos = (int) f.length();
			}

			int newSize = pos + newDataLength;
			if (newSize > 2097152000) {
				f.seek(key.DataAddress);
				f.writeByte(0);
				this.maxFileIndex = (short) (this.maxFileIndex + 1);
				BufferedRandomAccessFile fk = new BufferedRandomAccessFile(
						this.cacheDirectory + "key" + this.maxFileIndex + ".dat", "rw");
				BufferedRandomAccessFile fv = new BufferedRandomAccessFile(
						this.cacheDirectory + "value" + this.maxFileIndex + ".dat", "rw");
				this.keyFiles = ((BufferedRandomAccessFile[]) ArrayUtils.add(this.keyFiles, fk));
				this.valueFiles = ((BufferedRandomAccessFile[]) ArrayUtils.add(this.valueFiles, fv));
				f = this.valueFiles[this.maxFileIndex];
				key.DataFileIndex = this.maxFileIndex;
				pos = 0;
				f.setLength(newDataLength);
			}
			else {
				pos = (int) f.length();
				f.setLength(newSize);
			}

			f = this.keyFiles[key.KeyFileIndex];
			f.seek(key.keyAddress + 1);
			f.writeShort(this.maxFileIndex);
			f.writeInt(newDataLength);
			f.writeInt(pos);
			this.modCount += 1;
		}
		else {
			f = this.keyFiles[key.KeyFileIndex];
			f.seek(key.keyAddress + 3);
			f.writeInt(newDataLength);
			pos = key.DataAddress;
		}

		f = this.valueFiles[key.DataFileIndex];
		f.seek(pos);
		f.writeByte(1);
		byte[] bk = key.KeyString.getBytes();
		f.writeInt(bk.length);
		f.writeInt(bv.length);
		f.write(bk);
		f.write(bv);
	}

	private Key getKey(BufferedRandomAccessFile f) throws IOException {
		if ((f.length() == 0L) || (f.readByte() == 0)) {
			return null;
		}
		Key key = new Key();
		key.KeyFileIndex = f.readShort();
		key.KeyLength = f.readShort();
		key.keyAddress = f.readInt();

		f = this.keyFiles[key.KeyFileIndex];
		f.seek(key.keyAddress);
		if (f.readByte() == 0) {
			return null;
		}

		key.DataFileIndex = f.readShort();
		key.DataLength = f.readInt();
		key.DataAddress = f.readInt();
		f.readShort();
		byte[] bs = (byte[]) null;
		try {
			bs = new byte[key.KeyLength - 22];
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		f.read(bs);
		key.KeyString = new String(bs);
		return key;
	}

	private Key readKey(int index, String k) {
		int c = index % 268435456;
		index /= 268435456;
		if (this.addressFiles == null)
			initFiles();
		try {
			BufferedRandomAccessFile f = this.addressFiles[index];
			f.seek(c * 9);
			Key key = getKey(f);
			if ((key == null) || (key.KeyString.equals(k))) {
				return key;
			}
			do {
				f = this.keyFiles[key.KeyFileIndex];
				int pos = key.keyAddress + key.KeyLength - 9;
				f.seek(pos);
				key = getKey(f);
			}
			while ((key != null) && (!key.KeyString.equals(k)));
			return key;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Object readData(String k) throws IOException {
		int i = hash(k);
		Key key = readKey(i, k);
		if (key == null) {
			return null;
		}
		BufferedRandomAccessFile f = this.valueFiles[key.DataFileIndex];
		f.seek(key.DataAddress + 9 + key.KeyLength - 22);
		byte[] bv = new byte[key.DataLength - 9 - key.KeyLength + 22];
		f.read(bv);
		return toObject(bv);
	}

	private Object toObject(byte[] bs) {
		if ((bs == null) || (bs.length == 0)) {
			return null;
		}
		if (this.compressible) {
			try {
				bs = ZipUtil.unzip(bs);
			}
			catch (Exception e) {
				return null;
			}
		}
		if (bs.length == 0) {
			return null;
		}
		byte type = bs[0];
		bs = ArrayUtils.subarray(bs, 1, bs.length);
		if (type == 1) {
			return Integer.valueOf(NumberUtil.toInt(bs, 0));
		}
		if (type == 2) {
			return Long.valueOf(NumberUtil.toLong(bs));
		}
		if (type == 3) {
			return new String(bs);
		}
		if (type == 4) {
			return bs;
		}
		if (type == 5) {
			int[] arr = new int[bs.length / 4];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = NumberUtil.toInt(bs, i * 4);
			}
			return arr;
		}
		if (type == 6) {
			long[] arr = new long[bs.length / 8];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = NumberUtil.toInt(bs, i * 8);
			}
			return arr;
		}
		if (type == 7) {
			ByteBuffer bb = ByteBuffer.allocate(bs.length);
			bb.put(bs);
			bb.flip();
			ArrayList arr = new ArrayList();
			int index = 0;
			do {
				int length = bb.getInt();
				byte[] t = new byte[length];
				bb.get(t);
				arr.add(new String(t));
				index += 4 + length;
			}
			while (index != bs.length);

			String[] r = new String[arr.size()];
			for (int i = 0; i < r.length; i++) {
				r[i] = ((String) arr.get(i));
			}
			return r;
		}
		if (type == 8) {
			return FileUtil.unserialize(bs);
		}
		return null;
	}

	private byte[] toBytes(Object v) {
		byte type = 0;
		byte[] bs = (byte[]) null;
		if ((v instanceof Integer)) {
			type = 1;
			bs = NumberUtil.toBytes(((Integer) v).intValue());
		}
		else if ((v instanceof Long)) {
			type = 2;
			bs = NumberUtil.toBytes(((Long) v).longValue());
		}
		else if ((v instanceof String)) {
			type = 3;
			bs = ((String) v).getBytes();
		}
		else if ((v instanceof byte[])) {
			type = 4;
			bs = (byte[]) v;
		}
		else if ((v instanceof int[])) {
			type = 5;
			int[] arr = (int[]) v;
			bs = new byte[4 * arr.length];
			for (int i = 0; i < arr.length; i++)
				NumberUtil.toBytes(arr[i], bs, i * 4);
		}
		else if ((v instanceof long[])) {
			type = 6;
			long[] arr = (long[]) v;
			bs = new byte[8 * arr.length];
			for (int i = 0; i < arr.length; i++)
				NumberUtil.toBytes(arr[i], bs, i * 8);
		}
		else if ((v instanceof String[])) {
			type = 7;
			String[] arr = (String[]) v;
			bs = new byte[0];
			for (int i = 0; i < arr.length; i++) {
				byte[] b = (byte[]) null;
				b = arr[i].getBytes();
				bs = ArrayUtils.addAll(bs, NumberUtil.toBytes(b.length));
				bs = ArrayUtils.addAll(bs, b);
			}
		}
		else if ((v instanceof Serializable)) {
			type = 8;
			bs = FileUtil.serialize((Serializable) v);
		}
		if (this.compressible) {
			return ZipUtil.zip(ArrayUtils.add(bs, 0, type));
		}
		return ArrayUtils.add(bs, 0, type);
	}

	private synchronized void loadInfo() {
		File f = new File(this.cacheDirectory);
		if (!f.exists()) {
			f.mkdirs();
		}

		f = new File(this.cacheDirectory + "meta.dat");
		if (f.exists()) {
			byte[] bs = FileUtil.readByte(f);
			this.size = NumberUtil.toInt(bs, 0);
			this.total = NumberUtil.toInt(bs, 4);
			this.modCount = NumberUtil.toInt(bs, 8);
			this.maxFileIndex = NumberUtil.toShort(bs, 12);
			this.compressible = (NumberUtil.toShort(bs, 14) == 1);
		}
		else {
			this.size = 0;

			this.maxFileIndex = 0;
			this.modCount = 0;
		}
	}

	public synchronized void save() {
		if (this.cacheDirectory != null) {
			File f = new File(this.cacheDirectory + "meta.dat");
			byte[] bs = new byte[16];
			NumberUtil.toBytes(this.size, bs, 0);
			NumberUtil.toBytes(this.total, bs, 4);
			NumberUtil.toBytes(this.modCount, bs, 8);
			NumberUtil.toBytes(this.maxFileIndex, bs, 12);
			NumberUtil.toBytes((short) (this.compressible ? 1 : 0), bs, 14);
			FileUtil.writeByte(f, bs);
		}
	}

	public synchronized void close() {
		save();
		if (this.keyFiles != null) {
			for (int i = 0; i < this.keyFiles.length; i++) {
				try {
					this.keyFiles[i].close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (this.addressFiles != null) {
			for (int i = 0; i < this.addressFiles.length; i++) {
				try {
					this.addressFiles[i].close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (this.valueFiles != null)
			for (int i = 0; i < this.valueFiles.length; i++)
				try {
					this.valueFiles[i].close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
	}

	public synchronized boolean containsKey(String k) {
		if (this.map.containsKey(k)) {
			return true;
		}
		int i = hash(k);

		return readKey(i, k) != null;
	}

	public synchronized Entry firstEntry() {
		return Entry.first(this);
	}

	public synchronized void put(String k, Object value) {
		Object o = null;
		if (this.maxItemInMemory != 0)
			o = this.map.put(k, value);
		try {
			if (o == null) {
				int i = hash(k);
				Key key = readKey(i, k);
				if (key == null) {
					writeData(k, value);
					this.size += 1;
					if (this.size > this.total * 0.75D)
						resize();
				}
				else {
					writeData(k, value);
				}
			}
			else {
				writeData(k, value);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		save();
	}

	public int getInt(String key) {
		Object o = get(key);
		if (o == null) {
			return -2147483648;
		}
		if ((o instanceof Integer)) {
			return ((Integer) o).intValue();
		}
		throw new RuntimeException("Data type of key '" + key + "' is not int!");
	}

	public long getLong(String key) {
		Object o = get(key);
		if (o == null) {
			return -9223372036854775808L;
		}
		if ((o instanceof Long)) {
			return ((Long) o).longValue();
		}
		throw new RuntimeException("Data type of key '" + key + "' is not long!");
	}

	public String getString(String key) {
		Object o = get(key);
		if (o == null) {
			return null;
		}
		if ((o instanceof String)) {
			return (String) o;
		}
		throw new RuntimeException("Data type of key '" + key + "' is not String!");
	}

	public byte[] getByteArray(String key) {
		Object o = get(key);
		if (o == null) {
			return null;
		}
		if ((o instanceof byte[])) {
			return (byte[]) o;
		}
		throw new RuntimeException("Data type of key '" + key + "' is not byte[]!");
	}

	public int[] getIntArray(String key) {
		Object o = get(key);
		if (o == null) {
			return null;
		}
		if ((o instanceof int[])) {
			return (int[]) o;
		}
		throw new RuntimeException("Data type of key '" + key + "' is not int[]!");
	}

	public long[] getLongArray(String key) {
		Object o = get(key);
		if (o == null) {
			return null;
		}
		if ((o instanceof long[])) {
			return (long[]) o;
		}
		throw new RuntimeException("Data type of key '" + key + "' is not long[]!");
	}

	public String[] getStringArray(String key) {
		Object o = get(key);
		if (o == null) {
			return null;
		}
		if ((o instanceof String[])) {
			return (String[]) o;
		}
		throw new RuntimeException("Data type of key '" + key + "' is not String[]!");
	}

	private synchronized void resize() throws IOException {
		if (this.size < this.total * 0.75D) {
			return;
		}
		int total2 = this.total * 2;
		int fileCount = Double.valueOf(Math.ceil(total2 * 1.0D / 268435456.0D)).intValue();
		BufferedRandomAccessFile[] files = new BufferedRandomAccessFile[fileCount];
		int prefix = Double.valueOf(Math.log(total2 / 16) / Math.log(2.0D)).intValue();
		for (int i = 0; i < fileCount; i++) {
			files[i] = new BufferedRandomAccessFile(this.cacheDirectory + prefix + "key" + i + ".idx", "rw");
			if (i == this.addressFileCount - 1)
				files[i].setLength((total2 - (this.addressFileCount - 1) * 268435456) * 9);
			else {
				files[i].setLength(-1879048192L);
			}
		}

		byte[] empty = new byte[9];
		for (int i = 0; i < this.keyFiles.length; i++) {
			BufferedRandomAccessFile f = this.keyFiles[i];
			int pos = 0;
			while (f.length() > pos) {
				f.seek(pos);
				byte deleted = f.readByte();
				f.skipBytes(10);
				short len = f.readShort();

				if (deleted == 1) {
					byte[] bs = new byte[len];
					f.read(bs);
					f.seek(pos + 13 + len);
					f.write(empty);
					String k = new String(bs);
					int index = hash(k, total2);
					int c = index % 268435456;
					index /= 268435456;
					BufferedRandomAccessFile af = files[index];
					af.seek(c * 9);
					if (af.readByte() == 0) {
						af.seek(c * 9);
						af.writeByte(1);
						af.writeShort(i);
						af.writeShort(22 + bs.length);
						af.writeInt(pos);
					}
					else {
						af.seek(c * 9);
						Key key = getKey(af);
						if (key == null)
							throw new RuntimeException("Data error,not found key data in key position!");
						BufferedRandomAccessFile kf;
						int pos2;
						do {
							kf = this.keyFiles[key.KeyFileIndex];
							pos2 = key.keyAddress + key.KeyLength - 9;
							kf.seek(pos2);
							key = getKey(kf);
						}
						while (key != null);

						kf.seek(pos2);
						kf.writeByte(1);
						kf.writeShort(i);
						kf.writeShort(22 + bs.length);
						kf.writeInt(pos);
					}

				}

				pos = pos + (len + 22);
			}
		}

		BufferedRandomAccessFile[] tmp = this.addressFiles;
		this.addressFiles = files;
		this.total = total2;
		for (int i = 0; i < tmp.length; i++)
			tmp[i].delete();
	}

	public synchronized Serializable get(String k) {
		Object o = null;
		if (this.maxItemInMemory != 0) {
			o = this.map.get(k);
		}
		if (o != null)
			return (Serializable) o;
		try {
			return (Serializable) readData(k);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized boolean remove(String k) {
		this.map.remove(k);
		int index = hash(k);
		int c = index % 268435456;
		index /= 268435456;
		try {
			if (this.addressFiles == null) {
				initFiles();
			}
			BufferedRandomAccessFile f = this.addressFiles[index];
			f.seek(c * 9);
			Key key = getKey(f);
			if (key == null) {
				return false;
			}
			if (key.KeyString.equals(k)) {
				f = this.keyFiles[key.KeyFileIndex];
				int pos = key.keyAddress + key.KeyLength - 9;
				f.seek(pos);
				if (f.readByte() == 0) {
					f = this.addressFiles[index];
					f.seek(c * 9);
					f.writeByte(0);
				}
				else {
					f.seek(pos);
					byte[] bs = new byte[9];
					f.read(bs);
					f = this.addressFiles[index];
					f.seek(c * 9);
					f.write(bs);
				}
				f = this.keyFiles[key.KeyFileIndex];
				f.seek(key.keyAddress);
				f.writeByte(0);

				f = this.valueFiles[key.DataFileIndex];
				f.seek(key.DataAddress);
				f.writeByte(0);
				this.size -= 1;
				this.modCount += 1;
				save();
				return true;
			}
			int index2;
			int pos2;
			do {
				index2 = key.KeyFileIndex;
				f = this.keyFiles[index2];
				pos2 = key.keyAddress + key.KeyLength - 9;
				f.seek(pos2);
				key = getKey(f);
				if (key == null)
					return false;
			}
			while (!key.KeyString.equals(k));
			f = this.keyFiles[key.KeyFileIndex];
			f.seek(key.keyAddress);
			f.writeByte(0);
			int pos = key.keyAddress + key.KeyLength - 9;
			f.seek(pos);
			if (f.readByte() == 1) {
				f.seek(pos);
				byte[] bs = new byte[9];
				f.read(bs);
				f = this.keyFiles[index2];
				f.seek(pos2);
				f.write(bs);
			}

			f = this.valueFiles[key.DataFileIndex];
			f.seek(key.DataAddress);
			f.writeByte(0);
			this.size -= 1;
			this.modCount += 1;
			save();
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public synchronized void refactor() {
		if (this.total * 0.5D > this.modCount)
			return;
	}

	private int hash(Object x) {
		return hash(x, this.total);
	}

	public static int hash(Object x, int length) {
		int h = x.hashCode();
		h += (h << 9 ^ 0xFFFFFFFF);
		h ^= h >>> 14;
		h += (h << 4);
		h ^= h >>> 10;
		return h & length - 1;
	}

	public int size() {
		return this.size;
	}

	public String getCacheDirectory() {
		return this.cacheDirectory;
	}

	public void setCacheDirectory(String cacheDirectory) {
		if ((cacheDirectory.endsWith("/")) || (cacheDirectory.endsWith("\\"))) {
			cacheDirectory = cacheDirectory + "/";
		}
		this.cacheDirectory = cacheDirectory;
	}

	public int getMaxItemInMemory() {
		return this.maxItemInMemory;
	}

	public void setMaxItemInMemory(int maxItemInMemory) {
		this.maxItemInMemory = maxItemInMemory;
	}

	public boolean isCompressible() {
		return this.compressible;
	}

	public static class Entry {

		private FileCachedMapx fcm;

		private FileCachedMapx.Key key;

		protected Entry(FileCachedMapx fcm, FileCachedMapx.Key key) {
			this.fcm = fcm;
			this.key = key;
		}

		public String getKey() {
			return this.key.KeyString;
		}

		public Object getValue() {
			synchronized (this.fcm) {
				BufferedRandomAccessFile f = this.fcm.valueFiles[this.key.DataFileIndex];
				try {
					f.seek(this.key.DataAddress + 9 + this.key.KeyLength - 22);
					byte[] bv = new byte[this.key.DataLength - 9 - this.key.KeyLength + 22];
					f.read(bv);
					return this.fcm.toObject(bv);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		protected static Entry first(FileCachedMapx fcm) {
			synchronized (fcm) {
				try {
					if (fcm.addressFiles == null) {
						fcm.initFiles();
					}
					BufferedRandomAccessFile f = fcm.keyFiles[0];
					int pos = 0;
					FileCachedMapx.Key key = new FileCachedMapx.Key();
					while (f.length() > pos) {
						f.seek(pos);
						byte deleted = f.readByte();
						key.DataFileIndex = f.readShort();
						key.DataLength = f.readInt();
						key.DataAddress = f.readInt();
						short len = f.readShort();
						key.KeyLength = (short) (len + 22);
						if (deleted == 1) {
							byte[] bs = new byte[len];
							f.read(bs);
							key.KeyString = new String(bs);
							key.keyAddress = pos;
							return new Entry(fcm, key);
						}
						pos += len + 22;
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		public Entry next() {
			synchronized (this.fcm) {
				try {
					BufferedRandomAccessFile f = this.fcm.keyFiles[this.key.KeyFileIndex];
					int pos = this.key.keyAddress + this.key.KeyLength;
					FileCachedMapx.Key nextKey = new FileCachedMapx.Key();
					nextKey.KeyFileIndex = this.key.KeyFileIndex;
					if (pos == f.length()) {
						if (this.key.KeyFileIndex == this.fcm.maxFileIndex)
							return null;
						FileCachedMapx.Key tmp93_91 = nextKey;
						tmp93_91.KeyFileIndex = (short) (tmp93_91.KeyFileIndex + 1);
						nextKey.keyAddress = 0;
						f = this.fcm.keyFiles[nextKey.KeyFileIndex];
						pos = 0;
					}
					nextKey.keyAddress = pos;
					while (f.length() > pos) {
						f.seek(pos);
						byte deleted = f.readByte();
						nextKey.DataFileIndex = f.readShort();
						nextKey.DataLength = f.readInt();
						nextKey.DataAddress = f.readInt();
						short len = f.readShort();
						if (deleted == 1) {
							byte[] bs = new byte[len];
							f.read(bs);
							nextKey.KeyString = new String(bs);
							nextKey.KeyLength = (short) (len + 22);
							nextKey.keyAddress = pos;
							return new Entry(this.fcm, nextKey);
						}
						pos += len + 22;
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

	}

	public static class Key {

		short KeyFileIndex;

		short KeyLength;

		int keyAddress;

		String KeyString;

		short DataFileIndex;

		int DataLength;

		int DataAddress;

	}

}
