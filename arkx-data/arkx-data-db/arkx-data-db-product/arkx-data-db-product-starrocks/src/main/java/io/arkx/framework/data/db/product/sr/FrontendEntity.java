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

import lombok.Data;

@Data
public class FrontendEntity {

	private String ip;

	private String httpport;

	private Boolean alive;

	private Boolean join;

	private String role;

}
