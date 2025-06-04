package com.arkxos.framework.data.excel;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel导出
 */
public abstract class ExcelWriter {

	/**
	 * 向workbook中写入数据
	 * 
	 * @param workbook
	 * @param sheetTitle
	 * @param tableDataList
	 * @param headerNum		headerNum大于零，则包含表头，前headerNum行为标题行，其余的行为数据行。
	 * 						否则不包含表头信息           
	 * @return
	 */
	public abstract Workbook writeWorkbook(Workbook workbook, String sheetTitle, List<List<String>> tableDataList,
			int headerNum);
	/**
	 * 向workbook中写入多数据源的数据（多个sheet的数据来源不同）
	 * 
	 * @param workbook
	 * @param mutilSheetMap   key为sheet名称sheetTitle， value为表体数据tableDataList
	 * @param headerNum		headerNum大于零，则包含表头，前headerNum行为标题行，其余的行为数据行。
	 * 						否则不包含表头信息           
	 * @return
	 */
	public abstract Workbook writeWorkbookMutilDatasoure(Workbook workbook, Map<String, List<List<String>>> mutilSheetMap,int headerNum);

	/**
	 * 创建一个单元格
	 * 
	 * @param row
	 * @param colIndex
	 * @param content
	 * @return
	 */
	public abstract Cell createCell(Row row, int colIndex, String content);

	/**
	 * 创建一个单元格并设置单元格样式
	 * 
	 * @param row
	 * @param colIndex
	 * @param content
	 * @param titleStyle
	 * @return
	 */
	public Cell createCell(Row row, int colIndex, String content, CellStyle cellStyle) {
		Cell result = createCell(row, colIndex, content);
		if (cellStyle != null)
			result.setCellStyle(cellStyle);
		return result;
	}

	/**
	 * 创建单元格的样式
	 * 
	 * @param workbook
	 * @return
	 */
	public abstract CellStyle createCellStyle(Workbook workbook, Font font,
			HorizontalAlignment alignment, VerticalAlignment verticalAlignment, BorderStyle Border,short foregroundColor,FillPatternType fillPattern) ;
	/**
	 * 创建字体
	 * @param workbook
	 * @return
	 */
	public Font createFont(Workbook workbook){
		  Font font = workbook.createFont();
          return font;
	}
	
    /**
     * 创建默认表头标题单元格的样式（蓝色）
     * 
     * @param workbook
     * @return
     */
    public static CellStyle createDefHeaderCellStyle(Workbook workbook) {
        //设置表头标题样式
        Font headerFont = workbook.createFont();
                 headerFont.setFontHeightInPoints((short) 14);
//                 headerFont.setBoldweight(0x190);//HSSFFont.BOLDWEIGHT_BOLD);
        CellStyle result = workbook.createCellStyle();
                      result.setFont(headerFont);
                      result.setAlignment(HorizontalAlignment.CENTER);
                      result.setVerticalAlignment(VerticalAlignment.CENTER);
                      result.setFillForegroundColor(HSSFColorPredefined.PALE_BLUE.getIndex());//
                      result.setBorderRight(BorderStyle.THIN);
                      result.setFillPattern(FillPatternType.SOLID_FOREGROUND);                     
        return result;
    }


	/**
	 * 根据创建好的workbook对象，向响应端输出EXCEL
	 * 
	 * @param workbook
	 * @param exportFileName
	 * @param response
	 * @throws Exception
	 */
	public void exportExcelData(Workbook workbook, OutputStream outputStream)
			throws Exception {
		// 输出
		try {
			workbook.write(outputStream);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	/**
	 * 导出excel对外接口
	 * 
	 * @param exportFileName
	 *            导出后的文件名 如：XXX.xls或者XXX.xlsx
	 * @param title
	 *            标题 如：比对碰撞结果
	 * @param tableDataList
	 *            数据表数据 如：new String[][]{{"表头第一列","表头第二列"},{"aaa","bbb"}}
	 * @param response
	 * @throws Exception
	 */
	public void exportExcelData(String sheetTitle, List<List<String>> tableDataList, OutputStream outputStream)
			throws Exception {
		Workbook workbook = writeWorkbook(null, sheetTitle, tableDataList, 0);
		exportExcelData(workbook, outputStream);
	}
	
	public void exportExcelData(String sheetTitle, int headerNum, List<List<String>> tableDataList, OutputStream outputStream)
			throws Exception {
		Workbook workbook = writeWorkbook(null, sheetTitle, tableDataList, headerNum);
		exportExcelData(workbook, outputStream);
	}
	
}