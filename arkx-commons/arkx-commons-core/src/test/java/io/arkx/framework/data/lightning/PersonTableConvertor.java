package io.arkx.framework.data.lightning;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import io.arkx.framework.commons.collection.TwoTuple;

/**
 * 
 * @author Darkness
 * @date 2015年12月19日 下午5:22:32
 * @version V1.0
 * @since infinity 1.0
 */
public class PersonTableConvertor implements ILightningTableConvertor<PersonTable> {

	@Override
	public void onRecordReaded(List<LightningColumn> columns, PersonTable dt, RecordUnit rowBuffer) throws IOException {
	}

	@Override
	public TwoTuple<PkList, List<ByteBuffer>> buildRowByteBuffers(LightningColumn[] columns, PersonTable dt) {
		return null;
	}

	@Override
	public PersonTable createTable(Class<PersonTable> clazz, String tableName, LightningColumn[] columns) {
		return null;
	}

}
