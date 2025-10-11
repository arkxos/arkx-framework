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

@Getter
@Setter
public class IndexFieldMeta {

    private String fieldName;
    private Integer ordinalPosition;
    private Boolean isAscOrder;

    public IndexFieldMeta(String fieldName, Integer ordinalPosition, Boolean isAscOrder) {
        this.fieldName = fieldName;
        this.ordinalPosition = ordinalPosition;
        this.isAscOrder = isAscOrder;
    }

    public Boolean getAscOrder() {
        return isAscOrder;
    }

    public void setAscOrder(Boolean ascOrder) {
        isAscOrder = ascOrder;
    }
}
