package io.arkx.framework.data.db.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.arkx.framework.data.db.core.schema.ColumnDescription;

public class TypeHandler {

    // 类型转换策略缓存
    private static final Map<String, TypeConverter> CONVERTER_CACHE = new ConcurrentHashMap<>();

    static {
        // 注册基本类型转换器
        registerDefaultConverters();
    }

    public static void typeMapping(List<ColumnDescription> sourceColumnDescriptions,
            List<ColumnDescription> targetColumnDescriptions, Object[] dataList) {
        List<String> sourceColumnJavaTypes = sourceColumnDescriptions.stream()
                .map(ColumnDescription::getFiledTypeClassName).collect(Collectors.toList());
        List<String> targetColumnJavaTypes = targetColumnDescriptions.stream()
                .map(ColumnDescription::getFiledTypeClassName).collect(Collectors.toList());

        for (int j = 0; j < dataList.length; j++) {
            String sourceColumnJavaType = sourceColumnJavaTypes.get(j);
            String targetColumnJavaType = targetColumnJavaTypes.get(j);
            if (dataList[j] != null && StringUtils.equals(sourceColumnJavaType, targetColumnJavaType)) {
                dataList[j] = CONVERTER_CACHE.get(targetColumnJavaType).convert(dataList[j]);
            }
        }
    }

    /**
     * 注册默认的类型转换器
     */
    private static void registerDefaultConverters() {
        CONVERTER_CACHE.put("java.lang.String", new StringConverter());
        CONVERTER_CACHE.put("java.lang.Boolean", new BooleanConverter());
        CONVERTER_CACHE.put("java.lang.Byte", new ByteConverter());
        CONVERTER_CACHE.put("java.lang.Short", new ShortConverter());
        CONVERTER_CACHE.put("java.lang.Integer", new IntegerConverter());
        CONVERTER_CACHE.put("java.lang.Long", new LongConverter());
        CONVERTER_CACHE.put("java.lang.Float", new FloatConverter());
        CONVERTER_CACHE.put("java.lang.Double", new DoubleConverter());
        CONVERTER_CACHE.put("java.math.BigDecimal", new BigDecimalConverter());
        CONVERTER_CACHE.put("java.math.BigInteger", new BigIntegerConverter());
        CONVERTER_CACHE.put("java.util.Date", new UtilDateConverter());
        CONVERTER_CACHE.put("java.sql.Date", new SqlDateConverter());
        CONVERTER_CACHE.put("java.sql.Time", new SqlTimeConverter());
        CONVERTER_CACHE.put("java.sql.Timestamp", new SqlTimestampConverter());
        CONVERTER_CACHE.put("java.time.LocalDate", new LocalDateConverter());
        CONVERTER_CACHE.put("java.time.LocalDateTime", new LocalDateTimeConverter());
        CONVERTER_CACHE.put("java.time.LocalTime", new LocalTimeConverter());
        CONVERTER_CACHE.put("java.time.ZonedDateTime", new ZonedDateTimeConverter());
        CONVERTER_CACHE.put("java.util.UUID", new UUIDConverter());
    }

    /**
     * 执行字段级别的类型转换
     *
     * @param value
     *            待转换的值
     * @param sourceType
     *            源数据类型名称
     * @param targetType
     *            目标数据类型名称
     * @return 转换后的值
     */
    public static Object convert(Object value, String sourceType, String targetType) {
        if (value == null) {
            return null;
        }

        if (sourceType.equalsIgnoreCase(targetType)) {
            return value;
        }

        try {
            // 统一转换为大写进行匹配
            String targetUpper = targetType.toLowerCase(Locale.ENGLISH);

            // 先尝试精确匹配
            TypeConverter converter = CONVERTER_CACHE.get(targetUpper);
            if (converter != null) {
                return converter.convert(value);
            }

            // 尝试模糊匹配
            if (targetUpper.contains("date")) {
                return new UtilDateConverter().convert(value);
            } else if (targetUpper.contains("time")) {
                return new SqlTimeConverter().convert(value);
            } else if (targetUpper.contains("timestamp")) {
                return new SqlTimestampConverter().convert(value);
            } else if (targetUpper.contains("number") || targetUpper.contains("numeric")
                    || targetUpper.contains("bigdecimal")) {
                return new BigDecimalConverter().convert(value);
            }

            // 默认使用toString方法
            return value.toString();
        } catch (Exception e) {
            System.err.printf("类型转换失败：从 [%s] 到 [%s]，原始值: [%s]，错误: %s%n", sourceType, targetType, value, e.getMessage());
            return null;
        }
    }

    /**
     * 注册自定义类型转换器
     */
    public static void registerConverter(String targetType, TypeConverter converter) {
        CONVERTER_CACHE.put(targetType.toLowerCase(Locale.ENGLISH), converter);
    }

    /**
     * 批量类型转换方法
     *
     * @param values
     *            需要转换的值数组
     * @param sourceTypes
     *            源数据类型列表
     * @param targetTypes
     *            目标数据类型列表
     * @return 转换后的对象数组
     */
    public static Object[] batchConvert(List<Object> values, List<String> sourceTypes, List<String> targetTypes) {
        if (values.size() != sourceTypes.size() || values.size() != targetTypes.size()) {
            throw new IllegalArgumentException("输入参数大小必须一致");
        }

        Object[] result = new Object[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = convert(values.get(i), sourceTypes.get(i), targetTypes.get(i));
        }
        return result;
    }

    /**
     * 基于列描述的类型转换
     *
     * @param row
     *            数据行
     * @param sourceColumns
     *            源列定义
     * @param targetColumns
     *            目标列定义
     * @return 转换后的数据行
     */
    public static Object[] convertRow(Object[] row, List<ColumnDescription> sourceColumns,
            List<ColumnDescription> targetColumns) {
        if (row.length != sourceColumns.size() || row.length != targetColumns.size()) {
            throw new IllegalArgumentException("输入数据与列定义数量必须一致");
        }

        Object[] result = new Object[row.length];
        for (int i = 0; i < row.length; i++) {
            result[i] = convert(row[i], sourceColumns.get(i).getFieldTypeName(),
                    targetColumns.get(i).getFieldTypeName());
        }
        return result;
    }

    /**
     * 类型转换器接口
     */
    @FunctionalInterface
    private interface TypeConverter {
        Object convert(Object value);
    }

    /**
     * 字符串转换器
     */
    private static class StringConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            return value.toString();
        }
    }

    /**
     * 布尔类型转换器
     */
    private static class BooleanConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof Boolean) {
                return value;
            } else if (value instanceof Number number) {
                return number.intValue() != 0;
            } else {
                String str = value.toString().trim().toLowerCase();
                if (str.equals("true") || str.equals("1") || str.equals("yes") || str.equals("on")) {
                    return true;
                } else if (str.equals("false") || str.equals("0") || str.equals("no") || str.equals("off")) {
                    return false;
                }
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为布尔类型".formatted(value));
        }
    }

    /**
     * 字节类型转换器
     */
    private static class ByteConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof Byte) {
                return value;
            } else if (value instanceof Number number) {
                return number.byteValue();
            } else if (value instanceof String string) {
                return Byte.parseByte(string);
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为字节类型".formatted(value));
        }
    }

    /**
     * 短整型转换器
     */
    private static class ShortConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof Short) {
                return value;
            } else if (value instanceof Number number) {
                return number.shortValue();
            } else if (value instanceof String string) {
                return Short.parseShort(string);
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为短整型".formatted(value));
        }
    }

    /**
     * 整型转换器
     */
    private static class IntegerConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof Integer) {
                return value;
            } else if (value instanceof Number number) {
                return number.intValue();
            } else if (value instanceof String string) {
                return Integer.parseInt(string);
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为整型".formatted(value));
        }
    }

    /**
     * 长整型转换器
     */
    private static class LongConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof Long) {
                return value;
            } else if (value instanceof Number number) {
                return number.longValue();
            } else if (value instanceof String string) {
                return Long.parseLong(string);
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为长整型".formatted(value));
        }
    }

    /**
     * 浮点型转换器
     */
    private static class FloatConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof Float) {
                return value;
            } else if (value instanceof Number number) {
                return number.floatValue();
            } else if (value instanceof String string) {
                return Float.parseFloat(string);
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为浮点型".formatted(value));
        }
    }

    /**
     * 双精度浮点型转换器
     */
    private static class DoubleConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof Double) {
                return value;
            } else if (value instanceof Number number) {
                return number.doubleValue();
            } else if (value instanceof String string) {
                return Double.parseDouble(string);
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为双精度浮点型".formatted(value));
        }
    }

    /**
     * 大数字类型转换器
     */
    private static class BigDecimalConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof BigDecimal) {
                return value;
            } else if (value instanceof BigInteger integer) {
                return new BigDecimal(integer);
            } else if (value instanceof Number) {
                return new BigDecimal(value.toString());
            } else if (value instanceof String string) {
                return new BigDecimal(string);
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为大数字类型".formatted(value));
        }
    }

    /**
     * 大整数类型转换器
     */
    private static class BigIntegerConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof BigInteger) {
                return value;
            } else if (value instanceof BigDecimal decimal) {
                return decimal.toBigInteger();
            } else if (value instanceof Number) {
                return new BigInteger(value.toString());
            } else if (value instanceof String string) {
                return new BigInteger(string);
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为大整数类型".formatted(value));
        }
    }

    /**
     * Java.util.Date 类型转换器
     */
    private static class UtilDateConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof Date) {
                return value;
            } else if (value instanceof LocalDate date) {
                return Date.valueOf(date);
            } else if (value instanceof LocalDateTime time) {
                return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
            } else if (value instanceof String string) {
                try {
                    // ISO日期格式
                    return Date.from(LocalDate.parse(string).atStartOfDay(ZoneId.systemDefault()).toInstant());
                } catch (DateTimeParseException ignored) {
                    // 尝试解析为时间戳格式
                    try {
                        return new Date(Long.parseLong(string));
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("无法将 [%s] 转换为日期类型".formatted(value));
                    }
                }
            } else if (value instanceof Timestamp timestamp) {
                return new Date(timestamp.getTime());
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为日期类型".formatted(value));
        }
    }

    /**
     * Java.sql.Date 类型转换器
     */
    private static class SqlDateConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof Date) {
                return value;
            } else if (value instanceof LocalDate date) {
                return Date.valueOf(date);
            } else if (value instanceof String string) {
                try {
                    // ISO日期格式
                    return Date.valueOf(LocalDate.parse(string));
                } catch (DateTimeParseException ignored) {
                    // 尝试解析为时间戳格式
                    try {
                        return new Date(Long.parseLong(string));
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("无法将 [%s] 转换为SQL日期类型".formatted(value));
                    }
                }
            } else if (value instanceof Timestamp timestamp) {
                return new Date(timestamp.getTime());
            } else if (value instanceof Date) {
                return value;
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为SQL日期类型".formatted(value));
        }
    }

    /**
     * 时间类型转换器
     */
    private static class SqlTimeConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof Time) {
                return value;
            } else if (value instanceof LocalTime time) {
                return Time.valueOf(time);
            } else if (value instanceof String string) {
                try {
                    // ISO时间格式
                    return Time.valueOf(LocalTime.parse(string));
                } catch (DateTimeParseException ignored) {
                    // 尝试解析为毫秒数
                    try {
                        return new Time(Long.parseLong(string));
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("无法将 [%s] 转换为时间类型".formatted(value));
                    }
                }
            } else if (value instanceof Date date) {
                return new Time(date.getTime());
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为时间类型".formatted(value));
        }
    }

    /**
     * 时间戳类型转换器
     */
    private static class SqlTimestampConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof Timestamp) {
                return value;
            } else if (value instanceof LocalDateTime time) {
                return Timestamp.valueOf(time);
            } else if (value instanceof LocalDate date) {
                return Timestamp.valueOf(date.atStartOfDay());
            } else if (value instanceof Date date) {
                return new Timestamp(date.getTime());
            } else if (value instanceof String string) {
                try {
                    // ISO日期时间格式
                    return Timestamp.valueOf(LocalDateTime.parse(string));
                } catch (DateTimeParseException ignored) {
                    // 尝试解析为毫秒数
                    try {
                        return new Timestamp(Long.parseLong(string));
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("无法将 [%s] 转换为时间戳类型".formatted(value));
                    }
                }
            } else if (value instanceof Long long1) {
                return new Timestamp(long1);
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为时间戳类型".formatted(value));
        }
    }

    /**
     * 本地日期转换器
     */
    private static class LocalDateConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof LocalDate) {
                return value;
            } else if (value instanceof LocalDateTime time) {
                return time.toLocalDate();
            } else if (value instanceof Date date) {
                return date.toLocalDate();
            } else if (value instanceof String string) {
                return LocalDate.parse(string);
            } else if (value instanceof Timestamp timestamp) {
                return timestamp.toLocalDateTime().toLocalDate();
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为本地日期类型".formatted(value));
        }
    }

    /**
     * 本地日期时间转换器
     */
    private static class LocalDateTimeConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof LocalDateTime) {
                return value;
            } else if (value instanceof Date date) {
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            } else if (value instanceof String string) {
                return LocalDateTime.parse(string);
            } else if (value instanceof Timestamp timestamp) {
                return timestamp.toLocalDateTime();
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为本地日期时间类型".formatted(value));
        }
    }

    /**
     * 本地时间转换器
     */
    private static class LocalTimeConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof LocalTime) {
                return value;
            } else if (value instanceof Date date) {
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            } else if (value instanceof String string) {
                return LocalTime.parse(string);
            } else if (value instanceof Timestamp timestamp) {
                return timestamp.toLocalDateTime().toLocalTime();
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为本地时间类型".formatted(value));
        }
    }

    /**
     * 带时区的日期时间转换器
     */
    private static class ZonedDateTimeConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof ZonedDateTime) {
                return value;
            } else if (value instanceof LocalDateTime time) {
                return time.atZone(ZoneId.systemDefault());
            } else if (value instanceof Date date) {
                return date.toInstant().atZone(ZoneId.systemDefault());
            } else if (value instanceof String string) {
                return ZonedDateTime.parse(string);
            } else if (value instanceof Timestamp timestamp) {
                return timestamp.toLocalDateTime().atZone(ZoneId.systemDefault());
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为带时区的日期时间类型".formatted(value));
        }
    }

    /**
     * UUID转换器
     */
    private static class UUIDConverter implements TypeConverter {
        @Override
        public Object convert(Object value) {
            if (value instanceof UUID) {
                return value;
            } else if (value instanceof String string) {
                return UUID.fromString(string);
            } else if (value instanceof byte[] bytes && bytes.length == 16) {
                long msb = 0;
                long lsb = 0;
                for (int i = 0; i < 8; i++) {
                    msb = (msb << 8) | (bytes[i] & 0xff);
                }
                for (int i = 8; i < 16; i++) {
                    lsb = (lsb << 8) | (bytes[i] & 0xff);
                }
                return new UUID(msb, lsb);
            }
            throw new IllegalArgumentException("无法将 [%s] 转换为UUID类型".formatted(value));
        }
    }

}
