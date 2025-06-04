package io.arkx.framework.commons.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**   
 * 合并行读取空值
 * @author Darkness
 * @date 2013-10-11 下午03:51:42 
 * @version V1.0   
 */
public abstract class AbstractExcelReader {
	
	Workbook wb = null;

	public AbstractExcelReader(String path) {
		this.initWorkbook(path);
	}
	
	/**
	 * 初始化workbook
	 * 
	 * @author Darkness
	 * @date 2013-10-11 下午04:01:57 
	 * @version V1.0
	 */
	private void initWorkbook(String path) {
		try {
			InputStream inp = new FileInputStream(path);
			wb = WorkbookFactory.create(inp);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取sheet的列数
	 * 
	 * @author Darkness
	 * @date 2013-10-11 下午04:03:39 
	 * @version V1.0
	 */
	protected int getColumnCount(int sheetIndex) {
		int columnNum = 0;
		Sheet sheet = wb.getSheetAt(sheetIndex);
		if (sheet.getRow(0) != null) {
			columnNum = sheet.getRow(0).getLastCellNum() - sheet.getRow(0).getFirstCellNum();
		}
		return columnNum;
	}
	
	protected abstract String getCellValue(Sheet sheet, int rowIndex, Cell cell);
	
	public abstract Object getAllData(int sheetIndex) ;
	

	/**
	 * 返回Excel最大行index值，实际行数要加1
	 * 
	 * @return
	 */
	public int getRowNum(int sheetIndex) {
		Sheet sheet = wb.getSheetAt(sheetIndex);
		return sheet.getLastRowNum();
	}

	/**
	 * 返回数据的列数
	 * 
	 * @return
	 */
	public int getColumnNum(int sheetIndex) {
		Sheet sheet = wb.getSheetAt(sheetIndex);
		Row row = sheet.getRow(0);
		if (row != null && row.getLastCellNum() > 0) {
			return row.getLastCellNum();
		}
		return 0;
	}

	
}
