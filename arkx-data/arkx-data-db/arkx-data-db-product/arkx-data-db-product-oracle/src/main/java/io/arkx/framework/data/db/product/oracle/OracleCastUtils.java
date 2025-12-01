package io.arkx.framework.data.db.product.oracle;

import io.arkx.framework.data.db.common.util.JdbcTypesUtils;
import io.arkx.framework.data.db.common.util.ObjectCastUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.SqlTypeValue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@UtilityClass
public class OracleCastUtils {

  /**
   * 将java.sql.Array 类型转换为java.lang.String
   * <p>
   * Oracle 没有数组类型，这里以文本类型进行存储
   * <p>
   * Oracle的CLOB和BLOB类型写入请见：
   * <p>
   * oracle.jdbc.driver.OraclePreparedStatement.setObjectCritical
   */
  public static Object castByJdbcType(int jdbcType, Object value, List<InputStream> iss) {
    if (null == value) {
      return null;
    }

    try {
      switch (jdbcType) {
        case Types.NUMERIC:
          return new SqlTypeValue() {
            @Override
            public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType, String typeName)
                throws SQLException {
              if (value instanceof Byte byte1) {
                ps.setByte(paramIndex, byte1);
              } else if (value instanceof Short short1) {
                ps.setShort(paramIndex, short1);
              } else if (value instanceof Integer integer) {
                ps.setInt(paramIndex, integer);
              } else if (value instanceof BigInteger integer) {
                ps.setInt(paramIndex, integer.intValue());
              } else if (value instanceof Long long1) {
                ps.setLong(paramIndex, long1);
              } else if (value instanceof Float float1) {
                ps.setFloat(paramIndex, float1);
              } else if (value instanceof Double double1) {
                ps.setDouble(paramIndex, double1);
              } else if (value instanceof BigDecimal decimal) {
                ps.setBigDecimal(paramIndex, decimal);
              } else {
                ps.setObject(paramIndex, value, sqlType);
              }
            }
          };
        case Types.TIME:
          return new SqlTypeValue() {
            @Override
            public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType, String typeName)
                throws SQLException {
              if (value instanceof Time time) {
                ps.setTime(paramIndex, time);
              } else {
                LocalTime time = ObjectCastUtils.castToLocalTime(value);
                if (null == time) {
                  ps.setNull(paramIndex, jdbcType);
                } else {
                  ps.setTime(paramIndex, Time.valueOf(time));
                }
              }
            }
          };
        case Types.DATE:
          return new SqlTypeValue() {
            @Override
            public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType, String typeName)
                throws SQLException {
              if (value instanceof Date date) {
                ps.setDate(paramIndex, date);
              } else {
                LocalDate date = ObjectCastUtils.castToLocalDate(value);
                if (null == date) {
                  ps.setNull(paramIndex, jdbcType);
                } else {
                  ps.setDate(paramIndex, Date.valueOf(date));
                }
              }
            }
          };
        case Types.TIMESTAMP:
          return new SqlTypeValue() {
            @Override
            public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType, String typeName)
                throws SQLException {
              if (value instanceof Timestamp timestamp) {
                ps.setTimestamp(paramIndex, timestamp);
              } else {
                LocalDateTime dateTime = ObjectCastUtils.castToLocalDateTime(value);
                if (null == dateTime) {
                  ps.setNull(paramIndex, jdbcType);
                } else {
                  ps.setTimestamp(paramIndex, Timestamp.valueOf(dateTime));
                }
              }
            }
          };
        case Types.BLOB:
          return new SqlTypeValue() {
            @Override
            public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType, String typeName)
                throws SQLException {
              if (value instanceof java.sql.Blob blob) {
                ps.setBlob(paramIndex, blob);
              } else {
                InputStream is = new ByteArrayInputStream(ObjectCastUtils.castToByteArray(value));
                ps.setBlob(paramIndex, is);
                iss.add(is);
              }
            }
          };
        case Types.CLOB:
          return new SqlTypeValue() {
            @Override
            public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType, String typeName)
                throws SQLException {
              if (value instanceof java.sql.Clob clob) {
                ps.setClob(paramIndex, clob);
              } else {
                java.io.Reader reader = new StringReader(ObjectCastUtils.castToString(value));
                ps.setClob(paramIndex, reader);
              }
            }
          };
        case Types.NCLOB:
          return new SqlTypeValue() {
            @Override
            public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType, String typeName)
                throws SQLException {
              if (value instanceof java.sql.NClob clob) {
                ps.setNClob(paramIndex, clob);
              } else {
                java.io.Reader reader = new StringReader(ObjectCastUtils.castToString(value));
                ps.setNClob(paramIndex, reader);
              }
            }
          };
        default:
          return ObjectCastUtils.castByJdbcType(jdbcType, value);
      }
    } catch (
        Exception e) {
      log.warn("Convert from {} to Oracle {} failed: {}",
          value.getClass().getName(),
          JdbcTypesUtils.resolveTypeName(jdbcType),
          e.getMessage()
      );
      return null;
    }
  }

}
