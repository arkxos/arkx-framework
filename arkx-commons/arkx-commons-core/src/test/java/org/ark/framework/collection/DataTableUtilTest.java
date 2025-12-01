package org.ark.framework.collection;

import com.alibaba.fastjson.JSON;
import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.DataTableUtil;
import io.arkx.framework.commons.collection.DataTypes;
import org.ark.common.Person;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 
 * @author Darkness
 * @date 2012-3-22 下午9:17:19
 * @version V1.0
 */
public class DataTableUtilTest {
	
	@Test
	public void testInsertRow() {
	    DataColumn dc1 = new DataColumn("C1", DataTypes.STRING);
	    DataColumn dc2 = new DataColumn("C2", DataTypes.STRING);
	    DataTable dt = new DataTable(new DataColumn[] { dc1, dc2 }, null);
	    dt.insertRow(new Object[] { "1", "1" });
	    dt.insertRow(new Object[] { "3", "3" });
	    dt.insertRow(new Object[] { "4", "4" });
	    dt.insertRow(new Object[] { "5", "5" });

	    dt.insertRow(new Object[] { "2", "2" }, 1);
	    dt.insertRow(new Object[] { "2", "2" }, 4);

	    assertEquals(6, dt.getRowCount());
	    assertEquals("2", dt.get(1, 0));
	    assertEquals("2", dt.get(4, 0));
	    assertEquals("5", dt.get(5, 0));
	}

	public static void main(String[] args) {
		DataColumn dcUserName = new DataColumn("UserName", DataTypes.STRING);
		DataColumn dcRealName = new DataColumn("RealName", DataTypes.STRING);
		DataColumn dcStatusName = new DataColumn("StatusName", DataTypes.STRING);
		DataColumn dcBranchInnercodeName = new DataColumn("BranchInnercodeName", DataTypes.STRING);
		DataColumn dcRoleNames = new DataColumn("RoleNames", DataTypes.STRING);

		DataTable dt = new DataTable(new DataColumn[] { dcUserName, dcRealName, dcStatusName, dcBranchInnercodeName, dcRoleNames });
		dt.insertRow(new Object[] { "darkness1", "暗之幻影1", "1", "008", "admin" });
		dt.insertRow(new Object[] { "darkness2", "暗之幻影2", "1", "008", "admin" });

		System.out.println(dt);
		
		List<Map<String, Object>> mapData = DataTableUtil.dataTableToList(dt);
		System.out.println(JSON.toJSONString(mapData));
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
