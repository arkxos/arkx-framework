package com.arkxos.framework.data.lightning;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import com.arkxos.framework.commons.collection.TwoTuple;

/**
 * 
 * @author Darkness
 * @date 2015年12月19日 下午7:11:17
 * @version V1.0
 * @since infinity 1.0
 */
public interface ILightningTableConvertor<T extends ILightningTable> {

	T createTable(Class<T> clazz, String tableName, LightningColumn[] columns);

	void onRecordReaded(List<LightningColumn> columns, T dt, RecordUnit rowBuffer) throws IOException;

	TwoTuple<PkList, List<ByteBuffer>> buildRowByteBuffers(LightningColumn[] columns, T dt);

}
