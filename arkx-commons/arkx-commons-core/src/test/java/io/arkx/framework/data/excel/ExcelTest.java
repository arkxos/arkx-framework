package io.arkx.framework.data.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class ExcelTest {

	public static void readExcel() throws InvalidFormatException, IOException {
		String filePath = "C:\\Users\\darkness\\Desktop\\test-device.xlsx";

		ExcelReader readExcel = ExcelUtils.createCommonExcelReaderByFileName(filePath);

		try {
			readExcel.open();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		List<String[]> datas = readExcel.readExcelDataList();

		for (String[] strings : datas) {
			System.out.println(Arrays.toString(strings));
		}
	}

	public static void write() throws Exception {
		List<List<String>> tableDataList = new ArrayList<>();
		List<String> row = new ArrayList<>();
		row.add("姓名");
		row.add("学校");
		tableDataList.add(row);

		row = new ArrayList<>();
		row.add("姓名");
		row.add("学校");
		tableDataList.add(row);
		row = new ArrayList<>();
		row.add("姓名");
		row.add("学校");
		tableDataList.add(row);
		FileOutputStream outputStream = new FileOutputStream("e:\\test.xlsx");
		ExcelUtils.writeExcel(ExcelUtils.EXCEL2007, "测试文件", tableDataList, outputStream);
	}

	public static void main(String[] args) throws Exception {
		readExcel();
	}

}
