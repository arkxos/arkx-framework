package io.arkx.framework.cosyui.control;

import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.json.JSON;

import com.alibaba.fastjson.annotation.JSONField;

/**
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

	@JSONField(serialize = false)
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
