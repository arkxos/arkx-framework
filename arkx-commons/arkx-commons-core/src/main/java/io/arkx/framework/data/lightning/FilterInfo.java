package io.arkx.framework.data.lightning;

import io.arkx.framework.commons.util.DateUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.commons.util.TimeWatch;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 *  
 * @author Darkness
 * @date 2015年12月19日 下午6:04:29
 * @version V1.0
 * @since infinity 1.0
 * 
 * 根据where条件过滤数据
 * @param header 
 * 
 * @param whereString
 *            如：“ name='darkness' and password='sky' ”
 * @return
 * @throws IOException 
 */
public class FilterInfo {
	 
	Map<String, String> equalMap = new HashMap<>();
	Map<String, String> likeMap = new HashMap<>();
	Map<String, String> gtMap = new HashMap<>();
	Map<String, String> ltMap = new HashMap<>();
	
	Set<String> needFilterColumnNames = new HashSet<>();
	boolean isNeedFilter = false;
	
	public FilterInfo(String whereString) {
		if(!StringUtil.isEmpty(whereString)) {
			whereString = whereString.replaceAll("\'", "");
			
			if(whereString != null) {
				final String[] params = whereString.split("and");
				
				for (int k = 0; k < params.length; k++) {
					String[] paramInfo = params[k].split("=");
					if(paramInfo.length ==2) {
						needFilterColumnNames.add(paramInfo[0]);
						equalMap.put(paramInfo[0], paramInfo[1].trim());
					}
					
					String[] paramInfo2 = params[k].split("like");
					if(paramInfo2.length ==2) {
						needFilterColumnNames.add(paramInfo2[0]);
						likeMap.put(paramInfo2[0], paramInfo2[1].trim());
					}
					
					String[] paramInfo3 = params[k].split(">");
					if(paramInfo3.length ==2) {
						needFilterColumnNames.add(paramInfo3[0]);
						gtMap.put(paramInfo3[0], paramInfo3[1].trim());
					}
					
					String[] paramInfo4 = params[k].split("<");
					if(paramInfo4.length ==2) {
						needFilterColumnNames.add(paramInfo4[0]);
						ltMap.put(paramInfo4[0], paramInfo4[1].trim());
						System.out.println(ltMap);
					}
				}
			}
			
			if(needFilterColumnNames.size() == 0) {
				this.isNeedFilter = false;
			} else {
				this.isNeedFilter = true;
			}
		}
	}

	Map<String, Long> equalConvertValues = new HashMap<>();
	
	public long getEqualValue(String columnName) {
		Long convertValue = equalConvertValues.get(columnName);
		if(convertValue == null) {
			convertValue = LocalDate.parse(this.equalMap.get(columnName)).toEpochDay();
			equalConvertValues.put(columnName, convertValue);
		}
		 
		return convertValue;
	}

	public List<Integer> filter(PkList pkList) {
		List<Integer> result = new ArrayList<>();
		
		boolean needFilter = false;
		for (String columnName : this.needFilterColumnNames) {
			if(columnName.equals(pkList.columnName)) {
				needFilter = true;
				break;
			}
		}
		
		if(!needFilter) {
			return result;
		}
		TimeWatch timeWatch = new TimeWatch();
		timeWatch.startWithTaskName("===1");
		String columnName = pkList.columnName;
		LightningColumnType columnType = pkList.columnType;
		boolean isFilterEqual = this.equalMap.containsKey(columnName);
		String equalValue = this.equalMap.get(columnName);
		long comparevalue = this.getEqualValue(columnName);
		boolean isFilterGt = this.gtMap.containsKey(columnName);
		String gtValue = this.gtMap.get(columnName);
		boolean isFilterLt = this.ltMap.containsKey(columnName);
		String ltValue =this.ltMap.get(columnName);
		timeWatch.stopAndPrint();
		timeWatch.startWithTaskName("===2");
		boolean isInt = columnType == LightningColumnType.INT; 
		boolean isFloat = columnType == LightningColumnType.FLOAT;
		boolean isDouble = columnType == LightningColumnType.DOUBLE;
		boolean isLong = columnType == LightningColumnType.LONG;
		boolean isDate = columnType == LightningColumnType.DATE;
		
		for(int index = 0; index< pkList.values.size(); index++) {
			Object _value = pkList.values.get(index);
			if (isInt) {
				int value = (int)_value;
				if (isFilterEqual) {
					if (value != Integer.parseInt(equalValue)) {
						continue;
					}
				}
				if (isFilterGt) {
					if (value <= Integer.parseInt(gtValue)) {
						continue;
					}
				}
				if (isFilterLt) {
					if (value >= Integer.parseInt(ltValue)) {
						continue;
					}
				}
			} else if (isFloat) {
				float value = (float)_value;
				if (this.equalMap.containsKey(columnName)) {
					if (value != Float.parseFloat(this.equalMap.get(columnName))) {
						continue;
					}
				}
				if (this.gtMap.containsKey(columnName)) {
					if (value <= Float.parseFloat(this.gtMap.get(columnName))) {
						continue;
					}
				}
				if (this.ltMap.containsKey(columnName)) {
					if (value >= Float.parseFloat(this.ltMap.get(columnName))) {
						continue;
					}
				}
			} else if (isDouble) {
				double value = (double)_value;
				if (this.equalMap.containsKey(columnName)) {
					if (value != Double.parseDouble(this.equalMap.get(columnName))) {
						continue;
					}
				}
				if (this.gtMap.containsKey(columnName)) {
					if (value <= Double.parseDouble(this.gtMap.get(columnName))) {
						continue;
					}
				}
				if (this.ltMap.containsKey(columnName)) {
					if (value >= Double.parseDouble(this.ltMap.get(columnName))) {
						continue;
					}
				}
			} else if (isLong) {
				long value = (long)_value;
				if (isFilterEqual) {
					if (value != Long.parseLong(this.equalMap.get(columnName))) {
						continue;
					}
				}
				if (this.gtMap.containsKey(columnName)) {
					if (value <= Long.parseLong(this.gtMap.get(columnName))) {
						continue;
					}
				}
				if (this.ltMap.containsKey(columnName)) {
					if (value >= Long.parseLong(this.ltMap.get(columnName))) {
						continue;
					}
				}
			} else if (isDate) {
				long value = -1;
//				if(_value instanceof LocalDate) {
//					value = ((LocalDate)_value).toEpochDay();
//				} else {
					value = (long)_value; 
//				}

				if (isFilterEqual) {
					if (value != comparevalue) {
						continue;
					}
				}
				if (isFilterGt) {
					 comparevalue = LocalDate.parse(this.gtMap.get(columnName)).toEpochDay();
					if (value <= comparevalue) {
						continue;
					}
				}
				if (isFilterLt) {
					 comparevalue = LocalDate.parse(this.ltMap.get(columnName)).toEpochDay();
					if (value >= comparevalue) {
						continue;
					}
				}

			} else if (columnType == LightningColumnType.DATETIME) {
				long value = (long)_value;

				if (this.equalMap.containsKey(columnName)) {
					 comparevalue = DateUtil.parse(this.equalMap.get(columnName)).getTime();
					if (value != comparevalue) {
						continue;
					}
				}
				if (this.gtMap.containsKey(columnName)) {
					 comparevalue = DateUtil.parse(this.gtMap.get(columnName)).getTime();
					if (value <= comparevalue) {
						continue;
					}
				}
				if (this.ltMap.containsKey(columnName)) {
					 comparevalue = DateUtil.parse(this.ltMap.get(columnName)).getTime();
					if (value >= comparevalue) {
						continue;
					}
				}
			} else if (columnType == LightningColumnType.FIXED_STRING) {
				String value = (String)_value;
				if (this.equalMap.containsKey(columnName)) {
					if (!value.equals(this.equalMap.get(columnName))) {
						continue;
					}
				}
				if (this.likeMap.containsKey(columnName)) {
					if (value.indexOf(this.likeMap.get(columnName)) == -1) {
						continue;
					}
				}
			} else if (columnType == LightningColumnType.STRING) {
				String value = (String)_value;
				if (this.equalMap.containsKey(columnName)) {
					if (!value.equals(this.equalMap.get(columnName))) {
						continue;
					}
				}
				if (this.likeMap.containsKey(columnName)) {
					if (value.indexOf(this.likeMap.get(columnName)) == -1) {
						continue;
					}
				}
			}
			
			result.add(index);
		}
		timeWatch.stopAndPrint();
		
		return result;
	}
	
	public static void main(String[] args) {
		TimeWatch timeWatch = new TimeWatch();
		timeWatch.startWithTaskName("build");
		List<Integer> datas = new ArrayList<>();
		for(int i=0;i<100*10000;i++) {
			datas.add(i);
		}
		timeWatch.stopAndPrint();
		timeWatch.startWithTaskName("loop");
		for (int i = 0; i < datas.size(); i++) {
			int value = datas.get(i);
		}
		timeWatch.stopAndPrint();
	}
}
