package io.arkx.framework.data.db.product.postgresql.copy.pgsql.handlers;

import io.arkx.framework.data.db.product.postgresql.copy.pgsql.handlers.utils.GeometricUtils;
import io.arkx.framework.data.db.product.postgresql.copy.pgsql.model.geometric.Point;

import java.io.DataOutputStream;
import java.io.IOException;

public class PointValueHandler extends BaseValueHandler<Point> {

  @Override
  protected void internalHandle(DataOutputStream buffer, final Point value) throws IOException {
    buffer.writeInt(16);

    GeometricUtils.writePoint(buffer, value);
  }

  @Override
  public int getLength(Point value) {
    return 16;
  }
}