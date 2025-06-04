package com.arkxos.framework.json.convert;

import java.util.ArrayList;

import io.arkx.framework.data.db.orm.DAO;
import io.arkx.framework.data.db.orm.DAOSet;
import com.arkxos.framework.json.JSONArray;
import com.arkxos.framework.json.JSONObject;

/**
 * DAOSet的JSON转换器
 * 
 */
public class DAOSetConvertor implements IJSONConvertor {

	@Override
	public String getExtendItemID() {
		return "DAOSet";
	}

	@Override
	public String getExtendItemName() {
		return getExtendItemID();
	}

	@Override
	public boolean match(Object obj) {
		return obj instanceof DAOSet;
	}

	@Override
	public JSONObject toJSON(Object obj) {
		JSONObject jo = new JSONObject();
		DAOSet<?> set = (DAOSet<?>) obj;
		jo.put("_DAOClass", obj.getClass().getName());
		ArrayList<DAO<?>> list = new ArrayList<DAO<?>>(set.size());
		for (int i = 0; i < list.size(); i++) {
			list.add(set.get(i));
		}
		jo.put("Data", list);
		return jo;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object fromJSON(JSONObject map) {
		String className = map.getString("_DAOClass");
		try {
			DAO dao = (DAO) Class.forName(className).newInstance();
			JSONArray arr = (JSONArray) map.get("Data");
			DAOSet set = dao.newSet();
			for (Object obj : arr) {
				dao = (DAO) obj;
				set.add(dao);
			}
			return set;
		} catch (Exception e) {
			throw new JSONConvertException(e);
		}
	}
}
