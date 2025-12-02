package io.arkx.framework.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * 日期工具类
 *
 * @author Darkness
 * @date 2012-8-6 下午9:44:14
 * @version V1.0
 */
public class DateUtil {
    /**
     * 全局默认日期格式
     */
    public static final String Format_Date = "yyyy-MM-dd";

    /**
     * 全局默认时间格式
     */
    public static final String Format_Time = "HH:mm:ss";

    /**
     * 最后更新时间，通常用于http请求返回的header信息
     */
    public static final String Format_LastModified = "EEE, dd MMM yyyy HH:mm:ss";

    /**
     * 全局默认日期时间格式
     */
    public static final String Format_DateTime = "yyyy-MM-dd HH:mm:ss";

    private static ThreadLocal<Formats> formats = new ThreadLocal<Formats>();

    private static class Formats {
        private SimpleDateFormat DateOnly;
        private SimpleDateFormat TimeOnly;
        private SimpleDateFormat DateTime;
        private SimpleDateFormat LastModified;
        private HashMap<String, SimpleDateFormat> Others;
    }

    public static SimpleDateFormat getFormat(String format) {
        if (formats.get() == null) {
            formats.set(new Formats());
        }
        if (format.equals(Format_Date)) {
            return getDefaultDateFormat();
        }
        if (format.equals(Format_Time)) {
            return getDefaultTimeFormat();
        }
        if (format.equals(Format_DateTime)) {
            return getDefaultDateTimeFormat();
        }
        if (format.equals(Format_LastModified)) {
            return getLastModifiedFormat();
        }
        if (formats.get().Others == null) {
            formats.get().Others = new HashMap<String, SimpleDateFormat>();
        }
        if (!formats.get().Others.containsKey(format)) {
            formats.get().Others.put(format, new SimpleDateFormat(format, Locale.ENGLISH));
        }
        return formats.get().Others.get(format);
    }

    public static SimpleDateFormat getDefaultDateTimeFormat() {
        if (formats.get() == null) {
            formats.set(new Formats());
        }
        if (formats.get().DateTime == null) {
            formats.get().DateTime = new SimpleDateFormat(Format_DateTime, Locale.ENGLISH);
        }
        return formats.get().DateTime;
    }

    public static SimpleDateFormat getDefaultDateFormat() {
        if (formats.get() == null) {
            formats.set(new Formats());
        }
        if (formats.get().DateOnly == null) {
            formats.get().DateOnly = new SimpleDateFormat(Format_Date, Locale.ENGLISH);
        }
        return formats.get().DateOnly;
    }

    public static SimpleDateFormat getDefaultTimeFormat() {
        if (formats.get() == null) {
            formats.set(new Formats());
        }
        if (formats.get().TimeOnly == null) {
            formats.get().TimeOnly = new SimpleDateFormat(Format_Time, Locale.ENGLISH);
        }
        return formats.get().TimeOnly;
    }

    public static SimpleDateFormat getLastModifiedFormat() {
        if (formats.get() == null) {
            formats.set(new Formats());
        }
        if (formats.get().LastModified == null) {
            formats.get().LastModified = new SimpleDateFormat(Format_LastModified, Locale.ENGLISH);
        }
        return formats.get().LastModified;
    }

    /**
     * 得到以yyyy-MM-dd格式表示的当前日期字符串
     */
    public static String getCurrentDate() {
        return getDefaultDateFormat().format(new Date());
    }

    /**
     * 得到以format格式表示的当前日期字符串
     */
    public static String getCurrentDate(String format) {
        return getFormat(format).format(new Date());
    }

    /**
     * 得到以yyyy-MM-dd HH:mm:ss表示的当前时间字符串
     */
    public static String getCurrentDateTime() {
        return getDefaultDateTimeFormat().format(new Date());
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static String getCurrentTime(String format) {
        SimpleDateFormat t = new SimpleDateFormat(format);
        return t.format(new Date());
    }

    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    public static int getYearOfDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    public static int getDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        return cal.get(7);
    }

    public static int getDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(5);
    }

    /**
     * 指定日期是星期几
     *
     * @param date
     * @return
     */
    public static int getDayOfWeek(Date date) {// NO_UCD
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 指定日期是当月的第几天
     *
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date) {// NO_UCD
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取某一个月的天数
     *
     * @param date
     * @return
     */
    public static int getMaxDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DATE);
    }

    /**
     * 指定日期是当年的第几天
     *
     * @param date
     * @return
     */
    public static int getDayOfYear(Date date) {// NO_UCD
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 以指定的格式返回当前日期时间的字符串
     *
     * @param format
     * @return
     */
    public static String getCurrentDateTime(String format) {
        return getFormat(format).format(new Date());
    }

    /**
     * 以yyyy-MM-dd格式输出只带日期的字符串
     */
    public static String toString(Date date) {
        if (date == null) {
            return "";
        }
        return getDefaultDateFormat().format(date);
    }

    /**
     * 以yyyy-MM-dd HH:mm:ss输出带有日期和时间的字符串
     */
    public static String toDateTimeString(Date date) {
        if (date == null) {
            return "";
        }
        return getDefaultDateTimeFormat().format(date);
    }

    /**
     * 按指定的format输出日期字符串
     */
    public static String toString(Date date, String format) {
        if (date == null) {
            return "";
        }
        return getFormat(format).format(date);
    }

    public static String toTimeString(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    /**
     * 以yyyy-MM-dd解析两个字符串，并比较得到的两个日期的大小
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int compare(String date1, String date2) {
        return compare(date1, date2, Format_Date);
    }

    public static int compareTime(String time1, String time2) {
        return compareTime(time1, time2, "HH:mm:ss");
    }

    public static int compareTime(String time1, String time2, String format) {
        String[] arr1 = time1.split(":");
        String[] arr2 = time2.split(":");
        if (arr1.length < 2) {
            throw new RuntimeException("Invalid time:" + time1);
        }
        if (arr2.length < 2) {
            throw new RuntimeException("Invalid time:" + time2);
        }
        int h1 = Integer.parseInt(arr1[0]);
        int m1 = Integer.parseInt(arr1[1]);
        int h2 = Integer.parseInt(arr2[0]);
        int m2 = Integer.parseInt(arr2[1]);
        int s1 = 0;
        int s2 = 0;
        if (arr1.length == 3) {
            s1 = Integer.parseInt(arr1[2]);
        }
        if (arr2.length == 3) {
            s2 = Integer.parseInt(arr2[2]);
        }
        if ((h1 < 0) || (h1 > 23) || (m1 < 0) || (m1 > 59) || (s1 < 0) || (s1 > 59)) {
            throw new RuntimeException("Invalid time:" + time1);
        }
        if ((h2 < 0) || (h2 > 23) || (m2 < 0) || (m2 > 59) || (s2 < 0) || (s2 > 59)) {
            throw new RuntimeException("Invalid time:" + time2);
        }
        if (h1 != h2) {
            return h1 > h2 ? 1 : -1;
        }
        if (m1 == m2) {
            if (s1 == s2) {
                return 0;
            }
            return s1 > s2 ? 1 : -1;
        }

        return m1 > m2 ? 1 : -1;
    }
    /**
     * 以指定格式解析两个字符串，并比较得到的两个日期的大小
     *
     * @param date1
     * @param date2
     * @param format
     * @return
     */
    public static int compare(String date1, String date2, String format) {
        Date d1 = parse(date1, format);
        Date d2 = parse(date2, format);
        return d1.compareTo(d2);
    }

    /**
     * 判断指定的字符串是否符合HH:mm:ss格式，并判断其数值是否在正常范围
     *
     * @param time
     * @return
     */
    public static boolean isTime(String time) {
        String[] arr = time.split(":");
        if (arr.length < 2) {
            return false;
        }
        try {
            int h = Integer.parseInt(arr[0]);
            int m = Integer.parseInt(arr[1]);
            int s = 0;
            if (arr.length == 3) {
                s = Integer.parseInt(arr[2]);
            }
            if (h < 0 || h > 23 || m < 0 || m > 59 || s < 0 || s > 59) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 判断指定的字符串是否符合yyyy:MM:ss格式，但判断其数据值范围是否正常
     *
     * @param date
     * @return
     */
    public static boolean isDate(String date) {
        String[] arr = date.split("-");
        if (arr.length < 3) {
            return false;
        }
        try {
            int y = Integer.parseInt(arr[0]);
            int m = Integer.parseInt(arr[1]);
            int d = Integer.parseInt(arr[2]);
            if (y < 0 || m > 12 || m < 0 || d < 0 || d > 31) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否是日期或者带时间的日期，日期必须符合格式yy-MM-dd或yy-MM-dd HH:mm:ss
     */
    public static boolean isDateTime(String str) {
        if (StringUtil.isEmpty(str)) {
            return false;
        }
        if (str.endsWith(".0")) {
            str = str.substring(0, str.length() - 2);
        }
        if (str.indexOf(" ") > 0) {
            String[] arr = str.split(" ");
            if (arr.length == 2) {
                return isDate(arr[0]) && isTime(arr[1]);
            } else {
                return false;
            }
        } else {
            return isDate(str);
        }
    }

    /**
     * 判断指定日期是否是周末
     *
     * @param date
     * @return
     */
    public static boolean isWeekend(Date date) {// NO_UCD
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int t = cal.get(Calendar.DAY_OF_WEEK);
        if (t == Calendar.SATURDAY || t == Calendar.SUNDAY) {
            return true;
        }
        return false;
    }

    public static boolean isWeekend(String str) {
        return isWeekend(parse(str));
    }

    /**
     * 以yyyy-MM-dd解析指定字符串，返回相应java.util.Date对象
     *
     * @param str
     * @return
     */
    public static Date parse(String str) {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        try {
            return getDefaultDateFormat().parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 按指定格式解析字符串，并返回相应的java.util.Date对象
     *
     * @param str
     * @param format
     * @return
     */
    public static Date parse(String str, String format) {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        try {
            SimpleDateFormat t = new SimpleDateFormat(format);
            return t.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析http请求中的最后更新时间
     *
     * @param str
     * @return
     */
    public static Date parseLastModified(String str) {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        try {
            return getLastModifiedFormat().parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 以yyyy-MM-dd HH:mm:ss格式解析字符串，并返回相应的java.util.Date对象
     *
     * @param str
     * @return
     */
    public static Date parseDateTime(String str) {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        if (str.length() <= 10) {
            return parse(str);
        }
        try {
            return getDefaultDateTimeFormat().parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 以指定格式解析字符串，并返回相应的java.util.Date对象
     *
     * @param str
     * @param format
     * @return
     */
    public static Date parseDateTime(String str, String format) {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        try {
            SimpleDateFormat t = new SimpleDateFormat(format);
            return t.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 日期date上加count秒钟，count为负表示减
     */
    public static Date addSecond(Date date, int count) {
        return new Date(date.getTime() + 1000L * count);
    }

    /**
     * 日期date上加count分钟，count为负表示减
     */
    public static Date addMinute(Date date, int count) {// NO_UCD
        return new Date(date.getTime() + 60000L * count);
    }

    /**
     * 日期date上加count小时，count为负表示减
     */
    public static Date addHour(Date date, int count) {
        return new Date(date.getTime() + 3600000L * count);
    }

    /**
     * 日期date上加count天，count为负表示减
     */
    public static Date addDay(Date date, int count) {
        return new Date(date.getTime() + 86400000L * count);
    }

    /**
     * 日期date上加count星期，count为负表示减
     */
    public static Date addWeek(Date date, int count) {// NO_UCD
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.WEEK_OF_YEAR, count);
        return c.getTime();
    }

    /**
     * 日期date上加count月，count为负表示减
     */
    public static Date addMonth(Date date, int count) {
        /* ${_ARK_LICENSE_CODE_} */

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, count);
        return c.getTime();
    }

    /**
     * 日期date上加count年，count为负表示减
     */
    public static Date addYear(Date date, int count) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, count);
        return c.getTime();
    }

    /**
     * 将日期中的中文数字转化成阿拉伯数字，以例于使用指定格式解析
     */
    public static String convertChineseNumber(String strDate) {
        strDate = StringUtil.replaceEx(strDate, "一十一", "11");
        strDate = StringUtil.replaceEx(strDate, "一十二", "12");
        strDate = StringUtil.replaceEx(strDate, "一十三", "13");
        strDate = StringUtil.replaceEx(strDate, "一十四", "14");
        strDate = StringUtil.replaceEx(strDate, "一十五", "15");
        strDate = StringUtil.replaceEx(strDate, "一十六", "16");
        strDate = StringUtil.replaceEx(strDate, "一十七", "17");
        strDate = StringUtil.replaceEx(strDate, "一十八", "18");
        strDate = StringUtil.replaceEx(strDate, "一十九", "19");
        strDate = StringUtil.replaceEx(strDate, "二十一", "21");
        strDate = StringUtil.replaceEx(strDate, "二十二", "22");
        strDate = StringUtil.replaceEx(strDate, "二十三", "23");
        strDate = StringUtil.replaceEx(strDate, "二十四", "24");
        strDate = StringUtil.replaceEx(strDate, "二十五", "25");
        strDate = StringUtil.replaceEx(strDate, "二十六", "26");
        strDate = StringUtil.replaceEx(strDate, "二十七", "27");
        strDate = StringUtil.replaceEx(strDate, "二十八", "28");
        strDate = StringUtil.replaceEx(strDate, "二十九", "29");
        strDate = StringUtil.replaceEx(strDate, "十一", "11");
        strDate = StringUtil.replaceEx(strDate, "十二", "12");
        strDate = StringUtil.replaceEx(strDate, "十三", "13");
        strDate = StringUtil.replaceEx(strDate, "十四", "14");
        strDate = StringUtil.replaceEx(strDate, "十五", "15");
        strDate = StringUtil.replaceEx(strDate, "十六", "16");
        strDate = StringUtil.replaceEx(strDate, "十七", "17");
        strDate = StringUtil.replaceEx(strDate, "十八", "18");
        strDate = StringUtil.replaceEx(strDate, "十九", "19");
        strDate = StringUtil.replaceEx(strDate, "十", "10");
        strDate = StringUtil.replaceEx(strDate, "二十", "20");
        strDate = StringUtil.replaceEx(strDate, "三十", "20");
        strDate = StringUtil.replaceEx(strDate, "三十一", "31");
        strDate = StringUtil.replaceEx(strDate, "零", "0");
        strDate = StringUtil.replaceEx(strDate, "○", "0");
        strDate = StringUtil.replaceEx(strDate, "一", "1");
        strDate = StringUtil.replaceEx(strDate, "二", "2");
        strDate = StringUtil.replaceEx(strDate, "三", "3");
        strDate = StringUtil.replaceEx(strDate, "四", "4");
        strDate = StringUtil.replaceEx(strDate, "五", "5");
        strDate = StringUtil.replaceEx(strDate, "六", "6");
        strDate = StringUtil.replaceEx(strDate, "七", "7");
        strDate = StringUtil.replaceEx(strDate, "八", "8");
        strDate = StringUtil.replaceEx(strDate, "九", "9");
        return strDate;
    }

    /**
     * 时间差 - 年
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long diffYear(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int time = calendar.get(Calendar.YEAR);
        calendar.setTime(date2);
        return time - calendar.get(Calendar.YEAR);
    }

    /**
     * 时间差 - 季度
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long diffQuarter(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int time = calendar.get(Calendar.YEAR) * 4;
        calendar.setTime(date2);
        time -= calendar.get(Calendar.YEAR) * 4;
        calendar.setTime(date1);
        time += calendar.get(Calendar.MONTH) / 4;
        calendar.setTime(date2);
        return time - calendar.get(Calendar.MONTH) / 4;
    }

    /**
     * 时间差 - 月
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long diffMonth(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int time = calendar.get(Calendar.YEAR) * 12;
        calendar.setTime(date2);
        time -= calendar.get(Calendar.YEAR) * 12;
        calendar.setTime(date1);
        time += calendar.get(Calendar.MONTH);
        calendar.setTime(date2);
        return time - calendar.get(Calendar.MONTH);
    }

    /**
     * 时间差 - 星期
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long diffWeek(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();

        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        calendar.setTime(date1);
        int time = calendar.get(Calendar.YEAR) * 52;
        calendar.setTime(date2);
        time -= calendar.get(Calendar.YEAR) * 52;
        calendar.setTime(date1);
        time += calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(date2);
        return time - calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static Date clearHourMinuteSecond(Date date) {
        // 处理时间，建议用Calendar
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        // 设置当前时刻的时钟为0
        c1.set(Calendar.HOUR_OF_DAY, 0);
        // 设置当前时刻的分钟为0
        c1.set(Calendar.MINUTE, 0);
        // 设置当前时刻的秒钟为0
        c1.set(Calendar.SECOND, 0);
        // 设置当前的毫秒钟为0
        c1.set(Calendar.MILLISECOND, 0);
        return c1.getTime();
    }

    /**
     * 时间差 - 天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long diffDay(Date date1, Date date2) {

        long time = clearHourMinuteSecond(date1).getTime() / 1000 / 60 / 60 / 24;
        return time - clearHourMinuteSecond(date2).getTime() / 1000 / 60 / 60 / 24;
    }

    /**
     * 时间差 - 小时
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long diffHour(Date date1, Date date2) {
        long time = date1.getTime() / 1000 / 60 / 60;
        return time - date2.getTime() / 1000 / 60 / 60;
    }

    /**
     * 时间差 - 分钟
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long diffMinute(Date date1, Date date2) {
        long time = date1.getTime() / 1000 / 60;
        return time - date2.getTime() / 1000 / 60;
    }

    /**
     * 时间差 - 秒
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long diffSecond(Date date1, Date date2) {
        long time = date1.getTime() / 1000;
        return time - date2.getTime() / 1000;
    }

    public static int getDayOfYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(6);
    }

    public static int getDayOfWeek(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse(date));
        return cal.get(7);
    }

    public static int getDayOfMonth(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse(date));
        return cal.get(5);
    }

    public static int getDayOfYear(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse(date));
        return cal.get(6);
    }

    public static DateTimeFormatter DefaultDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        return DefaultDateTimeFormatter.format(localDateTime);
    }

    @Deprecated
    public static String getFirstDayOfMonth(String date) {
        return getFirstDayOfMonth(parse(date));
    }

    @Deprecated
    public static String getFirstDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, 1);
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    @Deprecated
    public static String getLastDayOfMonth(String date) {
        return getLastDayOfMonth(parse(date));
    }

    @Deprecated
    public static String getLastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(5, cal.getActualMaximum(5));
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    public static LocalDate firstDayOfWeek(LocalDate date) {
        TemporalAdjuster FIRST_OF_WEEK = TemporalAdjusters.ofDateAdjuster(
                localDate -> localDate.minusDays(localDate.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue()));
        return date.with(FIRST_OF_WEEK);
    }

    public static LocalDate lastDayOfWeek(LocalDate date) {
        TemporalAdjuster LAST_OF_WEEK = TemporalAdjusters.ofDateAdjuster(
                localDate -> localDate.plusDays(DayOfWeek.SUNDAY.getValue() - localDate.getDayOfWeek().getValue()));
        return date.with(LAST_OF_WEEK);
    }

    public static LocalDate firstDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate lastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate firstDayOfYear(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfYear());
    }

    public static LocalDate lastDayOfYear(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * localDate转Date
     */
    public static Date localDate2Date(LocalDate localDate) {
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        Instant instant1 = zonedDateTime.toInstant();
        Date from = Date.from(instant1);
        return from;
    }

    /**
     * Date 转 localDate
     */
    public static LocalDate date2LocalDate(Date date) {
        Instant instant = date.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        LocalDate localDate = zdt.toLocalDate();
        return localDate;
    }

    public static LocalDateTime startOfDay(LocalDate date) {
        LocalDate localDate = LocalDate.from(date);
        return localDate.atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
    }

    // 一天的结束
    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).plusDays(1L).minusNanos(1L).toLocalDateTime();
    }

    public static final DateTimeFormatter DFY_MD_HMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DFY_MD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * LocalDateTime 转时间戳
     *
     * @param localDateTime
     *            /
     * @return /
     */
    public static Long getTimeStamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * 时间戳转LocalDateTime
     *
     * @param timeStamp
     *            /
     * @return /
     */
    public static LocalDateTime fromTimeStamp(Long timeStamp) {
        return LocalDateTime.ofEpochSecond(timeStamp, 0, OffsetDateTime.now().getOffset());
    }

    /**
     * LocalDateTime 转 Date Jdk8 后 不推荐使用 {@link Date} Date
     *
     * @param localDateTime
     *            /
     * @return /
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDate 转 Date Jdk8 后 不推荐使用 {@link Date} Date
     *
     * @param localDate
     *            /
     * @return /
     */
    public static Date toDate(LocalDate localDate) {
        return toDate(localDate.atTime(LocalTime.now(ZoneId.systemDefault())));
    }

    /**
     * Date转 LocalDateTime Jdk8 后 不推荐使用 {@link Date} Date
     *
     * @param date
     *            /
     * @return /
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 日期 格式化
     *
     * @param localDateTime
     *            /
     * @param patten
     *            /
     * @return /
     */
    public static String localDateTimeFormat(LocalDateTime localDateTime, String patten) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(patten);
        return df.format(localDateTime);
    }

    /**
     * 日期 格式化
     *
     * @param localDateTime
     *            /
     * @param df
     *            /
     * @return /
     */
    public static String localDateTimeFormat(LocalDateTime localDateTime, DateTimeFormatter df) {
        return df.format(localDateTime);
    }

    /**
     * 日期格式化 yyyy-MM-dd HH:mm:ss
     *
     * @param localDateTime
     *            /
     * @return /
     */
    public static String localDateTimeFormatyMdHms(LocalDateTime localDateTime) {
        return DFY_MD_HMS.format(localDateTime);
    }

    /**
     * 日期格式化 yyyy-MM-dd
     *
     * @param localDateTime
     *            /
     * @return /
     */
    public String localDateTimeFormatyMd(LocalDateTime localDateTime) {
        return DFY_MD.format(localDateTime);
    }

    /**
     * 字符串转 LocalDateTime ，字符串格式 yyyy-MM-dd
     *
     * @param localDateTime
     *            /
     * @return /
     */
    public static LocalDateTime parseLocalDateTimeFormat(String localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.from(dateTimeFormatter.parse(localDateTime));
    }

    /**
     * 字符串转 LocalDateTime ，字符串格式 yyyy-MM-dd
     *
     * @param localDateTime
     *            /
     * @return /
     */
    public static LocalDateTime parseLocalDateTimeFormat(String localDateTime, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.from(dateTimeFormatter.parse(localDateTime));
    }

    /**
     * 字符串转 LocalDateTime ，字符串格式 yyyy-MM-dd HH:mm:ss
     *
     * @param localDateTime
     *            /
     * @return /
     */
    public static LocalDateTime parseLocalDateTimeFormatyMdHms(String localDateTime) {
        return LocalDateTime.from(DFY_MD_HMS.parse(localDateTime));
    }

}
