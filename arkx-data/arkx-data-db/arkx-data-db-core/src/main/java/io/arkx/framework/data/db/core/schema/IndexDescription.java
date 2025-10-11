// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/// //////////////////////////////////////////////////////////
package io.arkx.framework.data.db.core.schema;

import lombok.Getter;
import lombok.Setter;
import io.arkx.framework.data.db.common.type.TableIndexEnum;

import java.util.List;

@Setter
@Getter
public class IndexDescription {

    private TableIndexEnum indexType;

    private String indexName;

    private List<IndexFieldMeta> indexFields;

    public IndexDescription(TableIndexEnum indexType, String indexName, List<IndexFieldMeta> indexFields) {
        this.indexType = indexType;
        this.indexName = indexName;
        this.indexFields = indexFields;
    }

}
