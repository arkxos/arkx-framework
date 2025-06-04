package io.arkx.framework.data.db;

import io.arkx.framework.Config;
import io.arkx.framework.Current;
import io.arkx.framework.WebCurrent;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.core.JsonResult;
import io.arkx.framework.core.method.IMethodLocator;
import io.arkx.framework.core.method.MethodLocatorUtil;
import io.arkx.framework.cosyui.control.DataGridAction;
import io.arkx.framework.cosyui.control.PagedData;
import io.arkx.framework.cosyui.html.HtmlElement;
import io.arkx.framework.cosyui.html.HtmlParser;
import io.arkx.framework.cosyui.html.HtmlTD;
import io.arkx.framework.cosyui.html.HtmlTR;
import io.arkx.framework.cosyui.html.HtmlTable;
import io.arkx.framework.cosyui.tag.RestUtil;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;
import io.arkx.framework.cosyui.web.RequestData;
import io.arkx.framework.security.PrivCheck;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.util.List;

/**
 * @author Nobody
 * @date 2025-06-04 20:49
 * @since 1.0
 */
public class WebDataTableUtil extends DataTableUtil {

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

				Mapx<String, Object> map = WebCurrent.getRequest();
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
					RequestData requestData = WebCurrent.getRequest();
					requestData.put("pageIndex", dga.getPageIndex());
					requestData.put("pageSize", dga.getPageSize());
					dga.setParams(WebCurrent.getRequest());

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

}
