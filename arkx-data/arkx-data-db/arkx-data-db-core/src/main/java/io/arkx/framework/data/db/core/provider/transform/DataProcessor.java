package io.arkx.framework.data.db.core.provider.transform;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import io.arkx.framework.commons.util.ArkSpringContextHolder;
import io.arkx.framework.data.db.common.entity.ColumnMappingEntity;
import io.arkx.framework.data.db.common.entity.ColumnPlusEntity;
import io.arkx.framework.data.db.common.entity.NewDictInfoEntity;
import io.arkx.framework.data.db.common.entity.RawDictInfoEntity;
import io.arkx.framework.data.db.core.provider.transform.handlers.HandlerMap;
import io.arkx.framework.data.db.core.schema.ColumnDescription;
import io.arkx.framework.data.db.core.util.ColumHandler;
import io.arkx.framework.data.db.core.util.ColumnMappingUtils;
import io.arkx.framework.data.db.core.util.DictMappingUtils;
import io.arkx.framework.data.db.core.util.TypeHandler;
import io.arkx.framework.data.db.sdk.api.ColumnValueTransformer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataProcessor {

	private final List<ColumnDescription> sourceColumnDescriptions;

	private final List<ColumnDescription> targetColumnDescriptions;

	public DataProcessor(List<ColumnDescription> sourceColumnDescriptions,
			List<ColumnDescription> targetColumnDescriptions) {
		this.sourceColumnDescriptions = sourceColumnDescriptions;
		this.targetColumnDescriptions = targetColumnDescriptions;
	}

	/**
	 * 数据转换
	 */
	public Object[] process(String schemaName, String tableName, List<String> columns, Object[] originalResult) {
		if (CollectionUtils.isEmpty(sourceColumnDescriptions) || CollectionUtils.isEmpty(targetColumnDescriptions)
				|| ObjectUtils.isEmpty(originalResult)) { // originalResult
															// 有可能为空，不知道是不是bug
			return originalResult;
		}
		tableName = tableName.replace("_view", "");

		Object[] originalTemp = originalResult.clone();

		try {
			for (int i = 0; i < originalResult.length; i++) {
				int finalI = i;
				ColumnMappingEntity columnMappingEntity = ColumnMappingUtils.getColumnMappingEntity(schemaName,
						tableName, columns.get(i));
				if (columnMappingEntity == null) {
					continue;
				}

				// 替换原数据为null，但是配置了默认值的字段
				Optional.ofNullable(originalResult[i])
					.orElseGet(() -> originalResult[finalI] = columnMappingEntity.getDefaultValue());

				// 处理所有指定了处理器的字段
				execHandler(originalResult, columnMappingEntity, finalI);

				// 字典映射处理
				String finalTableName = tableName;
				Optional.ofNullable(ColumnMappingUtils.getColumnPlusEntity(columnMappingEntity.getId())).map(plus -> {
					if ("DICT_MAPPING".equals(plus.getHandlerCode())) { // 非级联字典或级联字典一级
						applyDictMapping(plus, originalResult, finalI);
					}
					else if ("DICT_SUB_NODES".equals(plus.getHandlerCode())) { // 级联子字典处理
						String[] split = plus.getValue().split(",");
						int index = columns.indexOf(split[0]);
						// 父级字段映射信息
						ColumnMappingEntity columnMappingEntity1 = ColumnMappingUtils.getColumnMappingEntity(schemaName,
								finalTableName, split[0]);
						if (columnMappingEntity1 == null) {
							return null;
						}
						// 父级字典映射附加信息（字典映射、默认值、以及子字典映射等）
						ColumnPlusEntity columnPlus = ColumnMappingUtils
							.getColumnPlusEntity(columnMappingEntity1.getId());
						if (columnPlus == null) {
							return null;
						}
						if (split.length == 1) { // 两集级联字典
							RawDictInfoEntity czDict = DictMappingUtils.getRawDict(columnPlus.getRawDictId(),
									originalTemp[index]);
							applyDictMapping(czDict, originalTemp, originalResult, finalI);
						}
						else { // 三级级联字典
							RawDictInfoEntity pzDict = DictMappingUtils.getRawDict(columnPlus.getRawDictId(),
									originalTemp[index]);
							int cIndex = columns.indexOf(split[1]);
							RawDictInfoEntity czDict = DictMappingUtils.getRawDict(pzDict.getId(),
									originalTemp[cIndex]);
							applyDictMapping(czDict, originalTemp, originalResult, finalI);
						}
					}
					return null;
				});

				List<ColumnValueTransformer> transformers = ArkSpringContextHolder
					.getBeansOfType(ColumnValueTransformer.class)
					.values()
					.stream()
					.toList();
				for (ColumnValueTransformer transformer : transformers) {
					transformer.transform(columns.get(i), originalResult, i);
				}
			}

			// 字段类型处理
			TypeHandler.typeMapping(sourceColumnDescriptions, targetColumnDescriptions, originalResult);

			return originalResult;
		}
		catch (Exception e) {
			log.error("process error：", e);
			return originalResult;
		}
	}

	private static void execHandler(Object[] originalResult, ColumnMappingEntity columnMappingEntity, int finalI) {
		if (StringUtils.isNotBlank(columnMappingEntity.getConverterType())) {
			String converterTypes = columnMappingEntity.getConverterType();
			String[] split = converterTypes.split(",");
			for (String s : split) {
				ColumHandler handler = HandlerMap.getHandlerWithName(s);
				handler.handle(originalResult, finalI);
			}
		}
	}

	private void applyDictMapping(ColumnPlusEntity columnPlus, Object[] originalResult, int i) {
		if (originalResult[i] == null || columnPlus == null) {
			return;
		}
		// 个别字典项是多选的，且多个字典值是“,”隔开的需要分开处理
		String[] originalValues = String.valueOf(originalResult[i]).split(",");
		String[] ythDictValues = new String[originalValues.length];

		for (int j = 0; j < originalValues.length; j++) {
			NewDictInfoEntity ythDict = DictMappingUtils.getNewDict(columnPlus.getRawDictId(), originalValues[j]);
			ythDictValues[j] = (ythDict != null) ? ythDict.getDictCode() : originalValues[j];
		}
		// 将处理完成的结果赋给原数据
		originalResult[i] = Arrays.stream(ythDictValues).filter(Objects::nonNull).collect(Collectors.joining(","));
	}

	private void applyDictMapping(RawDictInfoEntity czDict, Object[] temp, Object[] originalResult, int index) {
		if (temp[index] == null || czDict == null) {
			return;
		}
		NewDictInfoEntity ythDict = DictMappingUtils.getNewDict(czDict.getId(), temp[index]);
		originalResult[index] = (ythDict != null) ? ythDict.getDictCode() : temp[index];
	}

	private ColumHandler getHandlerWithName(String name) {
		return HandlerMap.getHandlerWithName(name);
	}

}
