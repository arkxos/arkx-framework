package io.arkx.framework.enums.conversion;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

/**
 * @author zhuCan
 * @description 开启 jpa对象转换功能
 * @since 2020-12-03 17:13
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ EnumsConversionAutoConfiguration.class })
public @interface EnableJpaAutoConversion {

}
