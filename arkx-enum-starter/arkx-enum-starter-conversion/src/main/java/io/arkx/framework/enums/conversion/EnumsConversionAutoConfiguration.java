package io.arkx.framework.enums.conversion;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

import jakarta.persistence.AttributeConverter;

/**
 * @author: zhuCan
 * @date: 2020/7/10 17:17
 * @description:
 */
@AutoConfiguration
@ConditionalOnClass({ AttributeConverter.class })
@Import({ ConverterPackageScan.class })
public class EnumsConversionAutoConfiguration {

}
