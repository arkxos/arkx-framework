package io.arkx.framework.data.db.product.postgresql.copy.pgsql.handlers;

import io.arkx.framework.data.db.product.postgresql.copy.pgsql.model.network.MacAddress;
import java.io.DataOutputStream;
import java.io.IOException;

public class MacAddressValueHandler extends BaseValueHandler<MacAddress> {

  @Override
  protected void internalHandle(DataOutputStream buffer, final MacAddress value)
      throws IOException {
    buffer.writeInt(6);
    buffer.write(value.getAddressBytes());
  }

  @Override
  public int getLength(MacAddress value) {
    return 6;
  }
}
