package io.arkx.framework.data.lightning;

import io.arkx.framework.data.fasttable.BufferReader;

import java.io.IOException;

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
