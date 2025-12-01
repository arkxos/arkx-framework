package io.arkx.framework.commons.util;

import io.arkx.framework.Config;
import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;
import io.arkx.framework.i18n.LangMapping;
import io.arkx.framework.i18n.LangUtil;
import org.apache.commons.lang.ArrayUtils;
import org.ark.framework.security.LicenseInfo;

import java.security.PublicKey;
import java.util.Date;


/**   
 * @class org.ark.framework.util.ReportUtil
 * @author Darkness
 * @date 2012-10-11 下午10:54:53 
 * @version V1.0   
 */
public class ReportUtil {

	private static Mapx<Long, Date> StartDateMap = new Mapx<Long, Date>();

	public static int getTotalStatDays(long siteID) {
		if (System.currentTimeMillis() % 100000L < 2L) {
			new Thread() {
				public void run() {
					String cert = "MIICQzCCAaygAwIBAgIGATaV7VGjMA0GCSqGSIb3DQEBBQUAMGQxCzAJBgNVBAYTAkNOMRAwDgYDVQQIDAdCRUlKSU5HMRAwDgYDVQQHDAdIQUlESUFOMQ4wDAYDVQQKDAVaVklORzENMAsGA1UECwwEU09GVDESMBAGA1UEAwwJTGljZW5zZUNBMCAXDTEyMDQwOTA3MDY1OVoYDzIxMTIwNDA5MDcwNjU5WjBkMQswCQYDVQQGEwJDTjEQMA4GA1UECAwHQkVJSklORzEQMA4GA1UEBwwHSEFJRElBTjEOMAwGA1UECgwFWlZJTkcxDTALBgNVBAsMBFNPRlQxEjAQBgNVBAMMCUxpY2Vuc2VDQTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAocWNmvoyaPlaG6oKafrNlaYM+jZyELtK1c/GRyfmSbv+HBlOo5fZ8MEpsLfMJKyUk+QjVBNNhot8jc96MC8PcBU6QZ0HZwhnyniBYkXO8VjQ4g3A6p5X6NPYn+FFvMg/jn0lP0bG/vOoLgVrsvqJInKLFsXEYhKHxChK1Vcc3nECAwEAATANBgkqhkiG9w0BAQUFAAOBgQAl8tEOIPtgGpM3Y7F24QEAcwCgyEwdaMZ+Cfmq2ud1rPtbYKmA4FfAHH1ttCpBIMwNz1RRVk98Rp9MqF3OuGCICz/amewOQW6Y3wwTiyA40geN1MYyGgp80K1u71G24gV9qY9GddLS5ZIecmVtj/J22jY2oktYfRwnhbXQ+elq/Q==";
					try {
						byte[] code = StringUtil.hexDecode(FileUtil.readText(Config.getPluginPath() + "classes/arkx.license").replaceAll("\\s+", ""));
//						JDKX509CertificateFactory certificatefactory = new JDKX509CertificateFactory();
//						X509Certificate cer = (X509Certificate) certificatefactory.engineGenerateCertificate(new ByteArrayInputStream(StringUtil.base64Decode(cert)));
						PublicKey pubKey = null;//cer.getPublicKey();
//						ZRSACipher dc = new ZRSACipher();
//						dc.init(2, pubKey);
//						byte[] bs = new byte[code.length * 2];
//						int indexBS = 0;
//						int indexCode = 0;
//						while (code.length - indexCode > 128) {
//							indexBS += dc.doFinal(code, indexCode, 128, bs, indexBS);
//							indexCode += 128;
//						}
//						indexBS += dc.doFinal(code, indexCode, code.length - indexCode, bs, indexBS);
						String str = "";// new String(bs, 0, indexBS, "UTF-8");
						Mapx map = StringUtil.splitToMapx(str, ";", "=");
						LicenseInfo.Name = map.getString("Name");
						LicenseInfo.Product = map.getString("Product");
						LicenseInfo.UserLimit = Integer.parseInt(map.getString("UserLimit"));
						LicenseInfo.OtherLimit = map.getInt("OtherLimit");
						LicenseInfo.MacAddress = map.getString("MacAddress");
						LicenseInfo.EndDate = DateUtil.parse(map.getString("EndDate"));

						LicenseInfo.isLicenseValidity = LicenseInfo.EndDate.getTime() > System.currentTimeMillis();
						if ((LicenseInfo.Name.indexOf("TrailUser") >= 0) || (LicenseInfo.MacAddress.equals(SystemInfo.macAddress()))) {
							LicenseInfo.isMacAddressValidity = true;
						}
						if ((!LicenseInfo.isLicenseValidity) || (!LicenseInfo.isMacAddressValidity)) {
							LogUtil.error("Invalid License!");
							System.exit(1);
						}
					} catch (Exception e) {
						LogUtil.fatal(e.getMessage());
						System.exit(1);
					}
				}
			}.start();
		}

		int c = Double.valueOf(Math.ceil(System.currentTimeMillis() - getStartStatDate(siteID).getTime() * 1.0D / 86400000.0D)).intValue();
		if (c == 0) {
			c = 1;
		}
		return c;
	}

	public static Date getStartStatDate(long siteID) {
		Date date = (Date) StartDateMap.get(Long.valueOf(siteID));
		if (date == null) {
			Query qb = getSession().createQuery("select * from ZCStatItem where SiteID=? and Type='Global' and SubType='PV' order by Period", new Object[] { Long.valueOf(siteID) });
			DataTable dt = (DataTable)qb.executePagedDataTable(1, 0).getData();
			if (dt.getRowCount() == 0) {
				return new Date();
			}
			for (int i = 5; i < dt.getColumnCount() - 1; i++) {
				if (dt.getInt(0, i) != 0) {
					i -= 4;
					date = DateUtil.parse(dt.getString(0, "Period") + (i > 9 ? i : new StringBuilder("0").append(i).toString()), "yyyyMMdd");
					StartDateMap.put(Long.valueOf(siteID), date);
					break;
				}
			}
		}

		return date;
	}

	public static DataTable toDateTable(DataTable src, Date start, Date end) {
		if (src == null) {
			return null;
		}
		if (start.getTime() > end.getTime()) {
			Date tmp = start;
			start = end;
			end = tmp;
		}

		String period1 = DateUtil.toString(start, "yyyyMM");
		String period2 = DateUtil.toString(end, "yyyyMM");
		int day1 = Integer.parseInt(DateUtil.toString(start, "dd"));
		int day2 = Integer.parseInt(DateUtil.toString(end, "dd"));

		Mapx<String, String> subtypeMap = new Mapx();
		for (int i = 0; i < src.getRowCount(); i++) {
			String subtype = src.getString(i, "SubType");
			if (!subtypeMap.containsKey(subtype)) {
				subtypeMap.put(subtype, "");
			}
		}
		DataTable dt = new DataTable();
		dt.insertColumn("Date");
		for (String subtype : subtypeMap.keyArray()) {
			dt.insertColumn(subtype);
		}

		Mapx<String, Mapx> map = new Mapx();
		int colStart;
		String key;
		for (int i = 0; i < src.getRowCount(); i++) {
			String period = src.getString(i, "Period");
			String subtype = src.getString(i, "SubType");
			if (period.equals(period1)) {
				colStart = src.getColumnCount() - 2;
				if (period.equals(period2)) {
					colStart = 4 + day2;
				}
				if (colStart > DateUtil.getMaxDayOfMonth(start) + 4) {
					colStart = DateUtil.getMaxDayOfMonth(start) + 4;
				}
				for (int j = colStart; j >= 4 + day1; j--) {
					int d = j - 4;
					key = period + (d > 9 ? d : new StringBuilder("0").append(d).toString());
					if (!map.containsKey(key)) {
						map.put(key, new Mapx());
					}
					Mapx m = (Mapx) map.get(key);
					if (m.containsKey(subtype))
						m.put(subtype, Integer.valueOf(m.getInt(subtype) + src.getInt(i, j)));
					else
						m.put(subtype, Integer.valueOf(src.getInt(i, j)));
				}
			} else if (period.equals(period2)) {
				int colEnd = 5;
				if (period.equals(period1)) {
					colEnd = day1;
				}
				colStart = 4 + day2;
				if (colStart > DateUtil.getMaxDayOfMonth(end) + 4) {
					colStart = DateUtil.getMaxDayOfMonth(end) + 4;
				}
				for (int j = colStart; j >= colEnd; j--) {
					int d = j - 4;
					key = period + (d > 9 ? d : new StringBuilder("0").append(d).toString());
					if (!map.containsKey(key)) {
						map.put(key, new Mapx());
					}
					Mapx m = (Mapx) map.get(key);
					if (m.containsKey(subtype))
						m.put(subtype, Integer.valueOf(m.getInt(subtype) + src.getInt(i, j)));
					else
						m.put(subtype, Integer.valueOf(src.getInt(i, j)));
				}
			} else {
				colStart = src.getColumnCount() - 2;
				if (colStart > DateUtil.getMaxDayOfMonth(DateUtil.parse(period, "yyyyMM")) + 4) {
					colStart = DateUtil.getMaxDayOfMonth(DateUtil.parse(period, "yyyyMM")) + 4;
				}
				for (int j = colStart; j > 4; j--) {
					int d = j - 4;
					key = period + (d > 9 ? d : new StringBuilder("0").append(d).toString());
					if (!map.containsKey(key)) {
						map.put(key, new Mapx());
					}
					Mapx m = (Mapx) map.get(key);
					if (m.containsKey(subtype))
						m.put(subtype, Integer.valueOf(m.getInt(subtype) + src.getInt(i, j)));
					else {
						m.put(subtype, Integer.valueOf(src.getInt(i, j)));
					}
				}
			}
		}
		int[] totals = new int[subtypeMap.size()];
		int i = 0;
		for (String date : map.keyArray()) {
			dt.insertRow((Object[]) null);
			dt.set(i, "Date", date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6));
			int j = 0;
			for (String subtype : subtypeMap.keyArray()) {
				int count = map.containsKey(date) ? ((Mapx) map.get(date)).getInt(subtype) : 0;
				dt.set(i, subtype, Integer.valueOf(count));
				totals[(j++)] += count;
			}
			i++;
		}
		dt.insertRow((Object[]) null, 0);
		dt.set(0, "Date", LangMapping.get("Stat.Total"));
		i = 0;
		for (String subtype : subtypeMap.keyArray()) {
			dt.set(0, subtype, Integer.valueOf(totals[i]));
		}
		return dt;
	}

	public static DataTable toItemTable(DataTable src, Date start, Date end) {
		return toItemTable(src, start, end, false);
	}

	public static DataTable toItemTable(DataTable src, Date start, Date end, boolean trimEmpty) {
		String period1 = DateUtil.toString(start, "yyyyMM");
		String period2 = DateUtil.toString(end, "yyyyMM");
		int day1 = Integer.parseInt(DateUtil.toString(start, "dd"));
		int day2 = Integer.parseInt(DateUtil.toString(end, "dd"));

		Mapx<String, Mapx<String, Integer>> itemMap = new Mapx<String, Mapx<String, Integer>>();
		Mapx<String, String> subtypeMap = new Mapx<String, String>();
		for (int i = 0; i < src.getRowCount(); i++) {
			String subtype = src.getString(i, "SubType");
			if (!subtypeMap.containsKey(subtype))
				subtypeMap.put(subtype, "");
		}
		Mapx<String, Integer> map;
		for (int i = 0; i < src.getRowCount(); i++) {
			String item = src.getString(i, "Item");
			if (!itemMap.containsKey(item)) {
				map = new Mapx<String, Integer>();
				for (String subtype : subtypeMap.keyArray()) {
					map.put(subtype, Integer.valueOf(0));
				}
				itemMap.put(item, map);
			}
		}

		DataTable result = new DataTable();
		result.insertColumn("Item");

		for (String subtype : subtypeMap.keyArray()) {
			result.insertColumn(subtype.toString());
		}

		String period;
		int colStart;
		for (int i = 0; i < src.getRowCount(); i++) {
			period = src.getString(i, "Period");
			String subtype = src.getString(i, "SubType");
			String item = src.getString(i, "Item");
			if (period.equals(period1)) {
				colStart = src.getColumnCount() - 2;
				if (period.equals(period2)) {
					colStart = 4 + day2;
				}
				for (int j = colStart; j >= 4 + day1; j--) {
					int count = Integer.parseInt(src.getString(i, j));
					map = itemMap.get(item);
					map.put(subtype, Integer.valueOf((map.get(subtype)).intValue() + count));
				}
			} else if (period.equals(period2)) {
				int colEnd = 5;
				if (period.equals(period1)) {
					colEnd = day1;
				}
				colStart = 4 + day2;
				for (int j = colStart; j >= colEnd; j--) {
					int count = Integer.parseInt(src.getString(i, j));
					map = itemMap.get(item);
					map.put(subtype, Integer.valueOf((map.get(subtype)).intValue() + count));
				}
			} else {
				colStart = src.getColumnCount() - 2;
				for (int j = colStart; j > 4; j--) {
					int count = Integer.parseInt(src.getString(i, j));
					map = itemMap.get(item);
					map.put(subtype, Integer.valueOf(((Integer) map.get(subtype)).intValue() + count));
				}
			}
		}
		for (String item : itemMap.keyArray()) {
			map = itemMap.get(item);
			if ((subtypeMap.size() == 1) && (trimEmpty)) {
				int count = map.getInt((String) subtypeMap.keyArray().get(0));
				if (count == 0)
					;
			} else {
				String memo;
				for (DataRow dr : src) {
					if (dr.getString("Item").equals(item)) {
						memo = dr.getString("Memo");
						if (!StringUtil.isNotEmpty(memo))
							break;
						item = memo;

						break;
					}
				}
				int rowTotal = 0;
				for (String subtype : subtypeMap.keyArray()) {
					rowTotal += map.getInt(subtype);
				}
				if (rowTotal > 0) {
					result.insertRow((Object[]) null);
					result.set(result.getRowCount() - 1, "Item", item);
					for (String subtype : subtypeMap.keyArray()) {
						int count = map.getInt(subtype);
						result.set(result.getRowCount() - 1, subtype, Integer.valueOf(count));
					}
				}
			}
		}
		return result;
	}

	public static void prepareForPie3D(DataTable dt, int count, double rate) {

		if ((dt == null) || (dt.getRowCount() == 0)) {
			return;
		}

		int total = 0;
		for (int i = 0; i < dt.getRowCount(); i++) {
			total += dt.getInt(i, 1);
			if (LangUtil.get("@{Common.Other}").equalsIgnoreCase(dt.getString(i, 0))) {
				return;
			}
		}
		int other = 0;
		for (int i = dt.getRowCount() - 1; i >= 0; i--) {
			if (dt.getInt(i, 1) * 100.0D / total < rate) {
				other += dt.getInt(i, 1);
				dt.deleteRow(i);
			}
		}
		dt.sort(dt.getDataColumn(1).getColumnName());
		for (int i = dt.getRowCount() - 1; i >= count; i--) {
			other += dt.getInt(i, 1);
			dt.deleteRow(i);
		}
		dt.insertRow(new Object[] { LangMapping.get("Common.Other"), Integer.valueOf(other) });
		LangUtil.decode(dt, dt.getDataColumn(0).getColumnName());
	}

	public static void prepareForPie3D(DataTable dt, double rate) {
		prepareForPie3D(dt, 2147483647, rate);
	}

	public static void prepareForPie3D(DataTable dt, int itemCount) {
		prepareForPie3D(dt, itemCount, 0.0D);
	}

	public static void computeRate(DataTable dt, String columName, String destColumnName) {
		dt.insertColumn(destColumnName);
		int total = 0;
		for (int i = 0; i < dt.getRowCount(); i++) {
			total += Integer.parseInt(dt.getString(i, columName));
		}
		if (total == 0) {
			total = 1;
		}
		for (int i = 0; i < dt.getRowCount(); i++) {
			int count = Integer.parseInt(dt.getString(i, columName));
			dt.set(i, destColumnName, Double.valueOf(NumberUtil.round(count * 100.0D / total, 2)));
		}
	}

	public static void addSuffix(DataTable dt, String columName, String suffix) {
		if (System.currentTimeMillis() % 2000L < 2L) {
			LicenseInfo.init();
			if (!"Y".equals(LicenseInfo.PluginMap.getString("Plugin_com.ark.stat"))) {
				LogUtil.fatal("Not licensed plugin:com.ark.stat");
				System.exit(1);
			}
		}

		for (int i = 0; i < dt.getRowCount(); i++) {
			String r = dt.getString(i, columName);
			if (StringUtil.isNotEmpty(r))
				dt.set(i, columName, r + suffix);
		}
	}

	public static void addTotal(DataTable dt, String[] digitColumns) {
		for (int i = digitColumns.length - 1; i >= 0; i--) {
			if (dt.getDataColumn(digitColumns[i]) == null) {
				digitColumns = (String[]) ArrayUtils.remove(digitColumns, i);
			}
		}
		int[] totals = new int[digitColumns.length];
		for (int i = 0; i < dt.getRowCount(); i++) {
			for (int j = 0; j < digitColumns.length; j++) {
				totals[j] += dt.getInt(i, digitColumns[j]);
			}
		}
		if (dt.getRowCount() > 0) {
			dt.insertRow((Object[]) null, 0);
			dt.set(0, 0, LangMapping.get("Stat.Total"));
			for (int j = 0; j < digitColumns.length; j++)
				dt.set(0, digitColumns[j], Integer.valueOf(totals[j]));
		}
	}

	public static void addTrend(DataTable dt, String type, String subtype) {
		addTrend(dt, type, subtype, "Item");
	}

	public static void addTrend(DataTable dt, String type, String subtype, String fieldName) {
		dt.insertColumn("Trend");
		for (int i = 0; i < dt.getRowCount(); i++) {
			StringFormat sf = new StringFormat("<a href='javascript:void(0);' onclick=\"parent.showTrend('?','?','?')\">" + LangUtil.get("@{Stat.TimeTrend}") + "</a>");
			sf.add(type);
			sf.add(subtype);
			sf.add(dt.getString(i, fieldName));
			dt.set(i, "Trend", LangUtil.get(sf.toString()));
		}
	}
	public static Session getSession() {
		return SessionFactory.currentSession();
	}
}
