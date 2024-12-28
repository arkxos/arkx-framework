package com.rapidark.framework.commons.util;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author Darkness
 * @date 2015年8月29日 下午7:55:08
 * @version V1.0
 * @since infinity 1.0
 */
public class TimeWatch {

	private static boolean isEnablePrint = true;

	public static void enablePrint() {
		isEnablePrint = true;
	}

	public static void disablePrint() {
		isEnablePrint = false;
	}
	
	public static TimeWatch create() {
		return new TimeWatch();
	}

	private long start;
	private long markStart;
	private String taskName= "task";
	
	Map<String, Long> marks = new LinkedHashMap<>();

	public TimeWatch startWithTaskName(String taskName) {
		this.taskName = taskName;
		this.start = System.nanoTime();
		this.markStart = start;
		return this;
	}

	public String stopAndPrint() {
		String result = "";
		
		if (isEnablePrint) {
			long end = System.nanoTime();
			long cost = end - start;
			
			result += this.taskName + " 耗时：" + formatTime(cost) + "\n";
			
			for (String mark : marks.keySet()) {
				result += "\t\t" + mark + " 耗时：" + formatTime(marks.get(mark)) + "\n";
			}
		}
		
		System.out.println(result);
		
		return result;
	}
	
	public long stop() {
		long end = System.nanoTime();
		long cost = end - start;
		return cost;
	}

	public long getCost() {
		long end = System.nanoTime();
		long cost = end - start;
		return cost;
	}

	public static void main(String[] args) {
		int totalMs = 276399;
		System.out.println(formatTime(totalMs));
		System.out.println(LocalTime.ofSecondOfDay(276399/1000).withNano(276399%1000));
		
		TimeWatch timeWatch = TimeWatch.create().startWithTaskName("abc");
		System.out.println("ddd");
		timeWatch.stopAndPrint();
	}
	
	/* 
	 * 毫秒转化时分秒毫秒 
	 */  
	public static String formatTime(long ms) {  
//		Integer namiao = 1000;
		long weimiaoUnit = 1000; 
		long haomiaoUnit = weimiaoUnit * 1000; 
		long secondUnit = haomiaoUnit * 1000; 
		long minuteUnit = secondUnit * 60;  
		long hourUnit = minuteUnit * 60;  
		long dayUnit = hourUnit * 24;  
	  
	    Long day = ms / dayUnit;  
	    Long hour = (ms - day * dayUnit) / hourUnit;  
	    Long minute = (ms - day * dayUnit - hour * hourUnit) / minuteUnit;  
	    Long second = (ms - day * dayUnit - hour * hourUnit - minute * minuteUnit) / secondUnit;  
	    Long haomiao = (ms - day * dayUnit - hour * hourUnit - minute * minuteUnit - second * secondUnit) / haomiaoUnit;  
	    Long weimiao = (ms - day * dayUnit - hour * hourUnit - minute * minuteUnit - second * secondUnit - haomiao * haomiaoUnit) / weimiaoUnit;  
	    Long namiao = ms - day * dayUnit - hour * hourUnit - minute * minuteUnit - second * secondUnit - haomiao * haomiaoUnit - weimiao * weimiaoUnit;
	      
	    StringBuffer sb = new StringBuffer();  
	    if(day > 0) {  
	        sb.append(day+"天");  
	    }  
	    if(hour > 0) {  
	        sb.append(hour+"小时");  
	    }  
	    if(minute > 0) {  
	        sb.append(minute+"分");  
	    }  
	    if(second > 0) {  
	        sb.append(second+"秒");  
	    }  
	    if(haomiao > 0) {  
	        sb.append(haomiao+"毫秒");  
	    }  
	    if(weimiao > 0) {  
	        sb.append(weimiao+"微秒");  
	    }  
	    if(namiao > 0) {  
	        sb.append(namiao+"纳秒");  
	    }  
	    
	    String result = sb.toString();
	    if(result.length() == 0) {
	    	result = "0纳秒";
	    }
	    return result;  
	}

	String lastMark = "prepare";
	
	public void mark(String desc) {
		long end = System.nanoTime();
		long cost = end - markStart;
		
		marks.put(desc, cost);
		
		this.markStart = System.nanoTime();
	}  
}
