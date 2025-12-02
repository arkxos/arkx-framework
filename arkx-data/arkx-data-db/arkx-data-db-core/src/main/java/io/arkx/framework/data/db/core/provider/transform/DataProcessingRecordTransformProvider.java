package io.arkx.framework.data.db.core.provider.transform;

import java.util.List;

public class DataProcessingRecordTransformProvider implements RecordTransformProvider {

	private final RecordTransformProvider delegate;

	private final DataProcessor dataProcessor;

	public DataProcessingRecordTransformProvider(RecordTransformProvider delegate, DataProcessor dataProcessor) {
		this.delegate = delegate;
		this.dataProcessor = dataProcessor;
	}

	@Override
	public String getTransformerName() {
		return delegate.getTransformerName();
	}

	@Override
	public Object[] doTransform(String schemaName, String tableName, List<String> columns, Object[] record) {
		// 先执行原始转换
		Object[] originalResult = delegate.doTransform(schemaName, tableName, columns, record);

		// 再执行自定义数据加工
		return dataProcessor.process(schemaName, tableName, columns, originalResult);
	}

}
