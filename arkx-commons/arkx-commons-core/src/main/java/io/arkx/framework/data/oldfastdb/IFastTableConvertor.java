package io.arkx.framework.data.oldfastdb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.ThreeTuple;
import io.arkx.framework.data.fasttable.FastColumn;

import com.google.common.collect.Multimap;

/**
 * @author Darkness
 * @date 2015年12月19日 下午7:11:17
 * @version V1.0
 * @since infinity 1.0
 */
public interface IFastTableConvertor<T extends IFastTable> {

	T createTable(Class<T> clazz, String tableName, FastColumn[] columns);

	void onRecordReaded(List<FastColumn> columns, T dt, RecordUnit rowBuffer) throws IOException;

	ThreeTuple<PkList, List<ByteBuffer>, Map<String, Multimap<Object, Integer>>> buildRowByteBuffers(int existRowCount,
			List<FastColumn> columns, DataRow[] rows, int typeLength);

}
