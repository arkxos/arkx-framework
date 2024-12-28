package com.rapidark.framework.data.excel.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.common.base.Joiner;
import com.rapidark.framework.data.excel.ExcelReader;
import com.rapidark.framework.data.excel.factory.Excel2003Facotry;

public class Excel2003Reader extends ExcelReader {

	public Excel2003Reader() {
		super();
	}
    
	public Excel2003Reader(File file) {
		super(file);
	}
    
	public Excel2003Reader(InputStream is) {
		this.fis = is;
	}
    
	public Excel2003Reader(Workbook workbook) {
		this.wb = (HSSFWorkbook) workbook;
	}
    
	@Override
	public void open() throws IOException {
		if (wb != null) {
			wb = (HSSFWorkbook) wb;
		} else {
			if (fis == null) {
				fis = new FileInputStream(file);
			}
			wb = new HSSFWorkbook(new POIFSFileSystem(fis));
			fis.close();
		}
	}

	public static void main(String args[]) {
		File file = new File("C:\\Users\\Administrator\\Desktop\\test.xls");
		ExcelReader readExcel = new Excel2003Facotry().createExcelReader(file);
		try {
			readExcel.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		readExcel.setSheetNum(0); // 设置读取索引为0的工作表
		// 总行数
		int count = readExcel.getRowCount();
		for (int i = 1; i <= count; i++) {
			String[] rows = readExcel.readExcelLine(i);
			System.out.println("[" + i + "]" + Joiner.on("  ").join(rows));
		}
	}

}