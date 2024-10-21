package com.rapidark.framework.common.utils;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/14 14:42
 */
public class DateUtilTest {

    @Test
    public void testMonthBetween() {
        LocalDate localDateTime1 = LocalDate.of(2021, 1, 2);
        LocalDate localDateTime2 = LocalDate.of(2021, 2, 2);

        System.out.println(Period.between(localDateTime1, localDateTime2).getMonths());
        System.out.println(Period.between(localDateTime2, localDateTime1).getMonths());
    }

    @Test
    public void testMinutesBetween() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime1 = LocalDateTime.parse("2021-05-01 12:25:29", formatter);
        LocalDateTime localDateTime2 = LocalDateTime.parse("2021-05-01 16:25:59", formatter);

        System.out.println(Duration.between(localDateTime1, localDateTime2).toMinutes());
        System.out.println(Duration.between(localDateTime2, localDateTime1).toMinutes());
    }

}
