package io.arkx.framework.data.excel;

import java.io.File;
import java.io.InputStream;

public interface IExcelFactory {

	ExcelReader createExcelReader();

	ExcelReader createExcelReader(File file);

	ExcelReader createExcelReader(InputStream is);

	ExcelWriter createExcelWriter();
}