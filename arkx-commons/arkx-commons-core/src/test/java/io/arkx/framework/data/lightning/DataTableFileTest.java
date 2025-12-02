package io.arkx.framework.data.lightning;

import static io.arkx.framework.commons.collection.DataColumn.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.util.TimeWatch;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * @author Darkness
 * @date 2015年11月8日 下午4:59:10
 * @version V1.0
 * @since infinity 1.0
 */
public class DataTableFileTest {

	private String TEST_TABLE_NAME = "testPerson";

	private LightningDataTable preparedDataTable() {
		DataColumn dcDate = dateColumn("date");
		DataColumn dcString = stringColumn("string", 20);
		DataColumn dcFixedString = fixedStringColumn("fixedstring", 6);
		DataColumn dcInt = intColumn("int");
		DataColumn dcFloat = floatColumn("float");
		DataColumn dcDouble = doubleColumn("double");
		DataColumn dcLong = longColumn("long");

		LightningDataTable dt = new LightningDataTable(TEST_TABLE_NAME,
				new DataColumn[] { dcDate, dcString, dcFixedString, dcInt, dcFloat, dcDouble, dcLong });

		return dt;
	}

	@Test
	public void createTableFileHeader() {
		LightningDataTable dt = preparedDataTable();
		String tableName = dt.getTableName();
		int rowCount = dt.getRowCount();
		LightningColumn[] columns = dt.getLightningColumns();
		ByteBuffer headerBuffer = LightningTableFile.buildTableInfoByteBuffer(tableName, columns, rowCount);
		int headerLength = headerBuffer.getInt();
		assertEquals(111, headerLength);
		TableInfo header = new TableInfoReader().buildTableInfo(headerBuffer);

		assertEquals(TEST_TABLE_NAME, header.tableName);
		assertEquals(0, header.rowSize);
		assertEquals(7, header.columnSize());
	}

	@Test
	public void create() {
		LightningDatabase.dropTable(TEST_TABLE_NAME);

		TimeWatch timeWatch = new TimeWatch();

		LightningDataTable dt = preparedDataTable();
		timeWatch.startWithTaskName("prepared datatable data");
		LocalDate now = LocalDate.now();
		int initRowCount = 100 * 100;
		for (int i = 0; i < initRowCount; i++) {
			LocalDate date = now.minusDays(-i);
			String regionCode = (100 + i % 3000) + "";
			dt.insertRow(date, "暗之幻影" + i, regionCode, i, "52.28", "52.2882", "888888888");
		}
		timeWatch.stopAndPrint();

		timeWatch.startWithTaskName("insert");
		LightningDatabase.insert(dt);

		LightningDataTable dt2 = preparedDataTable();
		for (int i = 0; i < 1; i++) {
			dt2.insertRow("2015-08-02", "暗之幻影", "123456", "1", "52.28", "52.2882", "888888888");
		}

		LightningDatabase.insert(dt2);
		timeWatch.stopAndPrint();

		timeWatch.startWithTaskName("read all");
		LightningDataTable dtFromFile = LightningDatabase.load(TEST_TABLE_NAME, LightningDataTable.class);
		assertEquals(initRowCount + 1, dtFromFile.getRowCount());
		timeWatch.stopAndPrint();

		// System.out.println(dtFromFile.getDataRow(10*10000-1));
		// System.out.println(dtFromFile.getDataRow(10*10000));

		timeWatch.startWithTaskName("select one");
		DataTable filterDtFromFile = LightningDatabase.select(TEST_TABLE_NAME, "date='2015-08-02'");

		assertEquals(1, filterDtFromFile.getRowCount());
		timeWatch.stopAndPrint();

		System.out.println(filterDtFromFile);
	}

	@Test
	public void testMap() {
		TimeWatch timeWatch = new TimeWatch();
		timeWatch.startWithTaskName("build index");
		Table<String, LocalDate, Integer> quoteCache = HashBasedTable.create();
		int symbol = 100;
		int symbolCount = 300;
		for (int i = 0; i < symbolCount; i++) {
			LocalDate now = LocalDate.now();
			String symbolCode = (symbol + i) + "";
			int initRowCount = 365 / 7 * 5 * 10;// 10年
			for (int day = 0; day < initRowCount; day++) {
				LocalDate date = now.minusDays(day);
				quoteCache.put(symbolCode, date, day);
			}
		}
		System.out.println(quoteCache.size());
		timeWatch.stopAndPrint();

		timeWatch.startWithTaskName("query single");
		quoteCache.get("100001", LocalDate.now().minusDays(100));
		System.out.println(quoteCache.row("100290").size());
		System.out.println(quoteCache.column(LocalDate.now().minusDays(100)).size());
		timeWatch.stopAndPrint();
	}

}
