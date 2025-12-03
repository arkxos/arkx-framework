package io.arkx.framework.commons.collection;

/**
 * @author Darkness
 * @date 2020年10月26日 下午9:33:18
 * @version V1.0
 */
public class FieldPropertyConvert {

    public static void main(String[] args) {
        System.out.println(propertyToField("userName"));
        System.out.println(fieldToProperty("user_naMe"));
        System.out.println(fieldToProperty("userNaMe"));
        System.out.println(FieldPropertyConvert.fieldToProperty("UserNaMe"));

        System.out.println(fieldToProperty2("user_naMe"));
        System.out.println(fieldToProperty2("UserNaMe"));
    }

    /**
     * 判断是否是大写字母
     *
     * @param c
     * @return
     */
    public static Boolean isUp(char c) {
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        return false;
    }

    /**
     * java对象属性转换为数据库字段，如userName-->user_name
     *
     * @param property
     * @return
     */
    public static String propertyToField(String property) {
        if (null == property) {
            return "";
        }
        char[] chars = property.toCharArray();
        StringBuffer field = new StringBuffer();
        for (char c : chars) {
            if (isUp(c)) {
                field.append("_" + String.valueOf(c).toLowerCase());
            } else {
                field.append(c);
            }
        }
        return field.toString();
    }

    /**
     * 将数据库字段转换为java属性，如user_name-->userName
     *
     * @param field
     *            字段名
     * @return
     */
    public static String fieldToProperty(String field) {
        if (null == field) {
            return "";
        }
        field = field.toLowerCase();
        if (field.indexOf("_") > 0) {
            field = field.toLowerCase();
        }
        char[] chars = field.toCharArray();
        StringBuffer property = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '_') {
                int j = i + 1;
                if (j < chars.length) {
                    property.append(String.valueOf(chars[j]).toUpperCase());
                    i++;
                }
            } else {
                if (i == 0) {
                    property.append(String.valueOf(c).toLowerCase());
                } else {
                    property.append(c);
                }
            }
        }
        return property.toString();
    }

    public static String fieldToProperty2(String field) {
        if (null == field) {
            return "";
        }
        // field = field.toLowerCase();
        if (field.indexOf("_") > 0) {
            field = field.toLowerCase();
        }
        char[] chars = field.toCharArray();
        StringBuffer property = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '_') {
                int j = i + 1;
                if (j < chars.length) {
                    property.append(String.valueOf(chars[j]).toUpperCase());
                    i++;
                }
            } else {
                if (i == 0) {
                    property.append(String.valueOf(c).toLowerCase());
                } else {
                    property.append(c);
                }
            }
        }
        return property.toString();
    }

}
