package com.rapidark.framework.json.datatable;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.rapidark.framework.commons.collection.DataTable;

/**  
 * 
 * @author darkness  
 * @date 2018-10-09 22:43:52
 * @version 1.0  
 * @since 4.0
 */
public class DataTableFastjsonConfig {

	public static void init(){
        SerializeConfig.getGlobalInstance().put(DataTable.class, new DataTableSerializer());
        
        ParserConfig.getGlobalInstance().putDeserializer(DataTable.class, new DataTableDeserializer());
    }
	
}
