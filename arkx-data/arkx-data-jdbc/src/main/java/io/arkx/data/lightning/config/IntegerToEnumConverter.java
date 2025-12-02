package io.arkx.data.lightning.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import io.arkx.framework.data.common.entity.Status;

// 将整数转换为枚举（读操作）
@ReadingConverter
public class IntegerToEnumConverter implements Converter<Integer, Status> {
    // private final Status enumType;
    //
    // public IntegerToEnumConverter(Status enumType) {
    // this.enumType = enumType;
    // }

    @Override
    public Status convert(Integer source) {
        return Status.fromCode(source);
    }
}
