package com.arkxos.framework.enums.conversion;

import jakarta.persistence.AttributeConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * @author: zhuCan
 * @date: 2020/7/10 17:17
 * @description:
 */
@AutoConfiguration
@ConditionalOnClass({AttributeConverter.class})
@Import({ConverterPackageScan.class})
public class EnumsConversionAutoConfiguration {


}
