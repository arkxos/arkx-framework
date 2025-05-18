package com.arkxos.framework.commons.collection;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arkxos.framework.commons.collection.tree.TreeIterator;
import com.arkxos.framework.commons.collection.tree.TreeNode;
import com.arkxos.framework.commons.collection.tree.Treex;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkxos.framework.commons.util.DateUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.commons.util.lang.ReflectionUtil;
import com.google.common.base.Joiner;

/**
 * @author Darkness
 * @date 2013-2-19 上午11:14:10
 * @version V1.0
 */
public class DataTableUtil {

	public static void main(String[] args) {
		System.out.println(DataTableUtil.class.getName());
	}

	private static Logger logger = LoggerFactory.getLogger(DataTableUtil.class);

	public static DataTable txtToDataTable(String content) {
		return txtToDataTable(content, null, ",", "\n");
	}

	public static DataTable txtToDataTable(String content, String fieldspliter, String rowspliter) {
		return txtToDataTable(content, null, fieldspliter, rowspliter);
	}

	public static DataTable txtToDataTable(String content, String[] columnNames) {
		return txtToDataTable(content, columnNames, ",", "\n");
	}

	public static DataTable txtToDataTable(String content, String[] columnNames, String fieldspliter,
			String rowspliter) {
		String[] rows = content.split(rowspliter);
		int startIndex = 0;
		if (columnNames == null) {
			columnNames = rows[0].split(fieldspliter);
			Mapx<String, String> map = new Mapx<>();
			for (int i = 0; i < columnNames.length; i++) {
				String name = columnNames[i];
				String tmp = name;
				int k = 2;
				while (map.containsKey(tmp)) {
					tmp = name + k++;
				}
				map.put(tmp, tmp);
				columnNames[i] = tmp;
			}
			startIndex = 1;
		}
		if ((columnNames == null) || (columnNames.length == 0)) {
			return null;
		}
		DataColumn[] dcs = new DataColumn[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			DataColumn dc = new DataColumn(columnNames[i], DataTypes.STRING, 200000);
			dcs[i] = dc;
		}
		Object[][] values = new Object[rows.length - startIndex][columnNames.length];
		for (int i = startIndex; i < rows.length; i++) {
			if (rows[i] == null) {
				continue;
			}
			String[] cols = rows[i].trim().split(fieldspliter);
			if (cols == null) {
				continue;
			}
			for (int j = 0; (j < cols.length) && (j < columnNames.length); j++) {
				values[(i - startIndex)][j] = StringUtil.javaDecode(cols[j]);
			}
		}
		return new DataTable(dcs, values);
	}

	public static String dataTableToTxt(DataTable dt) {
		return dataTableToTxt(dt, null, ",", "\n");
	}

	public static String dataTableToTxt(DataTable dt, String fieldspliter, String rowspliter) {
		return dataTableToTxt(dt, null, fieldspliter, rowspliter);
	}

	public static String dataTableToTxt(DataTable dt, String[] columnNames, String fieldspliter, String rowspliter) {
		StringBuilder sb = new StringBuilder();
		if (columnNames == null) {
			columnNames = new String[dt.getColumnCount()];
			for (int i = 0; i < columnNames.length; i++) {
				columnNames[i] = dt.getDataColumn(i).getColumnName();
			}
		}
		for (int i = 0; i < columnNames.length; i++) {
			if (i != 0) {
				sb.append(fieldspliter);
			}
			sb.append(columnNames[i]);
		}
		sb.append(rowspliter);
		for (int i = 0; i < dt.getRowCount(); i++) {
			for (int j = 0; j < dt.getColumnCount(); j++) {
				if (j != 0) {
					sb.append(fieldspliter);
				}
				sb.append(StringUtil.javaEncode(dt.getString(i, j)));
			}
			sb.append(rowspliter);
		}
		return sb.toString();
	}

	public static void dataTableToExcel(DataTable dt, String fileName) {
		dataTableToExcel(dt, fileName, null, null);
	}

	public static void dataTableToExcel(DataTable dt, String fileName, String[] columnNames, String[] widths) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			dataTableToExcel(dt, fos, columnNames, widths);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dataTableToExcel(DataTable dt, OutputStream os) {
		dataTableToExcel(dt, os, null, null);
	}

	public static void dataTableToExcel(DataTable dt, OutputStream os, String[] columnNames, String[] widths) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("First");
		try {
			dataTableToExcel(dt, sheet, columnNames, widths);
			wb.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dataTableToExcel(DataTable dt, HSSFSheet sheet, String[] columnNames, String[] widths) {
		HSSFWorkbook wb = sheet.getWorkbook();
		try {
			HSSFFont fontBold = wb.createFont();
			fontBold.setFontHeightInPoints((short) 10);
			fontBold.setFontName("宋体");
//			fontBold.setBoldweight((short) 700);

			HSSFFont fontNormal = wb.createFont();
			fontNormal.setFontHeightInPoints((short) 10);
			fontNormal.setFontName("宋体");
//			fontNormal.setBoldweight((short) 400);

			HSSFCellStyle styleBorderBold = wb.createCellStyle();
			styleBorderBold.setBorderBottom(BorderStyle.THIN);
			styleBorderBold.setBorderLeft(BorderStyle.THIN);
			styleBorderBold.setBorderRight(BorderStyle.THIN);
			styleBorderBold.setBorderTop(BorderStyle.THIN);
			styleBorderBold.setVerticalAlignment(VerticalAlignment.CENTER);
			styleBorderBold.setAlignment(HorizontalAlignment.CENTER);
			styleBorderBold.setWrapText(true);

			styleBorderBold.setFont(fontBold);

			HSSFCellStyle styleBorderNormal = wb.createCellStyle();
			styleBorderNormal.setBorderBottom(BorderStyle.THIN);
			styleBorderNormal.setBorderLeft(BorderStyle.THIN);
			styleBorderNormal.setBorderRight(BorderStyle.THIN);
			styleBorderNormal.setBorderTop(BorderStyle.THIN);
			styleBorderBold.setVerticalAlignment(VerticalAlignment.CENTER);
			styleBorderNormal.setFont(fontNormal);

			HSSFCellStyle styleBold = wb.createCellStyle();
			styleBold.setFont(fontBold);

			HSSFCellStyle styleNormal = wb.createCellStyle();
			styleNormal.setFont(fontNormal);

			HSSFRow row = sheet.getRow(0);
			if (row == null) {
				row = sheet.createRow(0);
			}
			for (int i = 0; i < dt.getColumnCount(); i++) {
				HSSFCell cell = row.getCell(i);
				if (cell == null) {
					cell = row.createCell(i);
				}
				cell.setCellType(CellType.STRING);
				cell.setCellStyle(styleBorderBold);
				if ((columnNames != null) && (columnNames.length > i))
					cell.setCellValue(columnNames[i]);
				else {
					cell.setCellValue(dt.getDataColumn(i).getColumnName());
				}

				row.setHeightInPoints(30.0F);

				if ((widths != null) && (widths.length > i)) {
					sheet.setColumnWidth(i, Integer.parseInt(widths[i]) * 37);
				}
			}

			for (int i = 0; i < dt.getRowCount(); i++) {
				row = sheet.getRow(i + 1);
				if (row == null) {
					row = sheet.createRow(i + 1);
				}

				row.setHeight((short) 345);
				for (int j = 0; j < dt.getColumnCount(); j++) {
					HSSFCell cell = row.getCell(j);
					if (cell == null) {
						cell = row.createCell(j);
					}
					cell.setCellType(CellType.STRING);
					cell.setCellStyle(styleBorderNormal);
					if (dt.get(i, j) == null)
						cell.setCellValue("");
					else if (dt.getDataColumn(j).getColumnType() == DataTypes.DATETIME)
						cell.setCellValue(DateUtil.toString((Date) dt.get(i, j), "yyyy-MM-dd"));
					else
						cell.setCellValue(dt.getString(i, j));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DataTable xlsToDataTable(String fileName) throws Exception {
		return xlsToDataTable(fileName, 0);
	}

	public static DataTable xlsToDataTable(String fileName, String sheetName) throws Exception {
		HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(fileName));
		int index = book.getSheetIndex(sheetName);
		if (index < 0) {
			throw new RuntimeException("未找到SheetName:" + sheetName);
		}
		return xlsToDataTable(book, index);
	}

	public static DataTable xlsToDataTable(String fileName, int sheetNo) throws Exception {
		HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(fileName));
		return xlsToDataTable(book, sheetNo);
	}

	public static DataTable xlsToDataTable(HSSFWorkbook book, int sheetNo) throws Exception {
		HSSFSheet sheet = book.getSheetAt(sheetNo);
		int maxRow = sheet.getPhysicalNumberOfRows();
		int maxCol = sheet.getRow(0).getPhysicalNumberOfCells();
		DataColumn[] dcs = new DataColumn[maxCol];
		int StartRowIndex = 1;
		Object[][] values = new Object[maxRow - StartRowIndex][maxCol];
		Mapx<Object, Object> map = new Mapx<>();
		for (int i = 0; i < maxCol; i++) {

			HSSFCell cell = sheet.getRow(0).getCell(i);
			String name = getCellValue(cell);
			String tmp = name;
			int k = 2;
			while (map.containsKey(tmp)) {
				tmp = name + k++;
			}
			map.put(tmp, tmp);

			DataColumn dc = new DataColumn(tmp, DataTypes.STRING, 2000);
//			dc.setColumnName();
//			dc.setColumnType();
			dcs[i] = dc;
		}
		for (int i = StartRowIndex; i < maxRow; i++) {
			HSSFRow row = sheet.getRow(i);
			if (row == null) {
				break;
			}
			HSSFCell cell = row.getCell(0);
			for (int j = 0; j < maxCol; j++) {
				cell = row.getCell(j);
				values[(i - StartRowIndex)][j] = getCellValue(cell);
			}
			logger.info("Reading Excel file,RowIndex is " + i + "...");
		}
		DataTable dt = new DataTable(dcs, values);
		return dt;
	}

	private static String getCellValue(HSSFCell cell) {
		String value = null;
		if (cell == null) {
			return "";
		}
		if (cell.getCellType() == CellType.NUMERIC) {
			if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
				value = DateUtil.toString(cell.getDateCellValue());
			} else if ((cell.getCellStyle().getDataFormat() == 31) || (cell.getCellStyle().getDataFormat() == 181)) {
				Date d = cell.getDateCellValue();
				value = DateUtil.toString(d);
			} else {
				value = NumberFormat.getNumberInstance().format(cell.getNumericCellValue());
				value = StringUtil.replaceEx(value, ",", "");
			}
		} else if (cell.getCellType() == CellType.FORMULA)
			value = String.valueOf(cell.getNumericCellValue());
		else {
			value = cell.getStringCellValue();
		}
		value = trimEx(value);
		return value;
	}

	private static String trimEx(String str) {
		String r = str.trim();
		while (r.startsWith("　")) {
			r = r.substring(1);
		}
		while (r.endsWith("　")) {
			r = r.substring(0, r.length() - 1);
		}
		return r.trim();
	}

//	public static DataTable extendBackupData(DataTable dt, String sql, String key) {
//		for (int i = 0; i < dt.getRowCount(); i++) {
//			DataRow dr = dt.getDataRow(i);
//			String keyValue = dr.getString(key);
//			DataTable backdt = new QueryBuilder(sql, new Object[] { keyValue }).executePagedDataTable(1, 0);
//			if (i == 0) {
//				for (int j = 0; j < backdt.getColumnCount(); j++) {
//					backdt.getDataColumn(j).setColumnName("Backup" + backdt.getDataColumn(j).getColumnName());
//					dt.insertColumn(backdt.getDataColumn(j).getColumnName());
//				}
//			}
//			if (backdt.getRowCount() == 1) {
//				System.arraycopy(backdt.getDataRow(0).getDataValues(), 0, dt.getDataRow(i).getDataValues(), dt.getColumnCount() - backdt.getColumnCount(), backdt.getColumnCount());
//			}
//		}
//		return null;
//	}

	public static void dataTableToWord(DataTable dt, XWPFTable table, String[] columnNames, String[] widths) {
		try {
			XWPFTableRow row = table.getRow(0);
			for (int i = 0; i < dt.getColumnCount(); i++) {
				XWPFTableCell cell = row.getCell(i);
				if (cell == null) {
					cell = row.addNewTableCell();
				}
				if ((columnNames != null) && (columnNames.length > i))
					cell.setText(columnNames[i]);
				else {
					cell.setText(dt.getDataColumn(i).getColumnName());
				}
			}

			for (int i = 0; i < dt.getRowCount(); i++) {
				row = table.createRow();
				row.setHeight(345);
				for (int j = 0; j < dt.getColumnCount(); j++) {
					XWPFTableCell cell = row.getCell(j);
					if (cell == null) {
						cell = row.addNewTableCell();
					}
					if (dt.get(i, j) == null) {
						cell.setText("");
					} else if (dt.getDataColumn(j).getColumnType() == DataTypes.DATETIME) {
						cell.setText(DateUtil.toString((Date) dt.get(i, j), "yyyy-MM-dd"));
					} else {
						String str = dt.getString(i, j);
						if (str.indexOf("\n") >= 0) {
							cell.removeParagraph(0);
							String[] arr = str.split("\\n");
							for (String line : arr)
								cell.addParagraph().createRun().setText(line);
						} else {
							cell.setText(dt.getString(i, j));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dataTableToWord(DataTable[] dt, String fileName, String[] columnNames, String[] widths) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			dataTableToWord(dt, fos, columnNames, widths);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dataTableToWord(DataTable[] dt, String fileName) {
		dataTableToWord(dt, fileName, null, null);
	}

	public static void dataTableToWord(DataTable[] dt, OutputStream os) {
		dataTableToWord(dt, os, null, null);
	}

	public static void dataTableToWord(DataTable[] dt, OutputStream os, String[] columnNames, String[] widths) {
		XWPFDocument document = new XWPFDocument();
		try {
			for (int i = 0; i < dt.length; i++) {
				XWPFTable table = document.createTable();
				dataTableToWord(dt[i], table, columnNames, widths);
				document.createParagraph().createRun().addBreak();
			}

			document.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author Darkness
	 * @date 2012-9-11 下午9:00:29
	 * @version V1.0
	 */
	public static DataTable sortDataTable(DataTable dataTable, String order, String next, String ordertype,
			String innerCodeField) {

		List<DataRow> result = new ArrayList<DataRow>();

		DataTable orderDT = new DataTable();
		DataTable nextDT = new DataTable();

		for (int i = 0; i < dataTable.getRowCount(); i++) {
			DataRow dataRow = dataTable.get(i);
			if (dataRow.getString(innerCodeField).startsWith(order)) {
				orderDT.insertRow(dataRow);
			}
			if (dataRow.getString(innerCodeField).startsWith(next)) {
				nextDT.insertRow(dataRow);
			}
		}

		if ("before".equalsIgnoreCase(ordertype)) {
			/**
			 * 向上拖动，在next之前插入所有orderDT，遇到order后跳过忽略即可
			 */
			for (int i = 0; i < dataTable.getRowCount(); i++) {
				if (dataTable.getString(i, innerCodeField).equals(next)) {// 在next之前，插入所有orderDT
					for (int m = 0; m < orderDT.getRowCount(); m++) {
						result.add(orderDT.getDataRow(m));
					}
				} else if (dataTable.getString(i, innerCodeField).equals(order)) {
					i = i - 1 + orderDT.getRowCount();
				} else {
					result.add(dataTable.getDataRow(i));
				}

			}
		} else if ("after".equalsIgnoreCase(ordertype)) {
			/**
			 * 向下拖动，遇到order跳过，遇到next后，先添加nextDT，在添加orderDT
			 */
			for (int i = 0; i < dataTable.getRowCount(); i++) {
				if (dataTable.getString(i, innerCodeField).equals(order)) {
					i = i - 1 + orderDT.getRowCount();
				} else if (dataTable.getString(i, innerCodeField).equals(next)) {
					for (int m = 0; (nextDT != null) && (m < nextDT.getRowCount()); m++) {
						result.add(nextDT.getDataRow(m));
					}

					for (int j = 0; (orderDT != null) && (j < orderDT.getRowCount()); j++) {
						result.add(orderDT.getDataRow(j));
					}
					i = i - 1 + nextDT.getRowCount();
				} else {
					result.add(dataTable.getDataRow(i));
				}
			}
		}

		return new DataTable().insertRows(result);
	}

	/**
	 * 将List<Object> 转换成DataTable
	 * 
	 * @author Darkness
	 * @date 2012-11-26 下午03:10:40
	 * @version V1.0
	 */
	public static DataTable toDataTable(List<?> entities) {
		if (entities == null || entities.size() == 0) {
			return new DataTable();
		}

		Field[] fields = ReflectionUtil.getDeclaredFields(entities.get(0).getClass());

		DataColumn[] dcs = new DataColumn[fields.length];

		Object[][] values = new Object[entities.size()][fields.length];

		for (int i = 0; i < fields.length; i++) {
			DataColumn dc = new DataColumn(fields[i].getName(), DataColumn.convertType(fields[i].getType()), 20000);

			dcs[i] = dc;
		}

		for (int i = 0; i < entities.size(); i++) {

			Object entity = entities.get(i);

			for (int j = 0; j < fields.length; j++) {
				values[i][j] = ReflectionUtil.getFieldValue(entity, fields[j].getName());
			}
		}
		DataTable dt = new DataTable(dcs, values);
		return dt;
	}

	public static DataTable sortTreeDataTable(DataTable dt, String identifierColumnName,
			String parentIdentifierColumnName) {
		if ((dt == null) || dt.isEmpty()) {
			return dt;
		}
		if (dt.getDataColumn(identifierColumnName) == null) {
			logger.warn("DataGridAction.sortTreeDataTable():ID column not found:" + identifierColumnName);
		}
		if (dt.getDataColumn(parentIdentifierColumnName) == null) {
			logger.warn("DataGridAction.sortTreeDataTable():Parent column not found:" + parentIdentifierColumnName);
		}
		if (dt.getDataColumn("_TreeLevel") != null) {
			dt.deleteColumn("_TreeLevel");
		}
		dt.insertColumn(DataColumn.intColumn("_TreeLevel"));
		Treex<String, DataRow> tree = Treex.dataTableToTree(dt, identifierColumnName, parentIdentifierColumnName);
		TreeIterator<String, DataRow> ti = tree.iterator();
		DataTable dest = new DataTable(dt.getDataColumns(), null);
		while (ti.hasNext()) {
			TreeNode<String, DataRow> node = ti.next();
			DataRow dr = node.getValue();// inner_code:null,parent_inner_code:null
			if (dr != null) {
				dr.set("_TreeLevel", node.getDepth());
				dr.set("inner_code", dr.get("tid"));
				dr.set("parent_inner_code", dr.get("tpid"));
				dest.insertRow(dr);
			}
		}
		return dest;
	}

	/**
	 * 将一个dataTable转化成 new DataTable("id", "value")
	 * 
	 * @method toIdValueDataTable
	 * @param {DataTable} dataTable
	 * @param {String}    idColumnName
	 * @param {String}    valueColumnName
	 */
	public static DataTable toIdValueDataTable(DataTable dataTable, String idColumnName, String valueColumnName) {
		DataTable result = new DataTable("id", "value");
		for (int i = 0; i < dataTable.getRowCount(); i++) {
			DataRow dataRow = dataTable.get(i);
			result.insertRow(dataRow.get(idColumnName), dataRow.get(valueColumnName));
		}
		return result;
	}

	public static void joinColumns(DataTable dataTable, List<String> columnNames, String newColumnName) {
		if (!dataTable.containsColumn(newColumnName)) {
			dataTable.insertColumn(newColumnName);
		}

		for (DataRow dataRow : dataTable) {
			List<String> allValues = new ArrayList<>();

			for (String columnName : columnNames) {
				String columnValue = dataRow.getString(columnName);
				if (!StringUtil.isEmpty(columnValue)) {
					allValues.add(columnValue);
				}
			}

			dataRow.set(newColumnName, Joiner.on(",").join(allValues));
		}
	}

	public static List<Map<String, Object>> dataTableToList(DataTable dataTable) {
		List<Map<String, Object>> result = new ArrayList<>();

		if (dataTable == null || dataTable.getRowCount() == 0) {
			return result;
		}

		// convert result to entity.

		for (DataRow dataRow : dataTable.getDataRows()) {

			Map<String, Object> t = dataRowtoMap(dataRow);

			result.add(t);
		}

		return result;
	}

	public static Map<String, Object> dataRowtoMap(DataRow dataRow) {

		// convert result to entity.
		Map<String, Object> result = new HashMap<>();

		for (DataColumn dataColumn : dataRow.getDataColumns()) {
			String columnName = dataColumn.getColumnName();
			
			Object value = dataRow.get(columnName);
			result.put(FieldPropertyConvert.fieldToProperty2(columnName), value);
		}

		return result;
	}
}