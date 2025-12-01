package io.arkx.framework.data.db.product.postgresql.copy.pgsql.handlers.utils;

import io.arkx.framework.data.db.product.postgresql.copy.pgsql.model.geometric.Point;

import java.io.DataOutputStream;
import java.io.IOException;

public class GeometricUtils {

  public static void writePoint(DataOutputStream buffer, final Point value) throws IOException {
    buffer.writeDouble(value.getX());
    buffer.writeDouble(value.getY());
  }

}
