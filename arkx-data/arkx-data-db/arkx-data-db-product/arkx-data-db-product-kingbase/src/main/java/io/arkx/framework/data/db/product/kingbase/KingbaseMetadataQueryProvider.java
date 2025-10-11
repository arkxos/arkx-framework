// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.kingbase;

import io.arkx.framework.data.db.common.consts.Constants;
import io.arkx.framework.data.db.product.postgresql.PostgresUtils;
import io.arkx.framework.data.db.product.postgresql.PostgresqlConst;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.meta.AbstractMetadataProvider;
import io.arkx.framework.data.db.core.schema.ColumnDescription;
import io.arkx.framework.data.db.core.schema.ColumnMetaData;
import io.arkx.framework.data.db.core.schema.TableDescription;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class KingbaseMetadataQueryProvider extends AbstractMetadataProvider {

  private static final String SHOW_CREATE_VIEW_SQL =
      """
      SELECT pg_get_viewdef((select pg_class.oid from pg_catalog.pg_class\s
      join pg_catalog.pg_namespace on pg_class.relnamespace = pg_namespace.oid\s
      where pg_namespace.nspname='%s' and pg_class.relname ='%s'),true) """;

  public KingbaseMetadataQueryProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public String getTableDDL(Connection connection, String schemaName, String tableName) {
    String sql = PostgresqlConst.CREATE_TABLE_SQL_TPL
        .replace(PostgresqlConst.TPL_KEY_SCHEMA, schemaName)
        .replace(PostgresqlConst.TPL_KEY_TABLE, tableName);
    try (Statement st = connection.createStatement()) {
      if (st.execute(sql)) {
        try (ResultSet rs = st.getResultSet()) {
          if (rs != null && rs.next()) {
            return rs.getString(1);
          }
        }
      }
    } catch (SQLException e) {
      //throw new RuntimeException(e);
    }

    return PostgresUtils.getTableDDL(this, connection, schemaName, tableName);
  }

  @Override
  public String getViewDDL(Connection connection, String schemaName, String tableName) {
    String sql = SHOW_CREATE_VIEW_SQL.formatted(schemaName, tableName);
    try (Statement st = connection.createStatement()) {
      if (st.execute(sql)) {
        try (ResultSet rs = st.getResultSet()) {
          if (rs != null && rs.next()) {
            return rs.getString(1);
          }
        }
      }
    } catch (SQLException e) {
      // throw new RuntimeException(e);
    }

    return null;
  }

  @Override
  public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
    String querySQL = " %s LIMIT 0 ".formatted(sql.replace(";", ""));
    return this.getSelectSqlColumnMeta(connection, querySQL);
  }

  @Override
  public void testQuerySQL(Connection connection, String sql) {
    String testQuerySql = "explain %s".formatted(sql.replace(";", ""));
    if (log.isDebugEnabled()) {
      log.debug("Execute sql :{}", testQuerySql);
    }
    try (Statement st = connection.createStatement()) {
      st.execute(testQuerySql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc,
      boolean addCr, boolean withRemarks) {
    String fieldname = v.getName();
    int length = v.getLength();
    int precision = v.getPrecision();
    int type = v.getType();

    String retval = " \"" + fieldname + "\"   ";

    switch (type) {
      case ColumnMetaData.TYPE_TIMESTAMP:
        retval += "TIMESTAMP";
        break;
      case ColumnMetaData.TYPE_TIME:
        retval += "TIME";
        break;
      case ColumnMetaData.TYPE_DATE:
        retval += "DATE";
        break;
      case ColumnMetaData.TYPE_BOOLEAN:
        retval += "BOOLEAN";
        break;
      case ColumnMetaData.TYPE_NUMBER:
      case ColumnMetaData.TYPE_INTEGER:
      case ColumnMetaData.TYPE_BIGNUMBER:
        if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
          if (useAutoInc) {
            retval += "BIGSERIAL";
          } else {
            retval += "BIGINT";
          }
        } else {
          if (length > 0) {
            if (precision > 0 || length > 18) {
              if ((length + precision) > 0 && precision > 0) {
                // Numeric(Precision, Scale): Precision = total length; Scale = decimal places
                retval += "NUMERIC(" + (length + precision) + ", " + precision + ")";
              } else {
                retval += "DOUBLE PRECISION";
              }
            } else {
              if (length > 9) {
                retval += "BIGINT";
              } else {
                if (length < 5) {
                  retval += "SMALLINT";
                } else {
                  retval += "INTEGER";
                }
              }
            }

          } else {
            retval += "DOUBLE PRECISION";
          }
        }
        break;
      case ColumnMetaData.TYPE_STRING:
        // 人大金仓varchar最大长度
        if (length < 1 || length >=  1048_5760) {
          retval += "TEXT";
        } else {
          retval += "VARCHAR(" + length + ")";
        }
        break;
      case ColumnMetaData.TYPE_BINARY:
        retval += "BYTEA";
        break;
      default:
        retval += "TEXT";
        break;
    }

    if (addCr) {
      retval += Constants.CR;
    }

    return retval;
  }

  @Override
  public List<String> getTableColumnCommentDefinition(TableDescription td,
      List<ColumnDescription> cds) {
    List<String> results = new ArrayList<>();
    if (StringUtils.isNotBlank(td.getRemarks())) {
      results.add("COMMENT ON TABLE \"%s\".\"%s\" IS '%s' "
          .formatted(
              td.getSchemaName(), td.getTableName(),
              td.getRemarks().replace("'", "")));
    }

    for (ColumnDescription cd : cds) {
      if (StringUtils.isNotBlank(cd.getRemarks())) {
        results.add("COMMENT ON COLUMN \"%s\".\"%s\".\"%s\" IS '%s' "
            .formatted(
                td.getSchemaName(), td.getTableName(), cd.getFieldName(),
                cd.getRemarks().replace("'", "")));
      }
    }

    return results;
  }

}
