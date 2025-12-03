package io.arkx.framework.data.oldfastdb;

import java.io.IOException;

import io.arkx.framework.data.fasttable.BufferReader;
import io.arkx.framework.data.fasttable.FastTableHeader;

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
