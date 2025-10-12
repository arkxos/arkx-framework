package io.arkx.framework.data.db.core.util;


import io.arkx.framework.data.db.common.entity.RawDictInfoEntity;
import io.arkx.framework.data.db.common.entity.NewDictInfoEntity;
import io.arkx.framework.data.db.common.util.DataDumpCenter;

import java.util.*;
import java.util.stream.Collectors;

public class DictMappingUtils {

    private static Map<Integer, Map<String, RawDictInfoEntity>> Raw_MAP;
    private static Map<Integer, NewDictInfoEntity> DICT_MAPPING;

    public static void init() {

        // 加载旧系统字典
        Raw_MAP = DataDumpCenter.rawDictInfoDao_getAll().get().stream()
            .collect(
                // 根据parentId进行分组
                Collectors.groupingBy(
                    RawDictInfoEntity::getParentId,
                    // 使用使用oldValue作为key, 将分组的子字典列表转成map
                    Collectors.toMap(
                        RawDictInfoEntity::getOldValue,
                        entity -> entity
                    )
                )
            );

        // 新旧字典映射关系
        DICT_MAPPING = DataDumpCenter.dictMappingInfoDao().get()
            .stream()
            .map(mapping -> {
                // 通过映射关系查询yth字典
                NewDictInfoEntity newDict = DataDumpCenter.newDictInfoDao_getDictById().apply(mapping.getNewDictId());
                HashMap<Integer, NewDictInfoEntity> mapper = new HashMap<>();
                mapper.put(mapping.getRawDictId(), newDict);
                return mapper;
            })
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
    }

    public static NewDictInfoEntity getNewDict(Integer rawParentDictId, Object oldValue) {
        if ((!(oldValue instanceof String) && !(oldValue instanceof Number))) {
            return null;
        }
        Map<String, RawDictInfoEntity> stringRawDictInfoEntityMap = Raw_MAP.get(rawParentDictId);
        if (null == stringRawDictInfoEntityMap) {
            return null;
        }
        RawDictInfoEntity rawDictInfoEntity = stringRawDictInfoEntityMap.get(String.valueOf(oldValue));
        if (rawDictInfoEntity == null) {
            return null;
        }
        return DICT_MAPPING.get(rawDictInfoEntity.getId());
    }

    public static NewDictInfoEntity getNewDict(Integer dictId) {
        return DICT_MAPPING.get(dictId);
    }

    public static RawDictInfoEntity getRawDict(Integer parentDictId, Object oldValue) {
        if ((!(oldValue instanceof String) && !(oldValue instanceof Number))) {
            return null;
        }
        Map<String, RawDictInfoEntity> rawDictInfoEntityMap = Raw_MAP.get(parentDictId);
        if (null == rawDictInfoEntityMap) {
            return null;
        }
        return rawDictInfoEntityMap.get(String.valueOf(oldValue));
    }

}
