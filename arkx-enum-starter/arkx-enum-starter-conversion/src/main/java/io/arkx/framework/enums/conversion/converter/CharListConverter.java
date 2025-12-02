package io.arkx.framework.enums.conversion.converter;

import java.util.List;

import jakarta.persistence.Converter;

/**
 * @author: zhuCan
 * @date: 2020/7/10 18:12
 * @description:
 */
@Converter(autoApply = true)
public class CharListConverter extends AbstractJsonConverter<List<Character>> {

}
