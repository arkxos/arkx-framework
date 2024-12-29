package com.arkxos.framework.data.oldfastdb;

import java.io.IOException;

import com.arkxos.framework.data.fasttable.BufferReader;
import com.arkxos.framework.data.fasttable.FastTableHeader;

public class RecordFilter extends BufferReader {

	public boolean filter(RecordUnit recordUnit, FastTableHeader tableInfo, FilterInfo filterInfo) throws IOException {
		if (filterInfo == null) {
			return true;
		}

		if (!filterInfo.isNeedFilter) {
			return true;
		}
		
		return recordUnit.isMatch(tableInfo, filterInfo);
		
	}

}
