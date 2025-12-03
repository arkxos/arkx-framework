// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.sr;

import io.arkx.framework.data.db.core.features.ProductFeatures;

public class StarrocksFeatures implements ProductFeatures {

    @Override
    public int convertFetchSize(int fetchSize) {
        return Integer.MIN_VALUE;
    }

}
