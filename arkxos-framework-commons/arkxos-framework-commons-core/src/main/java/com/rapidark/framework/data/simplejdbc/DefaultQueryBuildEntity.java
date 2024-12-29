package com.rapidark.framework.data.simplejdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**   
 * 
 * @author Darkness
 * @date 2014-4-26 上午10:59:50 
 * @version V1.0   
 */
public class DefaultQueryBuildEntity implements IQueryBuildEntity<Object[]> {

	@Override
	public List<Object[]> buildEntity(ResultSet resultSet, int columnCount) {
		List<Object[]> datas = new ArrayList<Object[]>();
		
		try {
			while (resultSet.next()) {
				
				Object[] row = new Object[columnCount];

				for (int i = 0; i < columnCount; i++) {
					row[i] = resultSet.getObject(i + 1);
				}

				datas.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return datas;
	}

}
