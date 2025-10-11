package io.arkx.framework.data.db.product.gbase;

import io.arkx.framework.data.db.product.mysql.MysqlMetadataQueryProvider;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.schema.SourceProperties;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class GbaseMetadataQueryProvider extends MysqlMetadataQueryProvider {

  public GbaseMetadataQueryProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public void postAppendCreateTableSql(StringBuilder builder, String tblComment, List<String> primaryKeys,
      SourceProperties tblProperties) {
    builder.append("ENGINE=EXPRESS DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin");
    if (StringUtils.isNotBlank(tblComment)) {
      builder.append(" COMMENT='%s' ".formatted(tblComment.replace("'", "\\'")));
    }
  }
}
