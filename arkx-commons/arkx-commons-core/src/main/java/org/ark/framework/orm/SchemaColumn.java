package org.ark.framework.orm;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * @class org.ark.framework.orm.SchemaColumn
 * @author Darkness
 * @date 2012-3-8 下午1:59:40
 * @version V1.0
 */
@Getter
@Setter
public class SchemaColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    private int ColumnType;

    private String ColumnName;

    private int ColumnOrder;

    private int Length;

    private int Precision;

    private boolean Mandatory;//

    // 必填
    private boolean isPrimaryKey;

    private String memo;

    public SchemaColumn(String name, int type, int order, int length, int precision, boolean mandatory, boolean ispk) {
        this(name, type, order, length, precision, mandatory, ispk, "");
    }

    public SchemaColumn(String name, int type, int order, int length, int precision, boolean mandatory, boolean ispk,
            String memo) {
        this.ColumnType = type;
        this.ColumnName = name;
        this.ColumnOrder = order;
        this.Length = length;
        this.Precision = precision;
        this.Mandatory = mandatory;
        this.isPrimaryKey = ispk;
        this.memo = memo;
    }

    public String getColumnName() {
        return this.ColumnName;
    }

    public int getColumnOrder() {
        return this.ColumnOrder;
    }

    public int getColumnType() {
        return this.ColumnType;
    }

    public boolean isPrimaryKey() {
        return this.isPrimaryKey;
    }

    public int getLength() {
        return this.Length;
    }

    public int getPrecision() {
        return this.Precision;
    }

    public boolean isMandatory() {
        return this.Mandatory;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}
