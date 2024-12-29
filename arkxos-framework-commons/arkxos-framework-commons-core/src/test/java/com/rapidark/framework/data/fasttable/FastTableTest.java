package com.rapidark.framework.data.fasttable;

import java.io.File;

import com.arkxos.framework.commons.util.SystemInfo;
import com.arkxos.framework.commons.util.UuidUtil;
import com.arkxos.framework.data.fasttable.FastColumn;
import com.arkxos.framework.data.fasttable.FastTable;
import com.arkxos.framework.data.fasttable.FastTableBuilder;
import com.arkxos.framework.data.fasttable.FastTableHeader;

/**
 * @author Darkness
 * @date 2017年7月13日 下午4:08:48
 * @version 1.0
 * @since 1.0
 */
public class FastTableTest {

	public static void main(String[] args) {
		String TEST_TABLE_NAME = "testPerson";

		FastTableHeader header = new FastTableHeader();
		header.setTableName(TEST_TABLE_NAME);

		header.addColumn(FastColumn.dateColumn("date"));
		header.addColumn(FastColumn.stringColumn("string", 20));
		header.addColumn(FastColumn.fixedStringColumn("fixedstring", 6));
		header.addColumn(FastColumn.intColumn("int"));
		header.addColumn(FastColumn.floatColumn("float"));
		header.addColumn(FastColumn.doubleColumn("double"));
		header.addColumn(FastColumn.longColumn("long"));

		header.setRowSize(0);

		// create
		FastTable fastTable = new FastTableBuilder()
				.tableName(TEST_TABLE_NAME)
				.addDateColumn("date")
				.addStringColumn("string", 20)
				.addFixedStringColumn("fixedstring", 6)
				.addIntColumn("int")
				.addFloatColumn("float")
				.addDoubleColumn("double")
				.addLongColumn("long")
				.create(SystemInfo.userDir() + File.separator + "temp" + File.separator + UuidUtil.base58Uuid() + ".aft");
		
		
		FastTable fastTable2 = FastTable.load(SystemInfo.userDir() + File.separator + "temp" + File.separator + UuidUtil.base58Uuid() + ".aft");
	}

}
