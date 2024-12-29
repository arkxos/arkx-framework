package com.arkxos.framework.commons.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 合并行有值
 * 
 * @author Darkness
 * @date 2013-10-11 下午03:54:43
 * @version V1.0
 */
public class MergeWithValueExcelReader extends SimpleExcelReader {

	public MergeWithValueExcelReader(String path) {
		super(path);
	}

	@Override
	protected String getCellValue(Sheet sheet, int rowIndex, Cell cell) {
		boolean isMerge = isMergedRegion(sheet, rowIndex, cell.getColumnIndex());
		// 判断是否具有合并单元格
		if (isMerge) {
			Row row = sheet.getRow(rowIndex);
			return getMergedRegionValue(sheet, row.getRowNum(), cell.getColumnIndex());
		}
		return super.getCellValue(sheet, rowIndex, cell);
	}

	/**
	 * 判断指定的单元格是否是合并单元格
	 * 
	 * @param sheet
	 * @param row
	 *            行下标
	 * @param column
	 *            列下标
	 * @return
	 */
	public static boolean isMergedRegion(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			int firstColumn = range.getFirstColumn();
			int lastColumn = range.getLastColumn();
			int firstRow = range.getFirstRow();
			int lastRow = range.getLastRow();
			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					return true;
				}
			}
		}
		return false;
	}

	public static CellRangeAddress getMergedRegion(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();

		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();

			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					return ca;
				}
			}
		}
		
		return null;
	}
	/**
	 * 获取合并单元格的值
	 * 
	 * @param sheet
	 * @param row
	 * @param column
	 * @return
	 */
	public  String getMergedRegionValue(Sheet sheet, int row, int column) {
		
		CellRangeAddress ca = getMergedRegion(sheet, row, column);
		Row fRow = sheet.getRow(ca.getFirstRow());
		Cell fCell = fRow.getCell(ca.getFirstColumn());
		return super.getCellValue(sheet, row, fCell);
	}
}
