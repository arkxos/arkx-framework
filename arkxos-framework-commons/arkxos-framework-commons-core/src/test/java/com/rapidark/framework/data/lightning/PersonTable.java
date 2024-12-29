package com.arkxos.framework.data.lightning;

import java.util.ArrayList;
import java.util.List;

import com.arkxos.framework.data.lightning.ILightningTable;
import com.arkxos.framework.data.lightning.LightningColumn;

/**
 *  
 * @author Darkness
 * @date 2015年12月19日 下午8:39:47
 * @version V1.0
 * @since infinity 1.0
 */
public class PersonTable implements ILightningTable {

	private List<Person> data = new ArrayList<>();
	
	public PersonTable(List<Person> data) {
		this.data = data;
	}
	
	@Override
	public String getTableName() {
		return "Person";
	}

	@Override
	public LightningColumn[] getLightningColumns() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return getLightningColumns().length;
	}
	
}

