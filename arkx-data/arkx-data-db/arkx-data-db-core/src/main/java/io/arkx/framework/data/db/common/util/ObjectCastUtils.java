package io.arkx.framework.data.db.common.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.*;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@UtilityClass
public final class ObjectCastUtils {

  /**
   * 将任意类型转换为java.lang.Byte类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Byte类型
   */
  public static Byte castToByte(final Object in) {
    if (in instanceof Number number) {
      return number.byteValue();
    } else if (in instanceof java.util.Date date) {
      return Long.valueOf(date.getTime()).byteValue();
    } else if (in instanceof String) {
      try {
        return Byte.parseByte(in.toString());
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.lang.String类型转换为java.lang.Byte类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof Character) {
      try {
        return Byte.parseByte(in.toString());
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.lang.Character类型转换为java.lang.Byte类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob clob) {
      try {
        String v = clob2Str(clob);
        return null == v ? null : Byte.parseByte(v);
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.sql.Clob类型转换为java.lang.Byte类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof Boolean boolean1) {
      return boolean1 ? (byte) 1 : (byte) 0;
    }

    return null;
  }


  public static byte[] castToByteArray(final Object in) {
    if (in instanceof byte[] bytes) {
      return bytes;
    } else if (in instanceof java.util.Date) {
      return in.toString().getBytes();
    } else if (in instanceof java.sql.Blob blob) {
      return blob2Bytes(blob);
    } else if (in instanceof java.lang.String || in instanceof java.lang.Character) {
      return in.toString().getBytes();
    } else if (in instanceof java.sql.Clob clob) {
      return clob2Str(clob).getBytes();
    } else {
      try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream(bos)) {
        oos.writeObject(in);
        oos.flush();
        return bos.toByteArray();
      } catch (Exception e) {
        log.error("Field value convert from {} to byte[] failed:", in.getClass().getName(), e);
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * 将任意类型转换为java.lang.Short类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Short类型
   */
  public static Short castToShort(final Object in) {
    if (in instanceof Number number) {
      return number.shortValue();
    } else if (in instanceof Byte byte1) {
      return (short) (byte1 & 0xff);
    } else if (in instanceof java.util.Date date) {
      return (short) date.getTime();
    } else if (in instanceof java.util.Calendar calendar) {
      return (short) calendar.getTime().getTime();
    } else if (in instanceof LocalDateTime time) {
      return (short) java.sql.Timestamp.valueOf(time).getTime();
    } else if (in instanceof java.time.OffsetDateTime time) {
      return (short) java.sql.Timestamp.valueOf(time.toLocalDateTime())
          .getTime();
    } else if (in instanceof String || in instanceof Character) {
      try {
        String s = in.toString().trim();
        if (s.equalsIgnoreCase("true")) {
          return Short.valueOf((short) 1);
        } else if (s.equalsIgnoreCase("false")) {
          return Short.valueOf((short) 0);
        } else {
          return Short.parseShort(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.lang.String类型转换为java.lang.Short类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob clob) {
      try {
        String s = clob2Str(clob);
        if (null == s) {
          return null;
        } else if (s.equalsIgnoreCase("true")) {
          return Short.valueOf((short) 1);
        } else if (s.equalsIgnoreCase("false")) {
          return Short.valueOf((short) 0);
        } else {
          return Short.parseShort(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.sql.Clob类型转换为java.lang.Short类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof Boolean boolean1) {
      return boolean1 ? (short) 1 : (short) 0;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.lang.Integer类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Integer类型
   */
  public static Integer castToInteger(final Object in) {
    if (in instanceof Number number) {
      return number.intValue();
    } else if (in instanceof Byte byte1) {
      return (byte1 & 0xff);
    } else if (in instanceof java.util.Date date) {
      return (int) date.getTime();
    } else if (in instanceof java.util.Calendar calendar) {
      return (int) calendar.getTime().getTime();
    } else if (in instanceof LocalDateTime time) {
      return (int) java.sql.Timestamp.valueOf(time).getTime();
    } else if (in instanceof java.time.OffsetDateTime time) {
      return (int) java.sql.Timestamp.valueOf(time.toLocalDateTime())
          .getTime();
    } else if (in instanceof String || in instanceof Character) {
      try {
        String s = in.toString().trim();
        if (s.equalsIgnoreCase("true")) {
          return Integer.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Integer.valueOf(0);
        } else {
          return Integer.parseInt(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.lang.String类型转换为java.lang.Integer类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob clob) {
      try {
        String s = clob2Str(clob);
        if (null == s) {
          return null;
        } else if (s.equalsIgnoreCase("true")) {
          return Integer.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Integer.valueOf(0);
        } else {
          return Integer.parseInt(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.sql.Clob类型转换为java.lang.Integer类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof Boolean boolean1) {
      return boolean1 ? (int) 1 : (int) 0;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.lang.Long类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Long类型
   */
  public static Long castToLong(final Object in) {
    if (in instanceof Number number) {
      return number.longValue();
    } else if (in instanceof Byte byte1) {
      return (long) (byte1 & 0xff);
    } else if (in instanceof java.util.Date date) {
      return date.getTime();
    } else if (in instanceof java.util.Calendar calendar) {
      return calendar.getTime().getTime();
    } else if (in instanceof LocalDateTime time) {
      return java.sql.Timestamp.valueOf(time).getTime();
    } else if (in instanceof java.time.OffsetDateTime time) {
      return java.sql.Timestamp.valueOf(time.toLocalDateTime())
          .getTime();
    } else if (in instanceof String || in instanceof Character) {
      try {
        String s = in.toString().trim();
        if (s.equalsIgnoreCase("true")) {
          return Long.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Long.valueOf(0);
        } else {
          return Long.parseLong(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.lang.String类型转换为java.lang.Long类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob clob) {
      try {
        String s = clob2Str(clob);
        if (null == s) {
          return null;
        } else if (s.equalsIgnoreCase("true")) {
          return Long.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Long.valueOf(0);
        } else {
          return Long.parseLong(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.sql.Clob类型转换为java.lang.Long类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof Boolean boolean1) {
      return boolean1 ? (long) 1 : (long) 0;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.lang.Number类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Number类型
   */
  public static Number castToNumeric(final Object in) {
    if (in instanceof Number number) {
      return number;
    } else if (in instanceof java.util.Date date) {
      return date.getTime();
    } else if (in instanceof java.util.Calendar calendar) {
      return calendar.getTime().getTime();
    } else if (in instanceof LocalDateTime time) {
      return java.sql.Timestamp.valueOf(time).getTime();
    } else if (in instanceof java.time.OffsetDateTime time) {
      return java.sql.Timestamp.valueOf(time.toLocalDateTime())
          .getTime();
    } else if (in instanceof String || in instanceof Character) {
      try {
        String s = in.toString().trim();
        if (s.equalsIgnoreCase("true")) {
          return Integer.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Integer.valueOf(0);
        } else {
          return new BigDecimal(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.lang.String类型转换为java.lang.Number类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob clob) {
      try {
        String s = clob2Str(clob);
        if (null == s) {
          return null;
        } else if (s.equalsIgnoreCase("true")) {
          return Integer.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Integer.valueOf(0);
        } else {
          return new BigDecimal(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.sql.Clob类型转换为java.lang.Number类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof Boolean boolean1) {
      return boolean1 ? (long) 1 : (long) 0;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.lang.Float类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Float类型
   */
  public static Float castToFloat(final Object in) {
    if (in instanceof Number number) {
      return number.floatValue();
    } else if (in instanceof java.util.Date date) {
      return (float) date.getTime();
    } else if (in instanceof java.util.Calendar calendar) {
      return (float) calendar.getTime().getTime();
    } else if (in instanceof LocalDateTime time) {
      return (float) java.sql.Timestamp.valueOf(time).getTime();
    } else if (in instanceof java.time.OffsetDateTime time) {
      return (float) java.sql.Timestamp.valueOf(time.toLocalDateTime())
          .getTime();
    } else if (in instanceof String || in instanceof Character) {
      try {
        String s = in.toString().trim();
        if (s.equalsIgnoreCase("true")) {
          return Float.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Float.valueOf(0);
        } else {
          return Float.parseFloat(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.lang.String类型转换为java.lang.Float类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob clob) {
      try {
        String s = clob2Str(clob);
        if (null == s) {
          return null;
        } else if (s.equalsIgnoreCase("true")) {
          return Float.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Float.valueOf(0);
        } else {
          return Float.parseFloat(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.sql.Clob类型转换为java.lang.Float类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof Boolean boolean1) {
      return boolean1 ? 1f : 0f;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.lang.Double类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Double类型
   */
  public static Double castToDouble(final Object in) {
    if (in instanceof Number number) {
      return number.doubleValue();
    } else if (in instanceof java.util.Date date) {
      return (double) date.getTime();
    } else if (in instanceof java.util.Calendar calendar) {
      return (double) calendar.getTime().getTime();
    } else if (in instanceof LocalDateTime time) {
      return (double) java.sql.Timestamp.valueOf(time).getTime();
    } else if (in instanceof java.time.OffsetDateTime time) {
      return (double) java.sql.Timestamp.valueOf(time.toLocalDateTime())
          .getTime();
    } else if (in instanceof String || in instanceof Character) {
      try {
        String s = in.toString().trim();
        if (s.equalsIgnoreCase("true")) {
          return Double.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Double.valueOf(0);
        } else {
          return Double.parseDouble(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将将java.lang.String类型转换为java.lang.Double类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob clob) {
      try {
        String s = clob2Str(clob);
        if (null == s) {
          return null;
        } else if (s.equalsIgnoreCase("true")) {
          return Double.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Double.valueOf(0);
        } else {
          return Double.parseDouble(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.sql.Clob类型转换为java.lang.Double类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof Boolean boolean1) {
      return boolean1 ? 1d : 0d;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.time.LocalDate类型
   *
   * @param in 任意类型的对象实例
   * @return java.time.LocalDate类型
   */
  public static LocalDate castToLocalDate(final Object in) {
    if (in instanceof java.sql.Time date) {
      LocalDate localDate = Instant.ofEpochMilli(date.getTime())
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
      return localDate;
    } else if (in instanceof java.sql.Timestamp t) {
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime.toLocalDate();
    } else if (in instanceof java.util.Date date) {
      LocalDate localDate = Instant.ofEpochMilli(date.getTime())
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
      return localDate;
    } else if (in instanceof java.util.Calendar calendar) {
      java.sql.Date date = new java.sql.Date(calendar.getTime().getTime());
      LocalDate localDate = Instant.ofEpochMilli(date.getTime())
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
      return localDate;
    } else if (in instanceof LocalDate date) {
      return date;
    } else if (in instanceof LocalTime) {
      return LocalDate.MIN;
    } else if (in instanceof LocalDateTime time) {
      return time.toLocalDate();
    } else if (in instanceof java.time.OffsetDateTime time) {
      return time.toLocalDate();
    } else if (in.getClass().getName().equals("oracle.sql.TIMESTAMP")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("timestampValue");
        java.sql.Timestamp date = (java.sql.Timestamp) m.invoke(in);
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in.getClass().getName().equals("microsoft.sql.DateTimeOffset")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("getTimestamp");
        java.sql.Timestamp t = (java.sql.Timestamp) m.invoke(in);
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return localDateTime.toLocalDate();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in instanceof String || in instanceof Character) {
      try {
        DateTime dt = DateUtil.parse(in.toString());
        LocalDate localDate = Instant.ofEpochMilli(dt.toSqlDate().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        return localDate;
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(
            "无法将java.lang.String类型转换为java.time.LocalDate类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob clob) {
      try {
        String v = clob2Str(clob);
        if (null == v) {
          return null;
        }
        DateTime dt = DateUtil.parse(in.toString());
        LocalDate localDate = Instant.ofEpochMilli(dt.toSqlDate().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        return localDate;
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.sql.Clob类型转换为java.time.LocalDate类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof Number number) {
      java.sql.Timestamp t = new java.sql.Timestamp(number.longValue());
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime.toLocalDate();
    }

    return null;
  }

  /**
   * 将任意类型转换为java.time.LocalTime类型
   *
   * @param in 任意类型的对象实例
   * @return java.time.LocalDate类型
   */
  public static LocalTime castToLocalTime(final Object in) {
    if (in instanceof java.sql.Time date) {
      LocalTime localTime = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault())
          .toLocalTime();
      return localTime;
    } else if (in instanceof java.sql.Timestamp t) {
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime.toLocalTime();
    } else if (in instanceof java.util.Date) {
      return LocalTime.of(0, 0, 0);
    } else if (in instanceof java.util.Calendar calendar) {
      java.sql.Date date = new java.sql.Date(calendar.getTime().getTime());
      LocalDateTime localDateTime = Instant.ofEpochMilli(date.getTime())
          .atZone(ZoneId.systemDefault())
          .toLocalDateTime();
      return localDateTime.toLocalTime();
    } else if (in instanceof LocalDate) {
      return LocalTime.of(0, 0, 0);
    } else if (in instanceof LocalTime time) {
      return time;
    } else if (in instanceof LocalDateTime time) {
      return time.toLocalTime();
    } else if (in instanceof java.time.OffsetDateTime time) {
      return time.toLocalTime();
    } else if (in.getClass().getName().equals("oracle.sql.TIMESTAMP")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("timestampValue");
        java.sql.Timestamp date = (java.sql.Timestamp) m.invoke(in);
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        return localDateTime.toLocalTime();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in.getClass().getName().equals("microsoft.sql.DateTimeOffset")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("getTimestamp");
        java.sql.Timestamp t = (java.sql.Timestamp) m.invoke(in);
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return localDateTime.toLocalTime();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in instanceof String || in instanceof Character) {
      try {
        DateTime dt = DateUtil.parse(in.toString());
        return LocalTime.ofSecondOfDay(dt.toSqlDate().getTime());
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(
            "无法将java.lang.String类型转换为java.sql.Time类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob clob) {
      try {
        String v = clob2Str(clob);
        if (null == v) {
          return null;
        }
        DateTime dt = DateUtil.parse(in.toString());
        return LocalTime.ofSecondOfDay(dt.toSqlDate().getTime());
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.sql.Clob类型转换为java.sql.Time类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof Number number) {
      java.sql.Timestamp t = new java.sql.Timestamp(number.longValue());
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime.toLocalTime();
    }

    return null;
  }

  /**
   * 将任意类型转换为java.time.LocalDateTime类型
   *
   * @param in 任意类型的对象实例
   * @return java.time.LocalDateTime类型
   */
  public static LocalDateTime castToLocalDateTime(final Object in) {
    if (in instanceof java.sql.Timestamp t) {
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime;
    } else if (in instanceof java.sql.Date date) {
      LocalDate localDate = date.toLocalDate();
      LocalTime localTime = LocalTime.of(0, 0, 0);
      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      return localDateTime;
    } else if (in instanceof java.sql.Time date) {
      java.sql.Timestamp t = new java.sql.Timestamp(date.getTime());
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime;
    } else if (in instanceof java.util.Date date) {
      java.sql.Timestamp t = new java.sql.Timestamp(date.getTime());
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime;
    } else if (in instanceof java.util.Calendar calendar) {
      java.sql.Timestamp t = new java.sql.Timestamp(calendar.getTime().getTime());
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime;
    } else if (in instanceof LocalDate localDate) {
      LocalTime localTime = LocalTime.of(0, 0, 0);
      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      return localDateTime;
    } else if (in instanceof LocalTime localTime) {
      LocalDate localDate = LocalDate.MIN;
      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      return localDateTime;
    } else if (in instanceof LocalDateTime time) {
      return time;
    } else if (in instanceof java.time.OffsetDateTime time) {
      return time.toLocalDateTime();
    } else if (in.getClass().getName().equals("oracle.sql.TIMESTAMP")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("timestampValue");
        java.sql.Timestamp t = (java.sql.Timestamp) m.invoke(in);
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return localDateTime;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in.getClass().getName().equals("microsoft.sql.DateTimeOffset")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("getTimestamp");
        java.sql.Timestamp t = (java.sql.Timestamp) m.invoke(in);
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return localDateTime;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in instanceof String || in instanceof Character) {
      try {
        DateTime dt = DateUtil.parse(in.toString());
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(dt.toTimestamp().toInstant(), ZoneId.systemDefault());
        return localDateTime;
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(
            "无法将java.lang.String类型转换为java.sql.TimeStamp类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob clob) {
      try {
        String v = clob2Str(clob);
        if (null == v) {
          return null;
        }
        java.sql.Timestamp t = java.sql.Timestamp.valueOf(v);
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return localDateTime;
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.sql.Clob类型转换为java.sql.TimeStamp类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof Number number) {
      java.sql.Timestamp t = new java.sql.Timestamp(number.longValue());
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.time.LocalDateTime类型
   *
   * @param in 任意类型的对象实例
   * @return java.sql.Timestamp类型
   */
  public static Timestamp castToTimestamp(final Object in) {
    if (in instanceof java.sql.Timestamp timestamp) {
      return timestamp;
    } else if (in instanceof java.sql.Date date) {
      LocalDate localDate = date.toLocalDate();
      LocalTime localTime = LocalTime.of(0, 0, 0);
      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      return Timestamp.valueOf(localDateTime);
    } else if (in instanceof java.sql.Time date) {
      return new java.sql.Timestamp(date.getTime());
    } else if (in instanceof java.util.Date date) {
      return new java.sql.Timestamp(date.getTime());
    } else if (in instanceof java.util.Calendar calendar) {
      return new java.sql.Timestamp(calendar.getTime().getTime());
    } else if (in instanceof LocalDate localDate) {
      LocalTime localTime = LocalTime.of(0, 0, 0);
      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      return Timestamp.valueOf(localDateTime);
    } else if (in instanceof LocalTime localTime) {
      LocalDate localDate = LocalDate.MIN;
      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      return Timestamp.valueOf(localDateTime);
    } else if (in instanceof LocalDateTime time) {
      return Timestamp.valueOf(time);
    } else if (in instanceof java.time.OffsetDateTime time) {
      return Timestamp.valueOf(time.toLocalDateTime());
    } else if (in.getClass().getName().equals("oracle.sql.TIMESTAMP")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("timestampValue");
        java.sql.Timestamp t = (java.sql.Timestamp) m.invoke(in);
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return Timestamp.valueOf(localDateTime);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in.getClass().getName().equals("microsoft.sql.DateTimeOffset")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("getTimestamp");
        java.sql.Timestamp t = (java.sql.Timestamp) m.invoke(in);
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return Timestamp.valueOf(localDateTime);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in instanceof String || in instanceof Character) {
      try {
        DateTime dt = DateUtil.parse(in.toString());
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(dt.toTimestamp().toInstant(), ZoneId.systemDefault());
        return Timestamp.valueOf(localDateTime);
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(
            "无法将java.lang.String类型转换为java.sql.TimeStamp类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob clob) {
      try {
        String v = clob2Str(clob);
        if (null == v) {
          return null;
        }
        java.sql.Timestamp t = java.sql.Timestamp.valueOf(v);
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return Timestamp.valueOf(localDateTime);
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.sql.Clob类型转换为java.sql.TimeStamp类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof Number number) {
      java.sql.Timestamp t = new java.sql.Timestamp(number.longValue());
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return Timestamp.valueOf(localDateTime);
    }

    return null;
  }

  /**
   * 将任意类型转换为Boolean类型
   *
   * @param in 任意类型的对象实例
   * @return Boolean类型
   */
  public static Boolean castToBoolean(final Object in) {
    if (in instanceof Boolean boolean1) {
      return boolean1;
    } else if (in instanceof Number number) {
      return number.intValue() != 0;
    } else if (in instanceof String || in instanceof Character) {
      try {
        return Boolean.parseBoolean(in.toString());
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(
            "无法将java.lang.String类型转换为java.lang.Boolean类型:%s".formatted(e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob clob) {
      try {
        String v = clob2Str(clob);
        return null == v ? null : Boolean.parseBoolean(v);
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            "无法将java.sql.Clob类型转换为java.lang.Boolean类型:%s".formatted(e.getMessage()));
      }
    }

    return null;
  }

  public static byte[] blob2Bytes(java.sql.Blob blob) {
    try (java.io.InputStream inputStream = blob.getBinaryStream()) {
      try (java.io.BufferedInputStream is = new java.io.BufferedInputStream(inputStream)) {
        byte[] bytes = new byte[(int) blob.length()];
        int len = bytes.length;
        int offset = 0;
        int read = 0;
        while (offset < len && (read = is.read(bytes, offset, len - offset)) >= 0) {
          offset += read;
        }
        return bytes;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String clob2Str(java.sql.Clob clob) {
    try (java.io.Reader is = clob.getCharacterStream()) {
      java.io.BufferedReader reader = new java.io.BufferedReader(is);
      StringBuffer sb = new StringBuffer();
      char[] buffer = new char[4096];
      for (int i = reader.read(buffer); i > 0; i = reader.read(buffer)) {
        sb.append(buffer, 0, i);
      }
      return sb.toString();
    } catch (SQLException | java.io.IOException e) {
      log.warn("Field Value convert from java.sql.Clob to java.lang.String failed:", e);
      return null;
    }
  }

  public static String castToString(final Object in) {
    if (in instanceof java.lang.Character) {
      return in.toString();
    } else if (in instanceof java.lang.String) {
      return in.toString();
    } else if (in instanceof java.lang.Character) {
      return in.toString();
    } else if (in instanceof java.sql.Clob clob) {
      return clob2Str(clob);
    } else if (in instanceof java.sql.Blob blob) {
      return Base64.encode(blob2Bytes(blob));
    } else if (in instanceof java.lang.Number) {
      return in.toString();
    } else if (in instanceof java.sql.RowId) {
      return in.toString();
    } else if (in instanceof java.lang.Boolean) {
      return in.toString();
    } else if (in instanceof java.util.Date) {
      return in.toString();
    } else if (in instanceof java.time.LocalDate) {
      return in.toString();
    } else if (in instanceof java.time.LocalTime) {
      return in.toString();
    } else if (in instanceof java.time.LocalDateTime) {
      return in.toString();
    } else if (in instanceof java.time.OffsetDateTime) {
      return in.toString();
    } else if (in instanceof java.sql.SQLXML) {
      return in.toString();
    } else if (in instanceof java.sql.Array) {
      return in.toString();
    } else if (in instanceof java.util.UUID) {
      return in.toString();
    } else if ("org.postgresql.util.PGobject".equals(in.getClass().getName())) {
      return in.toString();
    } else if ("org.postgresql.jdbc.PgSQLXML".equals(in.getClass().getName())) {
      try {
        Class<?> clz = in.getClass();
        Method getString = clz.getMethod("getString");
        return getString.invoke(in).toString();
      } catch (Exception e) {
        return "";
      }
    } else if (in.getClass().getName().equals("oracle.sql.INTERVALDS")) {
      return in.toString();
    } else if (in.getClass().getName().equals("oracle.sql.INTERVALYM")) {
      return in.toString();
    } else if (in.getClass().getName().equals("oracle.sql.TIMESTAMPLTZ")) {
      return in.toString();
    } else if (in.getClass().getName().equals("oracle.sql.TIMESTAMPTZ")) {
      return in.toString();
    } else if (in.getClass().getName().equals("oracle.sql.BFILE")) {
      Class<?> clz = in.getClass();
      try {
        Method methodFileExists = clz.getMethod("fileExists");
        boolean exists = (boolean) methodFileExists.invoke(in);
        if (!exists) {
          return "";
        }

        Method methodOpenFile = clz.getMethod("openFile");
        methodOpenFile.invoke(in);

        try {
          Method methodCharacterStreamValue = clz.getMethod("getBinaryStream");
          java.io.InputStream is = (java.io.InputStream) methodCharacterStreamValue.invoke(in);

          String line;
          StringBuilder sb = new StringBuilder();

          java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is));
          while ((line = br.readLine()) != null) {
            sb.append(line);
          }

          return sb.toString();
        } finally {
          Method methodCloseFile = clz.getMethod("closeFile");
          methodCloseFile.invoke(in);
        }
      } catch (java.lang.reflect.InvocationTargetException ex) {
        log.warn("Error for handle oracle.sql.BFILE: ", ex);
        return "";
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in.getClass().getName().equals("microsoft.sql.DateTimeOffset")) {
      return in.toString();
    } else if (in instanceof byte[] bytes) {
      return new String(bytes);
    } else if (in instanceof Map) {
      return JSON.toJSONString(in);
    } else if (in instanceof Collection) {
      return JSON.toJSONString(in);
    }

    return null != in ? in.toString() : null;
  }

  public static String objectToString(final Object in) {
    String v = in.toString();
    String a = in.getClass().getName() + "@" + Integer.toHexString(in.hashCode());
    if (a.length() == v.length() && StringUtils.equals(a, v)) {
      throw new UnsupportedOperationException("Unsupported convert "
          + in.getClass().getName() + " to java.lang.String");
    }

    return v;
  }

  public static Object castByJdbcType(int jdbcType, Object value) {
    switch (jdbcType) {
      case Types.BIT:
      case Types.TINYINT:
        return convert(value, ObjectCastUtils::castToByte);
      case Types.SMALLINT:
        return convert(value, ObjectCastUtils::castToShort);
      case Types.INTEGER:
        return convert(value, ObjectCastUtils::castToInteger);
      case Types.BIGINT:
        return convert(value, ObjectCastUtils::castToLong);
      case Types.NUMERIC:
      case Types.DECIMAL:
        return convert(value, ObjectCastUtils::castToNumeric);
      case Types.FLOAT:
      case Types.REAL:
        return convert(value, ObjectCastUtils::castToFloat);
      case Types.DOUBLE:
        return convert(value, ObjectCastUtils::castToDouble);
      case Types.BOOLEAN:
        return convert(value, ObjectCastUtils::castToBoolean);
      case Types.TIME:
        return convert(value, ObjectCastUtils::castToLocalTime);
      case Types.DATE:
        return convert(value, ObjectCastUtils::castToLocalDate);
      case Types.TIMESTAMP:
        return convert(value, ObjectCastUtils::castToTimestamp);
      case Types.BINARY:
      case Types.VARBINARY:
      case Types.BLOB:
      case Types.LONGVARBINARY:
        return convert(value, ObjectCastUtils::castToByteArray);
      case Types.CHAR:
      case Types.NCHAR:
      case Types.VARCHAR:
      case Types.LONGVARCHAR:
      case Types.NVARCHAR:
      case Types.LONGNVARCHAR:
      case Types.CLOB:
      case Types.NCLOB:
      case Types.NULL:
      case Types.OTHER:
      default:
        return convert(value, ObjectCastUtils::castToString);
    }
  }

  private static Object convert(Object value, Function<Object, Object> func) {
    try {
      return func.apply(value);
    } catch (Exception e) {
      return null;
    }
  }

  public static Object castByDetermine(final Object in) {
    if (null == in) {
      return null;
    }

    if (in instanceof BigInteger integer) {
      return integer.longValue();
    } else if (in instanceof BigDecimal decimal) {
      return decimal.doubleValue();
    } else if (in instanceof java.sql.Clob clob) {
      return clob2Str(clob);
    } else if (in instanceof java.sql.Array
        || in instanceof java.sql.SQLXML) {
      try {
        return objectToString(in);
      } catch (Exception e) {
        log.warn("Unsupported type for convert {} to java.lang.String", in.getClass().getName());
        return null;
      }
    } else if (in instanceof java.sql.Blob blob) {
      try {
        return blob2Bytes(blob);
      } catch (Exception e) {
        log.warn("Unsupported type for convert {} to byte[] ", in.getClass().getName());
        return null;
      }
    } else if (in instanceof java.sql.Struct) {
      log.warn("Unsupported type for convert {} to java.lang.String", in.getClass().getName());
      return null;
    }

    return in;
  }

}
