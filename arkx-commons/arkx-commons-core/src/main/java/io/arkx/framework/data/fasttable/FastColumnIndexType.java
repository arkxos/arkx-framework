package io.arkx.framework.data.fasttable;

/**
 *
 * @author Darkness
 * @date 2016年6月17日 下午1:24:04
 * @version V1.0
 */
public enum FastColumnIndexType {

    PrimaryKey((byte) 1, "主键"), Index((byte) 2, "索引"), Normal((byte) 3, "普通");

    private byte value;
    private String name;

    private FastColumnIndexType(byte value, String name) {
        this.name = name;
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static FastColumnIndexType valueOf(byte value) {
        for (FastColumnIndexType fastColumnIndexType : FastColumnIndexType.values()) {
            if (fastColumnIndexType.value == value) {
                return fastColumnIndexType;
            }
        }
        return null;
    }

}
