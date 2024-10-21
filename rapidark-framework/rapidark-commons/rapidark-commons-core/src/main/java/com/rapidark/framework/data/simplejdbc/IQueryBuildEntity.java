package com.rapidark.framework.data.simplejdbc;

import java.sql.ResultSet;
import java.util.List;

/**   
 * 
 * @author Darkness
 * @date 2014-4-26 上午10:52:41 
 * @version V1.0   
 */
public interface IQueryBuildEntity<T> {

	List<T> buildEntity(ResultSet resultSet, int columnCount);

}
