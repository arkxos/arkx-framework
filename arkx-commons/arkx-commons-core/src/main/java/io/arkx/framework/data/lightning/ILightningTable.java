package io.arkx.framework.data.lightning;

/**
 *
 * @author Darkness
 * @date 2015年12月19日 下午5:12:47
 * @version V1.0
 * @since infinity 1.0
 */
public interface ILightningTable {

    String getTableName();

    LightningColumn[] getLightningColumns();

    int getRowCount();

    int getColumnCount();
}
