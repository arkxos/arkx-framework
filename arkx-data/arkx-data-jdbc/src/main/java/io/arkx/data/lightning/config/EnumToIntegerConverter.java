package io.arkx.data.lightning.config;

import java.sql.JDBCType;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.mapping.JdbcValue;

import io.arkx.framework.data.common.entity.Status;

// 将枚举转换为整数（写操作）
@WritingConverter
public class EnumToIntegerConverter implements Converter<Status, JdbcValue> {

    @Override
    public JdbcValue convert(Status source) {
        // 将枚举转换为数据库可存储的值
        return JdbcValue.of(source.getCode(), JDBCType.INTEGER);
    }

}
