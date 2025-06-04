package com.arkxos.framework.data.excel.factory;

import java.io.File;
import java.io.InputStream;

import com.arkxos.framework.data.excel.ExcelReader;
import com.arkxos.framework.data.excel.ExcelWriter;
import com.arkxos.framework.data.excel.IExcelFactory;
import com.arkxos.framework.data.excel.reader.Excel2007Reader;
import com.arkxos.framework.data.excel.writer.Excel2007Writer;

public class Excel2007Factory implements IExcelFactory {

	@Override
	public ExcelReader createExcelReader(File file) {
		return new Excel2007Reader(file);
	}

	@Override
	public ExcelReader createExcelReader() {
		return new Excel2007Reader();
	}

	@Override
	public ExcelReader createExcelReader(InputStream is) {
		return new Excel2007Reader(is);
	}

	@Override
	public ExcelWriter createExcelWriter() {
		return new Excel2007Writer();
	}

}