package com.arkxos.framework.data.lightning;

import java.io.IOException;

import com.arkxos.framework.data.fasttable.BufferReader;

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
