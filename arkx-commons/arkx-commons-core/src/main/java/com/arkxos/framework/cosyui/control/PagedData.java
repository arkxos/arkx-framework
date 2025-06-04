package com.arkxos.framework.cosyui.control;

import com.alibaba.fastjson.annotation.JSONField;
import io.arkx.framework.commons.collection.DataTable;
import com.arkxos.framework.json.JSON;

/**
 * 
 * @author Darkness
 * @date 2016年12月21日 下午5:02:42
 * @version V1.0
 */
public class PagedData {

	int total;
	String data;
	
	public PagedData() {
	}

	public PagedData(int total, DataTable dataTable) {
		this.total = total;
		this.data = JSON.toJSONString(dataTable);
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	@JSONField(serialize=false)
	public DataTable getDataTable() {
		return JSON.parseBean(this.data, DataTable.class);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
