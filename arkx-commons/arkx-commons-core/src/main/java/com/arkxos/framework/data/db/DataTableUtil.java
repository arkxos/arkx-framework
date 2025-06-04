package com.arkxos.framework.data.db;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.arkxos.framework.Config;
import com.arkxos.framework.Current;
import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.DateUtil;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.StringUtil;
import com.arkxos.framework.core.JsonResult;
import com.arkxos.framework.core.method.IMethodLocator;
import com.arkxos.framework.core.method.MethodLocatorUtil;
import com.arkxos.framework.cosyui.control.DataGridAction;
import com.arkxos.framework.cosyui.control.PagedData;
import com.arkxos.framework.cosyui.html.HtmlElement;
import com.arkxos.framework.cosyui.html.HtmlParser;
import com.arkxos.framework.cosyui.html.HtmlTD;
import com.arkxos.framework.cosyui.html.HtmlTR;
import com.arkxos.framework.cosyui.html.HtmlTable;
import com.arkxos.framework.cosyui.tag.RestUtil;
import com.arkxos.framework.cosyui.template.exception.TemplateRuntimeException;
import com.arkxos.framework.cosyui.web.RequestData;
import com.arkxos.framework.data.jdbc.Session;
import com.arkxos.framework.data.jdbc.SessionFactory;
import com.arkxos.framework.security.PrivCheck;

public class DataTableUtil {
	public static void main(String[] args) {
		DataTable dt = new DataTable();
		dt.insertColumns(new String[] { "id", "NAME" });
		for (int i = 0; i < 10; i++) {
			dt.insertRow(new Object[] { "id" + i, "name" + i });
		}
		dataTableToExcel(dt, "e:\\test.xlsx");
	}

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
			Mapx<String, String> map = new Mapx();
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
			DataColumn dc = new DataColumn();
			dc.setColumnName(columnNames[i]);
			dc.setColumnType(DataTypes.STRING);
			dcs[i] = dc;
		}
		Object[][] values = new Object[rows.length - startIndex][columnNames.length];
		for (int i = startIndex; i < rows.length; i++) {
			if (rows[i] != null) {
				String[] cols = rows[i].trim().split(fieldspliter);
				if (cols != null) {
					for (int j = 0; (j < cols.length) && (j < columnNames.length); j++) {
						values[(i - startIndex)][j] = StringUtil.javaDecode(cols[j]);
					}
				}
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
			dataTableToExcel(dt, fos, columnNames, widths, fileName.endsWith(".xlsx"));
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dataTableToExcel(DataTable dt, OutputStream os) {
		dataTableToExcel(dt, os, null, null, true);
	}

	public static void dataTableToExcel(DataTable dt, OutputStream os, String[] columnNames, String[] widths,
			boolean is2007) {
		Workbook wb = null;
		if (is2007) {
			wb = new XSSFWorkbook();
		} else {
			wb = new HSSFWorkbook();
		}
		Sheet sheet = wb.createSheet("First");
		try {
			dataTableToExcel(dt, sheet, columnNames, widths);
			wb.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dataTableToExcel(DataTable dt, Sheet sheet, String[] columnNames, String[] widths) {
		Workbook wb = sheet.getWorkbook();
		try {
			Font fontBold = createFontBold(wb);
			Font fontNormal = createFontNormal(wb);
			CellStyle styleBorderBold = createStyleBorderBold(wb, fontBold);
			CellStyle styleBorderNormal = createStyleBorderNormal(wb, fontNormal);

			Row row = sheet.getRow(0);
			if (row == null) {
				row = sheet.createRow(0);
			}
			for (int i = 0; i < dt.getColumnCount(); i++) {
				Cell cell = row.getCell(i);
				if (cell == null) {
					cell = row.createCell(i);
				}
				cell.setCellType(CellType.STRING);
				cell.setCellStyle(styleBorderBold);
				if ((columnNames != null) && (columnNames.length > i)) {
					cell.setCellValue(columnNames[i]);
				} else {
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
					Cell cell = row.getCell(j);
					if (cell == null) {
						cell = row.createCell(j);
					}
					cell.setCellStyle(styleBorderNormal);
					int colType = dt.getDataColumn(j).getColumnType().code();
					if (dt.get(i, j) == null) {
						cell.setCellType(CellType.STRING);
						cell.setCellValue("");
					} else if (colType == 12) {
						cell.setCellType(CellType.STRING);
						cell.setCellValue(DateUtil.toString((Date) dt.get(i, j), "yyyy-MM-dd"));
					} else if ((colType == 3) || (colType == 4) || (colType == 5) || (colType == 6) || (colType == 7)
							|| (colType == 8) || (colType == 9) || (colType == 11)) {
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(dt.getDouble(i, j));
					} else {
						cell.setCellType(CellType.STRING);
						cell.setCellValue(dt.getString(i, j));
					}
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
		Workbook book = null;
		if (fileName.endsWith(".xlsx")) {
			book = new XSSFWorkbook(new FileInputStream(fileName));
		} else {
			book = new HSSFWorkbook(new FileInputStream(fileName));
		}
		int index = book.getSheetIndex(sheetName);
		if (index < 0) {
			throw new RuntimeException("未找到SheetName:" + sheetName);
		}
		return xlsToDataTable(book, index);
	}

	public static DataTable xlsToDataTable(String fileName, int sheetNo) throws Exception {
		Workbook book = null;
		if (fileName.endsWith(".xlsx")) {
			book = new XSSFWorkbook(new FileInputStream(fileName));
		} else {
			book = new HSSFWorkbook(new FileInputStream(fileName));
		}
		return xlsToDataTable(book, sheetNo);
	}

	public static DataTable xlsToDataTable(Workbook book, int sheetNo) throws Exception {
		Sheet sheet = book.getSheetAt(sheetNo);
		int maxRow = sheet.getPhysicalNumberOfRows();
		int maxCol = sheet.getRow(0).getPhysicalNumberOfCells();
		DataColumn[] dcs = new DataColumn[maxCol];
		int StartRowIndex = 1;
		Object[][] values = new Object[maxRow - StartRowIndex][maxCol];
		Mapx<String, String> map = new Mapx();
		for (int i = 0; i < maxCol; i++) {
			DataColumn dc = new DataColumn();
			Cell cell = sheet.getRow(0).getCell(i);
			String name = getCellValue(cell);
			String tmp = name;
			int k = 2;
			while (map.containsKey(tmp)) {
				tmp = name + k++;
			}
			map.put(tmp, tmp);

			dc.setColumnName(tmp);
			dc.setColumnType(DataTypes.STRING);
			dcs[i] = dc;
		}
		for (int i = StartRowIndex; i < maxRow; i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				break;
			}
			Cell cell = row.getCell(0);
			for (int j = 0; j < maxCol; j++) {
				cell = row.getCell(j);
				values[(i - StartRowIndex)][j] = getCellValue(cell);
			}
			LogUtil.info("Reading Excel file,RowIndex is " + i + "...");
		}
		DataTable dt = new DataTable(dcs, values);
		return dt;
	}

	private static String getCellValue(Cell cell) {
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
		} else if (cell.getCellType() == CellType.FORMULA) {
			value = String.valueOf(cell.getNumericCellValue());
		} else {
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

	public static DataTable extendBackupData(DataTable dt, String sql, String key) {
		for (int i = 0; i < dt.getRowCount(); i++) {
			DataRow dr = dt.getDataRow(i);
			String keyValue = dr.getString(key);
			DataTable backdt = getSession().createQuery(sql, new Object[] { keyValue }).executePagedDataTable(1, 0).getData();
			if (i == 0) {
				for (int j = 0; j < backdt.getColumnCount(); j++) {
					backdt.getDataColumn(j).setColumnName("Backup" + backdt.getDataColumn(j).getColumnName());
					dt.insertColumn(backdt.getDataColumn(j).getColumnName());
				}
			}
			if (backdt.getRowCount() == 1) {
				System.arraycopy(backdt.getDataRow(0).getDataValues(), 0, dt.getDataRow(i).getDataValues(),
						dt.getColumnCount() - backdt.getColumnCount(), backdt.getColumnCount());
			}
		}
		return null;
	}

	public static void dataTableToWord(DataTable dt, XWPFTable table, String[] columnNames, String[] widths) {
		try {
			XWPFTableRow row = table.getRow(0);
			for (int i = 0; i < dt.getColumnCount(); i++) {
				XWPFTableCell cell = row.getCell(i);
				if (cell == null) {
					cell = row.addNewTableCell();
				}
				if ((columnNames != null) && (columnNames.length > i)) {
					cell.setText(columnNames[i]);
				} else {
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
							String[] arrayOfString1;
							j = (arrayOfString1 = arr).length;
							for (i = 0; i < j; i++) {
								String line = arrayOfString1[i];
								cell.addParagraph().createRun().setText(line);
							}
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

	public static void prepareHtmlTableToExcel(OutputStream os, DataGridAction dga, DataTable dataSource, String xls,
			String strIndexes, String strRows, String strWidths) {
		try {
			Workbook wb = null;
			if ("2007".equals(Config.getExcelVersion())) {
				wb = new XSSFWorkbook();
			} else {
				wb = new HSSFWorkbook();
			}
			dga.bindData(dataSource);

			String result = dga.getResult();
			HtmlParser p = new HtmlParser(result);
			HtmlTable table = new HtmlTable((HtmlElement) p.parse().getTopElementsByTagName("table").get(0));
			String[] rows = null;
			if (StringUtil.isNotEmpty(strRows)) {
				rows = strRows.split(",");
			}
			List<HtmlTR> trs = table.getTRList();
			if (trs.size() > 0) {
				HtmlTR last = (HtmlTR) trs.get(trs.size() - 1);
				String ztype = last.getAttribute("ztype").toLowerCase();
				if ((ztype.equals("blank")) || (ztype.equals("pagebar")) || (ztype.equals("simplepagebar"))) {
					table.removeTR(trs.size() - 1);
				}
			}
			htmlTableToExcel(wb, os, table, strWidths.split(","), strIndexes.split(","), rows, "sheet1");
			wb.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void htmlTableToExcel(Workbook wb, OutputStream os, HtmlTable table, String[] widths,
			String[] indexes, String[] rows, String sheetName) {
		Sheet sheet = wb.createSheet(sheetName);
		try {
			Font fontBold = createFontBold(wb);
			Font fontNormal = createFontNormal(wb);
			CellStyle styleBorderBold = createStyleBorderBold(wb, fontBold);
			CellStyle styleBorderNormal = createStyleBorderNormal(wb, fontNormal);

			Row row = sheet.getRow(0);
			if (row == null) {
				row = sheet.createRow(0);
			}
			String[] digits = new String[indexes.length];
			List<HtmlTR> trs = table.getTRList();
			for (int i = 0; i < indexes.length; i++) {
				Cell cell = row.getCell(i);
				if (cell == null) {
					cell = row.createCell(i);
				}
				cell.setCellType(CellType.STRING);
				cell.setCellStyle(styleBorderBold);
				HtmlTD td = ((HtmlTR) trs.get(0)).getTD(Integer.parseInt(indexes[i]));
				digits[i] = td.getAttribute("digit");
				String html = td.getInnerHTML();
				html = html.replaceAll("<.*?>", "");
				html = StringUtil.htmlDecode(html);
				cell.setCellValue(html.trim());
				row.setHeightInPoints(23.0F);
				if ((widths != null) && (widths.length > i)) {
					double w = Double.parseDouble(widths[i]);
					if (w < 100.0D) {
						w = 100.0D;
					}
					sheet.setColumnWidth(i, Double.valueOf(w * 35.0D).intValue());
				}
			}
			for (int i = 0; i < indexes.length; i++) {
				int j = Integer.parseInt(indexes[i]);
				if (rows != null) {
					for (int k = 0; k < rows.length; k++) {
						int n = Integer.parseInt(rows[k]);
						String ztype = ((HtmlTR) trs.get(n)).getAttribute("ztype");
						if (k == trs.size() - 1) {
							if ((StringUtil.isNotEmpty(ztype)) && (ztype.equalsIgnoreCase("pagebar"))) {
								break;
							}
							String html = ((HtmlTR) trs.get(n)).getInnerHTML();
							if ((StringUtil.isEmpty(html)) || (html.indexOf("PageBarIndex") > 0)) {
								break;
							}
						}
						row = sheet.getRow(k + 1);
						if (row == null) {
							row = sheet.createRow(k + 1);
							row.setHeightInPoints(18.0F);
						}
						Cell cell = row.getCell(i);
						if (cell == null) {
							cell = row.createCell(i);
						}
						cell.setCellStyle(styleBorderNormal);

						String html = ((HtmlTR) trs.get(n)).getTD(j).getOuterHTML();
						html = html.replaceAll("<.*?>", "");
						html = StringUtil.htmlDecode(html).trim();

						String digit = digits[i];
						if (StringUtil.isEmpty(digit)) {
							cell.setCellType(CellType.STRING);
							cell.setCellValue(html);
						} else {
							try {
								cell.setCellType(CellType.NUMERIC);
								cell.setCellValue(Double.parseDouble(html));
							} catch (Exception e) {
								cell.setCellType(CellType.STRING);
								cell.setCellValue(html);
							}
						}
					}
				} else {
					for (int k = 1; k < trs.size(); k++) {
						String ztype = ((HtmlTR) trs.get(k)).getAttribute("ztype");
						if (k == table.getChildren().size() - 1) {
							if ((StringUtil.isNotEmpty(ztype)) && (ztype.equalsIgnoreCase("pagebar"))) {
								break;
							}
							String html = ((HtmlTR) trs.get(k)).getInnerHTML();
							if ((StringUtil.isEmpty(html)) || (html.indexOf("PageBarIndex") > 0)) {
								break;
							}
						}
						row = sheet.getRow(k);
						if (row == null) {
							row = sheet.createRow(k);
							row.setHeightInPoints(18.0F);
						}
						Cell cell = row.getCell(i);
						if (cell == null) {
							cell = row.createCell(i);
						}
						cell.setCellStyle(styleBorderNormal);

						String html = "";
						List<HtmlTD> tds = ((HtmlTR) trs.get(k)).getTDList();
						if (tds.size() > j) {
							html = ((HtmlTD) tds.get(j)).getOuterHTML();
							html = html.replaceAll("<.*?>", "");
							html = StringUtil.htmlDecode(html).trim();
						}
						String digit = digits[i];
						if (StringUtil.isEmpty(digit)) {
							cell.setCellType(CellType.STRING);
							cell.setCellValue(html);
						} else {
							try {
								cell.setCellType(CellType.NUMERIC);
								cell.setCellValue(Double.parseDouble(html));
							} catch (Exception e) {
								cell.setCellType(CellType.STRING);
								cell.setCellValue(html);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void prepareHtmlTableToExcel(OutputStream os, DataGridAction dga, String rowTotal,
			String excelPageFlag, String pageIndex, String pageSize, String pageFlag, String method, String rest, String xls,
			String strIndexes, String strRows, String strWidths, int pageLimit) {
		try {
			Workbook wb = null;
			if ("2007".equals(Config.getExcelVersion())) {
				wb = new XSSFWorkbook();
			} else {
				wb = new HSSFWorkbook();
			}
			String tmptTotal = (StringUtil.isEmpty(rowTotal)) || (rowTotal.equals("null")) ? "0" : rowTotal;
			int pageTotal = Integer.parseInt(tmptTotal) / 10000;
			if (Integer.parseInt(tmptTotal) % 10000 != 0) {
				pageTotal++;
			}
			if (pageTotal == 0) {
				pageTotal = 1;
			}
			int tmpPageIndex = 0;
			int tmpPageSize = 10000;
			if ("1".equals(excelPageFlag)) {
				tmpPageIndex = StringUtil.isEmpty(pageIndex) ? 0 : Integer.parseInt(pageIndex);
				pageTotal = tmpPageIndex + pageLimit;
				tmpPageSize = StringUtil.isEmpty(pageSize) ? 0 : Integer.parseInt(pageSize);
			}
			Sheet sheet = null;
			List<HtmlTR> trlist = null;
			CellStyle styleBorderNormal = null;
			String[] digits = null;
			String[] indexes = null;
			int r = 1;
			for (int i = tmpPageIndex; i < pageTotal; i++) {
				if ("true".equals(pageFlag)) {
					dga.setPageEnabled(true);
					dga.setPageIndex(i);
					dga.setPageSize(tmpPageSize);
				}
				
				Mapx<String, Object> map = Current.getRequest();
				for (String k : map.keySet()) {
					if (k.startsWith(xls)) {
						Object v = map.get(k);
						map.remove(k);
						map.put(k.substring(xls.length()), v);
					}
				}
				dga.setParams(map);
				dga.setAjaxRequest(true);
				
				if(!StringUtil.isEmpty(method)) {
					IMethodLocator m = MethodLocatorUtil.find(method);
					PrivCheck.check(m);
					
					m.execute(new Object[] { dga });
				} else {
					RequestData requestData = Current.getRequest();
					requestData.put("pageIndex", dga.getPageIndex());
					requestData.put("pageSize", dga.getPageSize());
					dga.setParams(Current.getRequest());
					
					JsonResult jsonResult = RestUtil.post(rest, requestData, PagedData.class);
					if(!jsonResult.isSuccess()) {
						throw new TemplateRuntimeException(jsonResult.getMessage());
					}
					PagedData pagedData = (PagedData)jsonResult.getData();
					dga.setTotal(pagedData.getTotal());
					dga.bindData(pagedData.getDataTable());
				}
				
				String result = dga.getResult();
				HtmlParser p = new HtmlParser(result);
				HtmlTable table = new HtmlTable((HtmlElement) p.parse().getTopElementsByTagName("table").get(0));
				String[] rows = null;
				if (StringUtil.isNotEmpty(strRows)) {
					rows = strRows.split(",");
				}
				List<HtmlTR> trs = table.getTRList();
				if (trs.size() > 0) {
					HtmlTR last = (HtmlTR) trs.get(trs.size() - 1);
					String ztype = last.getAttribute("ztype").toLowerCase();
					if ((ztype.equals("blank")) || (ztype.equals("pagebar")) || (ztype.equals("simplepagebar"))) {
						table.removeTR(trs.size() - 1);
					}
				}
				int tchidrenSize = table.getChildren().size();
				if (i == tmpPageIndex) {
					indexes = strIndexes.split(",");
					String[] widths = strWidths.split(",");
					sheet = wb.createSheet("sheet" + (i + 1));
					Font fontBold = createFontBold(wb);
					Font fontNormal = createFontNormal(wb);
					CellStyle styleBorderBold = createStyleBorderBold(wb, fontBold);

					styleBorderNormal = createStyleBorderNormal(wb, fontNormal);

					Row row = sheet.getRow(0);
					if (row == null) {
						row = sheet.createRow(0);
					}
					digits = new String[indexes.length];
					trlist = table.getTRList();
					for (int j = 0; j < indexes.length; j++) {
						Cell cell = row.getCell(j);
						if (cell == null) {
							cell = row.createCell(j);
						}
						cell.setCellType(CellType.STRING);
						cell.setCellStyle(styleBorderBold);
						HtmlTD td = ((HtmlTR) trlist.get(0)).getTD(Integer.parseInt(indexes[j]));
						digits[j] = td.getAttribute("digit");
						String html = td.getInnerHTML();
						html = html.replaceAll("<.*?>", "");
						html = StringUtil.htmlDecode(html);
						cell.setCellValue(html.trim());
						row.setHeightInPoints(23.0F);
						if ((widths != null) && (widths.length > j)) {
							double w = Double.parseDouble(widths[j]);
							if (w < 100.0D) {
								w = 100.0D;
							}
							sheet.setColumnWidth(j, Double.valueOf(w * 35.0D).intValue());
						}
					}
				} else {
					trlist = table.getTRList();
				}
				r = htmlTableToExcel(sheet, trlist, styleBorderNormal, digits, tchidrenSize, indexes, rows, r);
			}
			wb.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int htmlTableToExcel(Sheet sheet, List<HtmlTR> trs, CellStyle styleBorderNormal, String[] digits,
			int tchidrenSize, String[] indexes, String[] rows, int r) {
		try {
			if (rows != null) {
				for (int k = 0; k < rows.length; k++) {
					int n = Integer.parseInt(rows[k]);
					String ztype = ((HtmlTR) trs.get(n)).getAttribute("ztype");
					if (k == trs.size() - 1) {
						if ((StringUtil.isNotEmpty(ztype)) && (ztype.equalsIgnoreCase("pagebar"))) {
							break;
						}
						String html = ((HtmlTR) trs.get(n)).getInnerHTML();
						if ((StringUtil.isEmpty(html)) || (html.indexOf("PageBarIndex") > 0)) {
							break;
						}
					}
					Row row = sheet.getRow(r);
					if (row == null) {
						row = sheet.createRow(r);
						row.setHeightInPoints(18.0F);
					}
					r++;
					for (int i = 0; i < indexes.length; i++) {
						int j = Integer.parseInt(indexes[i]);
						Cell cell = row.getCell(i);
						if (cell == null) {
							cell = row.createCell(i);
						}
						cell.setCellStyle(styleBorderNormal);
						String html = ((HtmlTR) trs.get(n)).getTD(j).getOuterHTML();
						html = html.replaceAll("<.*?>", "");
						html = StringUtil.htmlDecode(html).trim();

						String digit = digits[i];
						if (StringUtil.isEmpty(digit)) {
							cell.setCellType(CellType.STRING);
							cell.setCellValue(html);
						} else {
							try {
								cell.setCellType(CellType.NUMERIC);
								cell.setCellValue(Double.parseDouble(html));
							} catch (Exception e) {
								cell.setCellType(CellType.STRING);
								cell.setCellValue(html);
							}
						}
					}
				}
			} else {
				for (int k = 1; k < trs.size(); k++) {
					String ztype = ((HtmlTR) trs.get(k)).getAttribute("ztype");
					if (k == tchidrenSize - 1) {
						if ((StringUtil.isNotEmpty(ztype)) && (ztype.equalsIgnoreCase("pagebar"))) {
							break;
						}
						String html = ((HtmlTR) trs.get(k)).getInnerHTML();
						if ((StringUtil.isEmpty(html)) || (html.indexOf("PageBarIndex") > 0)) {
							break;
						}
					}
					Row row = sheet.getRow(r);
					if (row == null) {
						row = sheet.createRow(r);
						row.setHeightInPoints(18.0F);
					}
					r++;
					for (int i = 0; i < indexes.length; i++) {
						int j = Integer.parseInt(indexes[i]);
						Cell cell = row.getCell(i);
						if (cell == null) {
							cell = row.createCell(i);
						}
						cell.setCellStyle(styleBorderNormal);
						String html = "";
						List<HtmlTD> tds = ((HtmlTR) trs.get(k)).getTDList();
						if (tds.size() > j) {
							html = ((HtmlTD) tds.get(j)).getOuterHTML();
							html = html.replaceAll("<.*?>", "");
							html = StringUtil.htmlDecode(html).trim();
						}
						String digit = digits[i];
						if (StringUtil.isEmpty(digit)) {
							cell.setCellType(CellType.STRING);
							cell.setCellValue(html);
						} else {
							try {
								cell.setCellType(CellType.NUMERIC);
								cell.setCellValue(Double.parseDouble(html));
							} catch (Exception e) {
								cell.setCellType(CellType.STRING);
								cell.setCellValue(html);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	public static Mapx<String, DataTable> splitDataTableByColumn(DataTable dt, String column) {
		Mapx<String, DataTable> map = new Mapx();
		if (!dt.containsColumn(column)) {
			return null;
		}
		for (DataRow dr : dt) {
			DataTable dtItem = null;
			if (map.containsKey(dr.getString(column))) {
				dtItem = (DataTable) map.get(dr.getString(column));
			} else {
				dtItem = new DataTable();
				map.put(dr.getString(column), dtItem);
			}
			dtItem.insertRow(dr);
		}
		return map;
	}

	private static Font createFontBold(Workbook wb) {
		Font fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) 10);
		fontBold.setFontName("宋体");
//		fontBold.setBoldweight((short) 700);
		return fontBold;
	}

	private static Font createFontNormal(Workbook wb) {
		Font fontNormal = wb.createFont();
		fontNormal.setFontHeightInPoints((short) 10);
		fontNormal.setFontName("宋体");
//		fontNormal.setBoldweight((short) 400);
		return fontNormal;
	}

	private static CellStyle createStyleBorderBold(Workbook wb, Font fontBold) {
		CellStyle styleBorderBold = wb.createCellStyle();
		styleBorderBold.setBorderBottom(BorderStyle.THIN);
		styleBorderBold.setBorderLeft(BorderStyle.THIN);
		styleBorderBold.setBorderRight(BorderStyle.THIN);
		styleBorderBold.setBorderTop(BorderStyle.THIN);
		styleBorderBold.setVerticalAlignment(VerticalAlignment.CENTER);
		styleBorderBold.setAlignment(HorizontalAlignment.CENTER);
		styleBorderBold.setWrapText(true);

		styleBorderBold.setFont(fontBold);
		return styleBorderBold;
	}

	private static CellStyle createStyleBorderNormal(Workbook wb, Font fontNormal) {
		CellStyle styleBorderNormal = wb.createCellStyle();
		styleBorderNormal.setBorderBottom(BorderStyle.THIN);
		styleBorderNormal.setBorderLeft(BorderStyle.THIN);
		styleBorderNormal.setBorderRight(BorderStyle.THIN);
		styleBorderNormal.setBorderTop(BorderStyle.THIN);
		styleBorderNormal.setVerticalAlignment(VerticalAlignment.CENTER);
		styleBorderNormal.setFont(fontNormal);
		return styleBorderNormal;
	}

	public static Session getSession() {
		return SessionFactory.currentSession();
	}
}
