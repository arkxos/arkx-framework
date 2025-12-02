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

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SchemaTableMeta extends TableDescription {

    private List<String> primaryKeys;
    private String createSql;
    private List<ColumnDescription> columns;
    private List<IndexDescription> indexes;
    private List<MetaColumn> metaColumns;

}
