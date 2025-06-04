package com.arkxos.framework.framework.common.collection;

import static com.arkxos.framework.commons.collection.DataColumn.dateColumn;
import static com.arkxos.framework.commons.collection.DataColumn.doubleColumn;
import static com.arkxos.framework.commons.collection.DataColumn.floatColumn;
import static com.arkxos.framework.commons.collection.DataColumn.intColumn;
import static com.arkxos.framework.commons.collection.DataColumn.stringColumn;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.arkxos.framework.commons.collection.DataColumn;
import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.collection.DataTableUtil;
import com.arkxos.framework.framework.common.Person;

/**
 * 
 * @author Darkness
 * @date 2012-3-22 下午9:17:19
 * @version V1.0
 */
public class DataTableTest {
	
	@Test
	public void create() {
		DataColumn dcUserName = dateColumn("year");
		DataColumn dcRealName = stringColumn("name", 10);
		DataColumn dcStatusName = intColumn("sex");
		DataColumn dcBranchInnercodeName = floatColumn("money");
		DataColumn dcRoleNames = doubleColumn("price");

		DataTable dt = new DataTable(new DataColumn[] { dcUserName, dcRealName, dcStatusName, dcBranchInnercodeName, dcRoleNames });
		dt.insertRow("2015-08-01", "暗之幻影1", "1", "52.28", "52.2882");
		dt.insertRow("2015-08-08", "暗之幻影2", "1", "52.289", "52.28");

		assertEquals(dt.getRowCount(), 2);
		assertEquals(dt.getColumnCount(), 5);
		
		LocalDate year = dt.getLocalDate(1, "year");
		assertEquals(LocalDate.parse("2015-08-08"), year);
	}
	
	@Test
	public void testInsertRow() {
	    DataColumn dc1 = stringColumn("C1", 10);
	    DataColumn dc2 = stringColumn("C2", 10);
	    DataTable dt = new DataTable(new DataColumn[] { dc1, dc2 }, null);
	    dt.insertRow("1", "1");
	    dt.insertRow("3", "3");
	    dt.insertRow("4", "4");
	    dt.insertRow("5", "5");

	    dt.insertRow(new Object[] { "2", "2" }, 1);
	    dt.insertRow(new Object[] { "2", "2" }, 4);

	    assertEquals(6, dt.getRowCount());
	    assertEquals("2", dt.get(1, 0));
	    assertEquals("2", dt.get(4, 0));
	    assertEquals("5", dt.get(5, 0));
	}

	/**
	 * List 转换成 DataTable
	 * 
	 * @author Darkness
	 * @date 2012-11-26 下午03:53:11 
	 * @version V1.0
	 */
	@Test
	public void entitiesToDataTable() {
		
		List<Person> persons = new ArrayList<Person>();
		
		for (int i = 0; i < 10; i++) {
			Person person = new Person();
			person.setId(i+"");
			person.setAge(i);
			person.setName("darkness" + i);
			person.setSex("man");
			
			persons.add(person);
		}
		
		DataTable dataTable = DataTableUtil.toDataTable(persons);
		
		assertEquals(dataTable.getRowCount(), 10);
		assertEquals(dataTable.getColumnCount(), 6);
	}
}
