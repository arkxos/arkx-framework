package com.github.dreamroute.sqlprinter.starter.converter.def;

import java.util.Date;

import com.github.dreamroute.sqlprinter.starter.anno.ValueConverter;

import cn.hutool.core.date.DateUtil;

/**
 * 日期转换器
 *
 * @author w.dehai.2021/9/7.15:35
 */
public class DateConverter implements ValueConverter {

    @Override
    public Object convert(Object value) {
        if (value instanceof Date) {
            value = DateUtil.format((Date) value, "yyyy-MM-dd HH:mm:ss.SSS");
        }
        return value;
    }

}
