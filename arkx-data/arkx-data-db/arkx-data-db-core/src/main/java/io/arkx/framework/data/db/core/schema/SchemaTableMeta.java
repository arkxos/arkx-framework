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

import java.util.List;

@Setter
@Getter
public class SchemaTableMeta extends TableDescription {

    private List<String> primaryKeys;
    private String createSql;
    private List<ColumnDescription> columns;
    private List<IndexDescription> indexes;

}
