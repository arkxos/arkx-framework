package io.arkx.framework.data.db.core.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiFormatDateParser {
    // 正则表达式模式
    private static final Pattern PATTERN1 = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}-\\d{2}\\.\\d{2}\\.\\d{2}\\.\\d{6}$");

    private static final Pattern PATTERN2 = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[+-]\\d{1,2}$");

    // 格式1: "2023-01-30-09.25.10.000000"
    private static final DateTimeFormatter FORMAT1 = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.nnnnnn",
            Locale.ENGLISH);

    // 格式2: "2023-08-23 17:32:47+08"
    private static final DateTimeFormatter FORMAT2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX",
            Locale.ENGLISH);

    public static Timestamp parseToDate(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        String processedInput = input.trim();

        // 使用正则表达式匹配格式
        if (PATTERN1.matcher(processedInput).matches()) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(processedInput, FORMAT1);
                return Timestamp.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("无法解析日期字符串: " + input);
            }
        } else if (PATTERN2.matcher(processedInput).matches()) {
            try {
                ZonedDateTime zdt = ZonedDateTime.parse(processedInput, FORMAT2);
                return Timestamp.from(zdt.toInstant());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("无法解析日期字符串: " + input);
            }
        } else {
            throw new IllegalArgumentException("不支持的日期格式: " + input);
        }
    }

    public static String floatToVarchar(Double doubleValue) {
        if (doubleValue == null) {
            log.warn("floatValue is null");
            return String.valueOf(doubleValue);
        }
        return String.valueOf(doubleValue.longValue());
    }

    // 测试代码
    public static void main(String[] args) {
        /*
         * List<String> testDates = Arrays.asList( "2023-01-30-09.25.10.000000",
         * "2023-08-23 17:32:47+08" );
         *
         * for (String date : testDates) { try { Date parsedDate = parseToDate(date);
         * System.out.println("原始: " + date + " -> 转换后: " + parsedDate); } catch
         * (IllegalArgumentException e) { System.out.println("处理失败: " + date + " - " +
         * e.getMessage()); } }
         */
        // 方法1: 使用 String.valueOf()
        double f1 = 1.753961612206E12;
        String str1 = String.valueOf(f1);
        System.out.println("String.valueOf(): " + floatToVarchar(f1));
        System.out.println("String.valueOf(): " + "%E".formatted(f1));

    }
}
