package org.ark.framework.orm;

import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.*;

import java.io.IOException;
import java.util.ArrayList;


/**
 * @class org.ark.framework.orm.ZDTParser
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:20:28 
 * @version V1.0
 */
public class ZDTParser {
	
	private String file;
	private String Version;
	private ArrayList<ZDTTableInfo> Tables = new ArrayList<ZDTTableInfo>();

	public ZDTParser(String file) {
		this.file = file;
	}

	public void parse() {
		BufferedRandomAccessFile braf = null;
		try {
			braf = new BufferedRandomAccessFile(this.file, "r");

			byte[] bs = new byte[4];
			braf.read(bs);
			int len = NumberUtil.toInt(bs);
			bs = new byte[len];
			braf.read(bs);
			this.Version = new String(bs);
			if (!ObjectUtil.in(new Object[] { this.Version, "1" })) {
				throw new RuntimeException("Unknown .zdm version:" + this.Version);
			}

			Mapx<String, ZDTTableInfo> map = new Mapx<String, ZDTTableInfo>();
			int currentPos = len + 4;
			String columns;
			while (braf.getFilePointer() != braf.length()) {
				bs = new byte[4];
				braf.read(bs);
				currentPos += 4;

				len = NumberUtil.toInt(bs);
				bs = new byte[len];
				braf.read(bs);
				currentPos += len;
				String name = new String(bs);

				bs = new byte[4];
				braf.read(bs);
				currentPos += 4;

				len = NumberUtil.toInt(bs);
				bs = new byte[len];
				braf.read(bs);
				currentPos += len;
				columns = new String(bs);
				SchemaColumn[] scs = parseColumns(columns);

				bs = new byte[4];
				braf.read(bs);
				currentPos += 4;

				len = NumberUtil.toInt(bs);
				bs = new byte[len];
				braf.read(bs);
				currentPos += len;
				String indexInfo = new String(bs);
				if ("_ARK_NULL".equals(indexInfo)) {
					indexInfo = null;
				}

				bs = new byte[4];
				braf.read(bs);
				currentPos += 4;

				len = NumberUtil.toInt(bs);
				bs = new byte[len];
				braf.read(bs);
				bs = ZipUtil.unzip(bs);
				currentPos += len;

				if (!map.containsKey(name)) {
					ZDTTableInfo ti = new ZDTTableInfo();
					ti.Name = name;
					ti.Columns = scs;
					ti.IndexInfo = indexInfo;
					ti.StartPosition = (currentPos - len - 4);
					ti.Positions.add(Integer.valueOf(ti.StartPosition));
					map.put(name, ti);
					this.Tables.add(ti);
				} else {
					ZDTTableInfo ti = (ZDTTableInfo) map.get(name);
					ti.Positions.add(Integer.valueOf(currentPos - len - 4));
				}

			}

			for (ZDTTableInfo ti : this.Tables) {
				DataTable dt = readOneTable(ti, braf, ((Integer) ti.Positions.get(ti.Positions.size() - 1)).intValue());
				ti.RowCount = ((ti.Positions.size() - 1) * 500 + dt.getRowCount());
			}
		} catch (Exception e1) {
			e1.printStackTrace();

			if (braf != null)
				try {
					braf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		} finally {
			if (braf != null)
				try {
					braf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public DataTable getDataTable(ZDTTableInfo ti, int start, int end) {
		BufferedRandomAccessFile braf = null;
		try {
			end--;
			int startIndex = start / 500;
			int endIndex = end / 500;
			if (endIndex >= ti.Positions.size()) {
				endIndex = ti.Positions.size() - 1;
			}
			if (start / 500 > ti.Positions.size()) {
				throw new RuntimeException("Invalid start position：" + start);
			}
			braf = new BufferedRandomAccessFile(this.file, "r");
			DataTable dt = null;
			for (int i = startIndex; i <= endIndex; i++) {
				DataTable dt2 = readOneTable(ti, braf, ((Integer) ti.Positions.get(i)).intValue());
				if (dt == null) {
					dt = new DataTable(dt2.getDataColumns(), null);
				}
				int rowStart = 0;
				if (i == startIndex) {
					rowStart = start % 500;
				}
				for (int j = rowStart; (dt.getRowCount() < end + 1 - start) && (j < dt2.getRowCount()); j++) {
					dt.insertRow(dt2.getDataRow(j));
				}
			}
			DataTable localDataTable1 = dt;
			return localDataTable1;
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (braf != null) {
				try {
					braf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private static DataTable readOneTable(ZDTTableInfo ti, BufferedRandomAccessFile braf, int pos) throws Exception {
		braf.seek(pos);
		byte[] bs = new byte[4];
		braf.read(bs);
		int len = NumberUtil.toInt(bs);
		bs = new byte[len];
		braf.read(bs);
		bs = ZipUtil.unzip(bs);
		String data = new String(bs, "UTF-8");
		if (ObjectUtil.empty(data)) {
			return new DataTable();
		}
		return parseDataTable(ti, data);
	}

	protected static SchemaColumn[] parseColumns(String columns) {
		String[] arr = StringUtil.splitEx(columns, "\n");
		ArrayList<SchemaColumn> list = new ArrayList<SchemaColumn>();
		for (String str : arr) {
			if (StringUtil.isEmpty(str)) {
				continue;
			}
			String[] arr2 = StringUtil.splitEx(str, "\t");
			String name = arr2[0];
			int type = Integer.parseInt(arr2[1]);
			int order = Integer.parseInt(arr2[2]);
			int length = Integer.parseInt(arr2[3]);
			int precision = Integer.parseInt(arr2[4]);
			boolean mandatory = "true".equals(arr2[5]);
			boolean pk = "true".equals(arr2[6]);
			SchemaColumn sc = new SchemaColumn(name, type, order, length, 
					precision, mandatory, pk, "");
			list.add(sc);
		}
		SchemaColumn[] scs = new SchemaColumn[list.size()];
		return list.toArray(scs);
	}

	protected static DataTable parseDataTable(ZDTTableInfo ti, String data) {
		SchemaColumn[] scs = ti.Columns;
		DataTable dt = new DataTable();
		for (SchemaColumn sc : scs) {
			dt.insertColumn(new DataColumn(sc.getColumnName(), sc.getColumnType()));
		}
		String[] arr = StringUtil.splitEx(data, "\n");
		label545: for (String str : arr) {
			String[] arr2 = StringUtil.splitEx(str, "\t");
			Object[] vs = new Object[scs.length];
			int i = 0;
			for (String v : arr2) {
				if (ObjectUtil.empty(v))
					vs[i] = null;
				else if (ObjectUtil.in(new Object[] { Integer.valueOf(scs[i].getColumnType()), Integer.valueOf(1), Integer.valueOf(10) })) {
					if (v.equals("null"))
						vs[i] = null;
					else
						vs[i] = StringUtil.javaDecode(v.substring(1, v.length() - 1));
				} else if (ObjectUtil.in(new Object[] { Integer.valueOf(scs[i].getColumnType()), Integer.valueOf(8), Integer.valueOf(9) }))
					vs[i] = Integer.valueOf(Integer.parseInt(v));
				else if (scs[i].getColumnType() == 7) {
					if ("\"\"".equals(v)) {
						vs[i] = Long.valueOf(0L);
					} else {
						try {
							vs[i] = Long.valueOf(Long.parseLong(v));
						} catch (Exception e) {
							if (ti.Name.equals("ZCTemplate"))
								break label545;
						}
						System.out.println("出错:" + ti.Name + "\t" + scs[i].getColumnName() + "\t" + v);
					}

				} else if (scs[i].getColumnType() == 5)
					vs[i] = Float.valueOf(Float.parseFloat(v));
				else if (ObjectUtil.in(new Object[] { Integer.valueOf(scs[i].getColumnType()), Integer.valueOf(6), Integer.valueOf(4) }))
					vs[i] = Double.valueOf(Double.parseDouble(v));
				else if (scs[i].getColumnType() == 12)
					vs[i] = DateUtil.parseDateTime(v);
				else if (scs[i].getColumnType() == 2) {
					vs[i] = StringUtil.base64Decode(StringUtil.javaDecode(v));
				}
				i++;
			}
			dt.insertRow(vs);
		}
		return dt;
	}

	public String getFile() {
		return this.file;
	}

	public String getVersion() {
		return this.Version;
	}

	public ArrayList<ZDTTableInfo> getTables() {
		return this.Tables;
	}

	public static class ZDTTableInfo {
		public String Name;
		public SchemaColumn[] Columns;
		public String IndexInfo;
		public int RowCount;
		public int StartPosition;
		public ArrayList<Integer> Positions = new ArrayList<Integer>();
	}
}