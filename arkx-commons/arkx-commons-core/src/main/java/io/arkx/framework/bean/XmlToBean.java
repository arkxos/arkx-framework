package io.arkx.framework.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.arkx.framework.data.xml.XMLMultiLoader;

/**
 * @author Darkness
 * @date 2013-11-5 下午02:08:24
 * @version V1.0
 */
public class XmlToBean {

    public static final String PROPERTY = "PROPERTY";

    public static final String ATTRIBUTE = "ATTRIBUTE";

    static Map<String, String> beanAliasMap = new HashMap<String, String>();

    public static void registerBeanAlias(String alias, String beanClassName) {
        beanAliasMap.put(alias, beanClassName);
    }

    /**
     * 默认使用attribute转换bean
     *
     * @author Darkness
     * @date 2013-12-14 下午04:05:40
     * @version V1.0
     */
    public static List<Object> toBean(XMLMultiLoader xmlLoader, boolean ingoreRoot) {
        return toBean(xmlLoader, ATTRIBUTE, ingoreRoot);
    }

    public static List<Object> toBean(XMLMultiLoader xmlLoader, String style, boolean ingoreRoot) {
        if (ATTRIBUTE.equals(style)) {
            return new AttributeStyleXmlToBean(ingoreRoot).toBean(xmlLoader);
        }
        return new PropertyStyleXmlToBean(ingoreRoot).toBean(xmlLoader);
    }

}
