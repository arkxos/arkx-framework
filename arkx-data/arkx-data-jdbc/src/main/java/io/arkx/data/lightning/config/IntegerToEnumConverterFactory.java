//package io.arkx.framework.data.common;
//
//import org.apache.poi.ss.formula.functions.T;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.core.convert.converter.ConverterFactory;
//import org.springframework.data.convert.ReadingConverter;
//
//@ReadingConverter
//public class IntegerToEnumConverterFactory implements ConverterFactory<Integer, Enum<?>> {
//
//	@Override
//	public <T extends Enum<?>> Converter<Integer, T> getConverter(Class<T> targetType) {
//		return new IntegerToEnumConverter(targetType);
//	}
//
//}