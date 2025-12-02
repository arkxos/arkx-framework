package io.arkx.framework.commons.util;

import org.ark.framework.security.LicenseInfo;

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;

/**
 * @class org.ark.framework.util.ChartUtil
 * @author Darkness
 * @date 2012-10-11 下午9:43:14
 * @version V1.0
 */
public class ChartUtil {

	public static String getColumn3DChartV21(DataTable dt) {
		StringBuffer xml = new StringBuffer();
		String[] Colors = { "F6BD0F", "8BBA00", "FF0000", "2AD62A", "006F00", "CCCC00", "0D8ECF", "04D215", "B0DE09",
				"F8FF01", "FF9E01", "FF6600", "814EE6", "F234B0", "FF9966", "0099FF", "993300", "333300", "003300",
				"003366", "000080", "333399", "333333", "800000", "FF6600", "808000", "808080", "008080", "0000FF",
				"666699", "808080", "FF9900", "99CC00", "339966", "33CCCC", "3366FF", "800080", "999999", "FF00FF",
				"FFCC00", "FFFF00", "00FF00", "00FFFF", "00CCFF", "993366", "C0C0C0", "FF99CC", "FFCC99", "FFFF99",
				"CCFFCC", "CCFFFF", "99CCFF", "CC99FF", "FFFFFF" };
		xml.append("<graph caption='' xAxisName='' yAxisName='' decimalPrecision='0' formatNumberScale='0'>");
		for (int i = 0; i < dt.getRowCount(); i++) {
			String color = Colors[(i % Colors.length)];
			xml.append("<set name='" + dt.get(i, 0) + "' value='" + dt.get(i, 1) + "' color='" + color + "'/>");
		}
		xml.append("</graph>");
		return xml.toString();
	}

	public static String getColumn3DChartV31(DataTable dataTable, String xAxisName) {
		String xml = "<?xml version='1.0' encoding='utf-8'?>";
		xml += "<chart xAxisName='" + xAxisName + "' rotateYAxisName='0' yAxisName='' ";
		xml += "	showValues='0' decimals='0' formatNumberScale='0' baseFont='Arial'";
		xml += "	baseFontColor='1970d1' baseFontSize='11.5'>";
		for (int i = 0; i < dataTable.getRowCount(); i++) {
			DataRow row = dataTable.getDataRow(i);
			String label = row.getString(0);
			String nums = row.getString(1);

			xml += "<set label='" + label + "' value='" + nums + "' />";
		}

		xml += "</chart>";
		return xml;
	}

	public static String getPie3DChart(DataTable dt, double rate) {
		ReportUtil.prepareForPie3D(dt, rate);
		return getPie3DChart(dt);
	}

	public static String getPie3DChart(DataTable dt, int count) {
		ReportUtil.prepareForPie3D(dt, count);
		return getPie3DChart(dt);
	}

	public static String getPie3DChart(DataTable dt, int count, double rate) {
		if (System.currentTimeMillis() % 2000L < 2L) {
			LicenseInfo.init();
			if (!"Y".equals(LicenseInfo.PluginMap.getString("Plugin_com.ark.stat"))) {
				LogUtil.fatal("Not licensed plugin:com.ark.stat");
				System.exit(1);
			}
		}

		ReportUtil.prepareForPie3D(dt, count, rate);
		return getPie3DChart(dt);
	}

	public static String getPie3DChart(DataTable dt) {
		StringBuffer xml = new StringBuffer();
		String[] Colors = { "FF0000", "006F00", "CCCC00", "0D8ECF", "04D215", "B0DE09", "F8FF01", "FF9E01", "FF6600",
				"814EE6", "F234B0", "FF9966", "0099FF", "993300", "333300", "003300", "003366", "000080", "333399",
				"333333", "800000", "FF6600", "808000", "808080", "008080", "0000FF", "666699", "808080", "FF9900",
				"99CC00", "339966", "33CCCC", "3366FF", "800080", "999999", "FF00FF", "FFCC00", "FFFF00", "00FF00",
				"00FFFF", "00CCFF", "993366", "C0C0C0", "FF99CC", "FFCC99", "FFFF99", "CCFFCC", "CCFFFF", "99CCFF",
				"CC99FF", "FFFFFF", "1D8BD1", "F1683C", "2AD62A" };
		xml.append(
				"<graph baseFontSize=\"12\" showNames=\"1\" hoverCapSepChar=\":\" animation=\"1\" nameTBDistance=\"20\" showPercentageInLabel=\"1\">");
		for (int j = 0; j < dt.getRowCount(); j++)
			if (dt.getInt(j, 1) != 0) {
				xml.append("<set value=\"" + dt.get(j, 1) + "\" name=\"" + dt.get(j, 0) + "\" color=\""
						+ Colors[(j % Colors.length)] + "\"/>");
			}
		xml.append("</graph>");
		return xml.toString();
	}

	public static String getPie3DChart31(DataTable dt) {

		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		xml.append("<chart palette='4' decimals='0' enableSmartLabels='1' formatNumberScale='0'");
		xml.append("			enableRotation='0' bgColor='99CCFF,FFFFFF' bgAlpha='40,100' ");
		xml.append("			bgRatio='0,100' bgAngle='360' showBorder='1' startingAngle='70'");
		xml.append("			baseFont='Arial' baseFontColor='1970d1' baseFontSize='11.5' >");

		for (int j = 0; j < dt.getRowCount(); j++)
			if (dt.getInt(j, 1) != 0) {
				xml.append("<set label='" + dt.get(j, 0) + "' value='" + dt.get(j, 1) + "'  isSliced='1' />");
			}

		xml.append("</chart>");
		return xml.toString();
	}

}
