package com.rapidark.cloud.platform.gateway.support;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.core.style.DefaultToStringStyler;
import org.springframework.core.style.DefaultValueStyler;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.ClassUtils;

import java.util.function.Function;

/**
 * @Description 模仿GatewayToStringStyler类，对/actuator/gateway/routes输出的信息中filters做格式化处理，默认只对GatewayFilterFactory做了处理，未对GatewayFilter做格式化；
 * @Author JL
 * @Date 2023/10/11
 * @Version V1.0
 */
public class CustomGatewayToStringStyler extends DefaultToStringStyler {

    private static final CustomGatewayToStringStyler FILTER_INSTANCE = new CustomGatewayToStringStyler(GatewayFilter.class,
            CustomGatewayToStringStyler::normalizeFilterFactoryName);

    private final Function<Class, String> classNameFormatter;

    private final Class instanceClass;

    public static ToStringCreator filterToStringCreator(Object obj) {
        return new ToStringCreator(obj, FILTER_INSTANCE);
    }

    public CustomGatewayToStringStyler(Class instanceClass, Function<Class, String> classNameFormatter) {
        super(new DefaultValueStyler());
        this.classNameFormatter = classNameFormatter;
        this.instanceClass = instanceClass;
    }

    @Override
    public void styleStart(StringBuilder buffer, Object obj) {
        if (!obj.getClass().isArray()) {
            String shortName;
            if (instanceClass.isInstance(obj)) {
                shortName = classNameFormatter.apply(obj.getClass());
            }
            else {
                shortName = ClassUtils.getShortName(obj.getClass());
            }
            buffer.append('[').append(shortName);
        }
        else {
            buffer.append('[');
            styleValue(buffer, obj);
        }
    }

    public static String normalizeFilterFactoryName(Class<? extends GatewayFilter> clazz) {
        return removeGarbage(clazz.getSimpleName().replace(GatewayFilter.class.getSimpleName(), ""));
    }

    private static String removeGarbage(String s) {
        int garbageIdx = s.indexOf("$Mockito");
        if (garbageIdx > 0) {
            return s.substring(0, garbageIdx);
        }

        return s;
    }
}
