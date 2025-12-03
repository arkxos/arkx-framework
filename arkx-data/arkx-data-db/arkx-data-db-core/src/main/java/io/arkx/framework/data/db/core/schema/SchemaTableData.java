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

/**
 * 数据库表的数据
 *
 * @author tang
 */
@Setter
@Getter
public class SchemaTableData {

    private String schemaName;

    private String tableName;

    private List<String> columns;

    private List<List<Object>> rows;

}
