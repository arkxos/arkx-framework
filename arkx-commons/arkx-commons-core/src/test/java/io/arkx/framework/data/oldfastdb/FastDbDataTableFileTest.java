package io.arkx.framework.data.oldfastdb;

import static io.arkx.framework.commons.collection.DataColumn.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

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
public class FastDbDataTableFileTest {

    Fastdb fastdb = new Fastdb();

    private String TEST_TABLE_NAME = "testPerson";

    private FastDataTable preparedDataTable() {
        DataColumn dcDate = dateIndexColumn("date");
        DataColumn dcString = stringColumn("string", 20);
        DataColumn dcFixedString = fixedIndexStringColumn("fixedstring", 6);
        DataColumn dcInt = intColumn("int");
        DataColumn dcFloat = floatColumn("float");
        DataColumn dcDouble = doubleColumn("double");
        DataColumn dcLong = longColumn("long");

        FastDataTable dt = new FastDataTable(TEST_TABLE_NAME,
                new DataColumn[]{dcDate, dcString, dcFixedString, dcInt, dcFloat, dcDouble, dcLong});

        return dt;
    }

    // @Test
    public void create() {
        fastdb.dropTable(TEST_TABLE_NAME);

        TimeWatch timeWatch = new TimeWatch();

        FastDataTable dt = preparedDataTable();
        timeWatch.startWithTaskName("prepared datatable data");
        LocalDate now = LocalDate.now();
        // int initRowCount = 100*10000;
        // for (int i = 0; i < initRowCount; i++) {
        // LocalDate date = now.minusDays(-i);
        // String regionCode = (100000 + i%3000)+"";
        // dt.insertRow(date, "暗之幻影"+i, regionCode, i, "52.28", "52.2882", "888888888");
        // }
        int yearCount = 1;
        int symbol = 100;
        int symbolCount = 300;
        for (int i = 0; i < symbolCount; i++) {
            // LocalDate now = LocalDate.now();
            String symbolCode = (symbol + i) + "";
            int initRowCount = 365 * yearCount;// 1年
            for (int day = 0; day < initRowCount; day++) {
                LocalDate date = now.minusDays(day);
                // quoteCache.put(symbolCode, date, day);
                dt.insertRow(date, "暗之幻影" + i, symbolCode, i, "52.28", "52.2882", "888888888");
            }
        }

        timeWatch.stopAndPrint();

        timeWatch.startWithTaskName("insert");
        fastdb.insert(dt);

        FastDataTable dt2 = preparedDataTable();
        for (int i = 0; i < 1; i++) {
            dt2.insertRow("2020-08-02", "暗之幻影", "123456", "1", "52.28", "52.2882", "888888888");
        }

        fastdb.insert(dt2);
        timeWatch.stopAndPrint();

        timeWatch.startWithTaskName("read all");
        FastDataTable dtFromFile = fastdb.load(TEST_TABLE_NAME, FastDataTable.class);
        // assertEquals(initRowCount+1, dtFromFile.getRowCount());
        timeWatch.stopAndPrint();

        // System.out.println(dtFromFile.getDataRow(10*10000-1));
        // System.out.println(dtFromFile.getDataRow(10*10000));

        timeWatch.startWithTaskName("select one");
        DataTable filterDtFromFile = fastdb.select(TEST_TABLE_NAME, "date='2020-08-02'");

        assertEquals(1, filterDtFromFile.getRowCount());
        timeWatch.stopAndPrint();

        timeWatch.startWithTaskName("select by date");
        filterDtFromFile = fastdb.select(TEST_TABLE_NAME, "date='" + LocalDate.now() + "'");

        assertEquals(300, filterDtFromFile.getRowCount());
        timeWatch.stopAndPrint();

        System.out.println(filterDtFromFile.getDataRow(0));

        timeWatch.startWithTaskName("select by symbol");
        filterDtFromFile = fastdb.select(TEST_TABLE_NAME, "fixedstring='001'");

        assertEquals(365 * yearCount * 0, filterDtFromFile.getRowCount());
        timeWatch.stopAndPrint();

        // System.out.println(filterDtFromFile.getDataRow(0));
    }

    // @Test
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
