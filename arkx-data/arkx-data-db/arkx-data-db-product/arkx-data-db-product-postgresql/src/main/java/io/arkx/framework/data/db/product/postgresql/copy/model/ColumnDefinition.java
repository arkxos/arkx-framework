package io.arkx.framework.data.db.product.postgresql.copy.model;

import io.arkx.framework.data.db.product.postgresql.copy.pgsql.PgBinaryWriter;
import java.util.function.BiConsumer;

public class ColumnDefinition<TEntity> {

  private final String columnName;

  private final BiConsumer<PgBinaryWriter, TEntity> write;

  public ColumnDefinition(String columnName, BiConsumer<PgBinaryWriter, TEntity> write) {
    this.columnName = columnName;
    this.write = write;
  }

  public String getColumnName() {
    return columnName;
  }

  public BiConsumer<PgBinaryWriter, TEntity> getWrite() {
    return write;
  }

  @Override
  public String toString() {
    return "ColumnDefinition (ColumnName = {%1$s}, Serialize = {%2$s})"
        .formatted(columnName, write);
  }
}