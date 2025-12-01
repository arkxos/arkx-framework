// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.greenplum;

import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.schema.SourceProperties;
import io.arkx.framework.data.db.product.postgresql.PostgresMetadataQueryProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;

@Slf4j
public class GreenplumMetadataQueryProvider extends PostgresMetadataQueryProvider {

  static {
    systemSchemas.add("pg_aoseg");
    systemSchemas.add("gp_toolkit");
  }

  public GreenplumMetadataQueryProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public void postAppendCreateTableSql(StringBuilder builder, String tblComment, List<String> primaryKeys,
      SourceProperties tblProperties) {
    List<String> distributed = determineDistributed(primaryKeys, tblProperties.getDistributedKeys());
    if (null == distributed) {
      return;
    }
    String dk = getPrimaryKeyAsString(distributed);
    builder.append("\n DISTRIBUTED BY (").append(dk).append(")");
  }

  private List<String> determineDistributed(List<String> primaryKeys, List<String> distributedKeys) {
    if (CollectionUtils.isEmpty(distributedKeys)) {
      // 分布键为空,看是否有主键
      return CollectionUtils.isEmpty(primaryKeys) ? null : primaryKeys;
    }
    // 分布键不为空,看是否是主键的子集,主键为空直接用分布键
    return CollectionUtils.isEmpty(primaryKeys) || new HashSet<>(primaryKeys).containsAll(distributedKeys)
        ? distributedKeys : null;
  }

}
