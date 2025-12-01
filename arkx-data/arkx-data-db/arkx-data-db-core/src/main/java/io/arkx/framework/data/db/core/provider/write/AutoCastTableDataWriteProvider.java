package io.arkx.framework.data.db.core.provider.write;

import io.arkx.framework.data.db.common.util.ObjectCastUtils;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;

import java.util.List;

public class AutoCastTableDataWriteProvider extends DefaultTableDataWriteProvider {

  public AutoCastTableDataWriteProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public long write(List<String> fieldNames, List<Object[]> recordValues) {
    int[] argTypes = new int[fieldNames.size()];
    for (int i = 0; i < fieldNames.size(); ++i) {
      String col = fieldNames.get(i);
      argTypes[i] = this.columnType.get(col);
    }
    recordValues.parallelStream().forEach((Object[] row) -> {
      for (int i = 0; i < row.length; ++i) {
        row[i] = ObjectCastUtils.castByJdbcType(argTypes[i], row[i]);
      }
    });

    return super.write(fieldNames, recordValues);
  }

}
