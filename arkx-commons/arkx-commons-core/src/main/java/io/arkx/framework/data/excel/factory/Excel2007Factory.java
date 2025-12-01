package io.arkx.framework.data.excel.factory;

import io.arkx.framework.data.excel.ExcelReader;
import io.arkx.framework.data.excel.ExcelWriter;
import io.arkx.framework.data.excel.IExcelFactory;
import io.arkx.framework.data.excel.reader.Excel2007Reader;
import io.arkx.framework.data.excel.writer.Excel2007Writer;

import java.io.File;
import java.io.InputStream;

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