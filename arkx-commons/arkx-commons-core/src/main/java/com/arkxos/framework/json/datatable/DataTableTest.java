package com.arkxos.framework.json.datatable;

import com.alibaba.fastjson.JSON;
import io.arkx.framework.commons.collection.DataTable;
import com.arkxos.framework.json.convert.DataTableConvertor;

/**  
 * 
 * @author darkness  
 * @date 2018-10-09 22:50:08
 * @version 1.0  
 * @since 4.0
 */
public class DataTableTest {

	public static void main(String[] args) {
		DataTableFastjsonConfig.init();
		
		DataTable dataTable = new DataTable("id", "name");
		dataTable.insertRow("id1", "ark1");
		dataTable.insertRow("id2", "ark2");
		System.out.println(JSON.toJSONString(dataTable));
		dataTable = JSON.parseObject(JSON.toJSONString(dataTable), DataTable.class);
		
		System.out.println(new DataTableConvertor().toJSON(dataTable));
	}
	
}
