package com.rapidark.common.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;

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

}
