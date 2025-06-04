package com.arkxos.framework.data.excel.factory;

import java.io.File;
import java.io.InputStream;

import com.arkxos.framework.data.excel.ExcelReader;
import com.arkxos.framework.data.excel.ExcelWriter;
import com.arkxos.framework.data.excel.IExcelFactory;
import com.arkxos.framework.data.excel.reader.Excel2003Reader;
import com.arkxos.framework.data.excel.writer.Excel2003Writer;

public class Excel2003Facotry implements IExcelFactory {

	@Override
	public ExcelReader createExcelReader() {
		return new Excel2003Reader();
	}

	@Override
	public ExcelReader createExcelReader(File file) {
		return new Excel2003Reader(file);
	}

	@Override
	public ExcelReader createExcelReader(InputStream is) {
		return new Excel2003Reader(is);
	}

	@Override
	public ExcelWriter createExcelWriter(){
		return new Excel2003Writer();
	}

}