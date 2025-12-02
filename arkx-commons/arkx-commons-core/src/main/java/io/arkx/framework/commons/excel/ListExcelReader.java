package io.arkx.framework.commons.excel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;

import io.arkx.framework.commons.collection.TwoTuple;
import io.arkx.framework.commons.util.StringUtil;

/**
 * @author Darkness
 * @date 2013-10-11 下午04:52:55
 * @version V1.0
 */
public abstract class ListExcelReader extends AbstractExcelReader {

	public ListExcelReader(String path) {
		super(path);
	}

	public Map<String, List<String[]>> getAllData() {
		Map<String, List<String[]>> result = new LinkedHashMap<>();

		int sheetCount = wb.getNumberOfSheets();
		for (int i = 0; i < sheetCount; i++) {
			// System.out.println("deal with sheet:" + i);
			TwoTuple<String, List<String[]>> sheetInfo = getAllData(i);
			result.put(sheetInfo.first, sheetInfo.second);
		}

		return result;
	}

	/**
	 * 取Excel所有数据，包含header
	 * @return List<String[]>
	 */
	public TwoTuple<String, List<String[]>> getAllData(int sheetIndex) {
		List<String[]> result = new ArrayList<>(100);

		int columnNum = getColumnCount(sheetIndex);
		if (columnNum <= 0) {
			return new TwoTuple<>("", result);
		}

		Sheet sheet = wb.getSheetAt(sheetIndex);
		int i = 0;
		for (Row row : sheet) {
			boolean isEmptyRow = true;
			String[] singleRow = new String[columnNum];
			for (int j = 0; j < columnNum; j++) {
				Cell cell = row.getCell(j, MissingCellPolicy.CREATE_NULL_AS_BLANK);
				singleRow[j] = getCellValue(sheet, i, cell);

				if (!StringUtil.isEmpty(singleRow[j])) {
					isEmptyRow = false;
				}
			}
			i++;
			if (isEmptyRow) {// 如果为空，跳过
				continue;
			}
			result.add(singleRow);
		}

		return new TwoTuple<>(sheet.getSheetName(), result);
	}

	/**
	 * 获取某一行数据
	 * @param rowIndex 计数从0开始，rowIndex为0代表header行
	 * @return
	 */
	private String[] getRowData(int sheetIndex, int rowIndex) {
		List<String[]> result = new ArrayList<String[]>(100);

		if (rowIndex > this.getColumnNum(sheetIndex)) {
			return new String[0];
		}
		return result.get(rowIndex);
	}

	/**
	 * 获取某一列数据
	 * @param colIndex
	 * @return
	 */
	private String[] getColumnData(int sheetIndex, int colIndex) {
		if (colIndex > this.getColumnNum(sheetIndex)) {
			return new String[0];
		}
		List<String[]> result = new ArrayList<String[]>(100);

		String[] dataArray = null;
		if (result != null && result.size() > 0) {
			dataArray = new String[this.getRowNum(sheetIndex) + 1];
			int index = 0;
			for (String[] rowData : result) {
				if (rowData != null) {
					dataArray[index] = rowData[colIndex];
					index++;
				}
			}
		}
		return dataArray;
	}

}
