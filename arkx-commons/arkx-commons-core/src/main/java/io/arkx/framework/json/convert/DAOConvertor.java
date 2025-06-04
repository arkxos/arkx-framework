package io.arkx.framework.json.convert;

import io.arkx.framework.data.db.orm.DAO;
import io.arkx.framework.data.db.orm.DAOColumn;
import io.arkx.framework.json.JSONObject;

/**
 * DAO的JSON转换器
 * 
 */
public class DAOConvertor implements IJSONConvertor {

	@Override
	public String getExtendItemID() {
		return "DAO";
	}

	@Override
	public String getExtendItemName() {
		return getExtendItemID();
	}

	@Override
	public boolean match(Object obj) {
		return obj instanceof DAO;
	}

	@Override
	public JSONObject toJSON(Object obj) {
		JSONObject jo = new JSONObject();
		DAO<?> dao = (DAO<?>) obj;
		for (DAOColumn dc : dao.columns()) {
			jo.put(dc.getColumnName(), dao.getV(dc.getColumnName()));
		}
		jo.put("_DAOClass", obj.getClass().getName());
		return jo;
	}

	@Override
	public Object fromJSON(JSONObject map) {
		String className = map.getString("_DAOClass");
		try {
			DAO<?> dao = (DAO<?>) Class.forName(className).newInstance();
			for (String k : map.keySet()) {
				dao.setV(k, map.get(k));
			}
			return dao;
		} catch (Exception e) {
			throw new JSONConvertException(e);
		}
	}

}
