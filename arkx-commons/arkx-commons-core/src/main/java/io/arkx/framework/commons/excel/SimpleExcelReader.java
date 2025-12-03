package io.arkx.framework.commons.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author Darkness
 * @date 2013-10-11 下午04:12:22
 * @version V1.0
 */
public class SimpleExcelReader extends ListExcelReader {

    public SimpleExcelReader(String path) {
        super(path);
    }

    @Override
    protected String getCellValue(Sheet sheet, int rowIndex, Cell cell) {
        switch (cell.getCellType()) {
            case BLANK :
                return "";
            case BOOLEAN :
                return Boolean.toString(cell.getBooleanCellValue());
            // 数值
            case NUMERIC :
                if (DateUtil.isCellDateFormatted(cell)) {
                    return String.valueOf(cell.getDateCellValue());
                }

                cell.setCellType(CellType.STRING);
                String temp = cell.getStringCellValue();
                // 判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串
                if (temp.indexOf(".") > -1) {
                    return String.valueOf(Double.valueOf(temp)).trim();
                } else {
                    return temp.trim();
                }
            case STRING :
                return cell.getStringCellValue().trim();
            case ERROR :
                return "";
            case FORMULA :
                cell.setCellType(CellType.STRING);
                String value = cell.getStringCellValue();
                if (value != null) {
                    value = value.replaceAll("#N/A", "").trim();
                }
                return value;
            default :
                return "";
        }
    }

}
