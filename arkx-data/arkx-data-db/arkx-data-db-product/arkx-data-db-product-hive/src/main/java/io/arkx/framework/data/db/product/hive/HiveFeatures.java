// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.hive;

import io.arkx.framework.data.db.core.features.DefaultProductFeatures;

public class HiveFeatures extends DefaultProductFeatures {

    /**
     * 是否使用CreateTableAs方式建表
     *
     * @return boolean
     */
    public boolean useCTAS() {
        return false;
    }
}
