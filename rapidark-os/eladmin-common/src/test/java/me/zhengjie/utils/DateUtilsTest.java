package me.zhengjie.utils;

import com.rapidark.common.utils.DateUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

public class DateUtilsTest {
    @Test
    public void test1() {
        long l = System.currentTimeMillis() / 1000;
        LocalDateTime localDateTime = com.rapidark.common.utils.DateUtil.fromTimeStamp(l);
        System.out.print(com.rapidark.common.utils.DateUtil.localDateTimeFormatyMdHms(localDateTime));
    }

    @Test
    public void test2() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(com.rapidark.common.utils.DateUtil.localDateTimeFormatyMdHms(now));
        Date date = com.rapidark.common.utils.DateUtil.toDate(now);
        LocalDateTime localDateTime = com.rapidark.common.utils.DateUtil.toLocalDateTime(date);
        System.out.println(com.rapidark.common.utils.DateUtil.localDateTimeFormatyMdHms(localDateTime));
        LocalDateTime localDateTime1 = com.rapidark.common.utils.DateUtil.fromTimeStamp(date.getTime() / 1000);
        System.out.println(DateUtil.localDateTimeFormatyMdHms(localDateTime1));
    }
}
