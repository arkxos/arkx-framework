package io.arkx.framework.boot.spring.axon;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 
 * @author Darkness
 * @date 2019-05-26 17:44:26
 * @version V1.0
 */
public class AxonUtil {

	private static DateTimeFormatter Date_Time_Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	public static LocalDateTime instant2LocalDateTime(Instant instant) {
		 LocalDateTime localDateTime = LocalDateTime.ofInstant(instant,
	              ZoneId.of(ZoneId.SHORT_IDS.get("CTT")));
		 return localDateTime;
	}
	
	public static String formatInstant(Instant instant) {
		 LocalDateTime date = LocalDateTime.ofInstant(instant,
	              ZoneId.of(ZoneId.SHORT_IDS.get("CTT")));
		 return date.format(Date_Time_Formatter);
	}
	
}

