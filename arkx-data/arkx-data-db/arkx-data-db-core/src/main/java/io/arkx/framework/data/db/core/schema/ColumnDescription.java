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

import io.arkx.framework.data.db.common.type.ProductTypeEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * 数据库列描述符信息定义(Column Description)
 *
 * @author tang
 */
@Getter
@Setter
public class ColumnDescription {

	private String fieldName;

	private String labelName;

	private String fieldTypeName;

	private String filedTypeClassName;

	private int fieldType;

	private int displaySize;

	private int scaleSize;

	private int precisionSize;

	private boolean autoIncrement;

	private boolean nullable;

	private String remarks;

	private boolean signed = false;

	private ProductTypeEnum productType;

	private String defaultValue;

	public String getFieldName() {
		if (null != this.fieldName) {
			return fieldName;
		}

		return this.labelName;
	}

	public String getLabelName() {
		if (null != labelName) {
			return labelName;
		}

		return this.fieldName;
	}

	public ColumnDescription copy() {
		ColumnDescription description = new ColumnDescription();
		description.setFieldName(fieldName);
		description.setLabelName(labelName);
		description.setFieldTypeName(fieldTypeName);
		description.setFiledTypeClassName(filedTypeClassName);
		description.setFieldType(fieldType);
		description.setDisplaySize(displaySize);
		description.setScaleSize(scaleSize);
		description.setPrecisionSize(precisionSize);
		description.setAutoIncrement(autoIncrement);
		description.setNullable(nullable);
		description.setRemarks(remarks);
		description.setSigned(signed);
		description.setProductType(productType);
		description.setDefaultValue(defaultValue);
		return description;
	}

	/// //////////////////////////////////////////

	public ColumnMetaData getMetaData() {
		return new ColumnMetaData(this);
	}

}
