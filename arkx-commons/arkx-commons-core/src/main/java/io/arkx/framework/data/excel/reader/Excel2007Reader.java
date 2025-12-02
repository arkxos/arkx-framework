package io.arkx.framework.data.excel.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.arkx.framework.data.excel.ExcelReader;
import io.arkx.framework.data.excel.factory.Excel2007Factory;

import com.google.common.base.Joiner;

public class Excel2007Reader extends ExcelReader {

	public Excel2007Reader() {
		super();
	}

	public Excel2007Reader(File file) {
		super(file);
	}

	public Excel2007Reader(InputStream is) {
		this.fis = is;
	}

	public Excel2007Reader(Workbook workbook) {
		this.wb = (XSSFWorkbook) workbook;
	}

	@Override
	public void open() throws IOException {
		if (wb != null) {
			wb = (XSSFWorkbook) wb;
		}
		else {
			if (fis == null)
				fis = new FileInputStream(file);
			try {
				// 可以直接传流参数，但是推荐使用OPCPackage容器打开
				wb = new XSSFWorkbook(OPCPackage.open(fis));
			}
			catch (InvalidFormatException e) {
				e.printStackTrace();
			}
			fis.close();
		}
	}

	public static void main(String args[]) {
		File file = new File("C:\\Users\\Administrator\\Desktop\\test.xlsx");
		ExcelReader readExcel = new Excel2007Factory().createExcelReader(file);
		try {
			readExcel.open();
		}
		catch (IOException e) {
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
