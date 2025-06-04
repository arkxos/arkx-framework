package io.arkx.framework.data.excel.eventmodle;

public class ExcelReaderUtil {
	
	//excel2003扩展名
	public static final String EXCEL03_EXTENSION = ".xls";
	//excel2007扩展名
	public static final String EXCEL07_EXTENSION = ".xlsx";
	
	/**
	 * 读取Excel文件，可能是03也可能是07版本
	 * @param excel03
	 * @param excel07
	 * @param fileName
	 * @throws Exception 
	 */
	public static void readExcel(IRowReader reader,String fileName) throws Exception{
		if (fileName.endsWith(EXCEL03_EXTENSION)){
			Excel2003Reader excel03 = new Excel2003Reader();
			excel03.setRowReader(reader);
			excel03.process(fileName);
		} else if (fileName.endsWith(EXCEL07_EXTENSION)){
			Excel2007Reader excel07 = new Excel2007Reader();
			excel07.setRowReader(reader);
			excel07.process(fileName);
		} else {
			throw new  Exception("文件格式错误，fileName的扩展名只能是xls或xlsx。");
		}
	}
	
	/**
	 * 只读取一个Sheet
	 * @param reader
	 * @param fileName
	 * @param sheetIndex
	 * @throws Exception
	 */
	public static void readOneSheet(IRowReader reader,String fileName, int sheetIndex)throws Exception{
		if (fileName.endsWith(EXCEL03_EXTENSION)){
			Excel2003Reader excel03 = new Excel2003Reader();
			excel03.setRowReader(reader);
			excel03.processOneSheet(fileName, 1);
		} else if (fileName.endsWith(EXCEL07_EXTENSION)){
			Excel2007Reader excel07 = new Excel2007Reader();
			excel07.processOneSheet(fileName, 1);
		} else {
			throw new  Exception("文件格式错误，fileName的扩展名只能是xls或xlsx。");
		}
	}
}