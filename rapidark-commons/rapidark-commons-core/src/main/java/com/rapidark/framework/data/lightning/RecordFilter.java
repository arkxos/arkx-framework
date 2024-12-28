package com.rapidark.framework.data.lightning;

import java.io.IOException;

import com.rapidark.framework.data.fasttable.BufferReader;

public class RecordFilter extends BufferReader {

	public boolean filter(RecordUnit recordUnit, TableInfo tableInfo, FilterInfo filterInfo) throws IOException {
		if (filterInfo == null) {
			return true;
		}

		if (!filterInfo.isNeedFilter) {
			return true;
		}
		
		return recordUnit.isMatch(tableInfo, filterInfo);
		
	}

}
