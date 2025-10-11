package io.arkx.framework.data.db.product.postgresql.copy.model;

import io.arkx.framework.data.db.product.postgresql.copy.util.PostgreSqlUtils;

public class TableDefinition {

  private final String schema;

  private final String tableName;

  public TableDefinition(String tableName) {
    this("", tableName);
  }

  public TableDefinition(String schema, String tableName) {
    this.schema = schema;
    this.tableName = tableName;
  }

  public String getSchema() {
    return schema;
  }

  public String getTableName() {
    return tableName;
  }

  public String GetFullyQualifiedTableName(boolean usePostgresQuoting) {
    return PostgreSqlUtils.getFullyQualifiedTableName(schema, tableName, usePostgresQuoting);
  }

  @Override
  public String toString() {
    return "TableDefinition (Schema = {%1$s}, TableName = {%2$s})".formatted(
        schema, tableName);
  }
}