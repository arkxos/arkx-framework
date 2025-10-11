package io.arkx.framework.data.db.common.util;

import io.arkx.framework.data.db.common.entity.*;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataDumpCenter {

    @Setter
    private static Supplier<List<ColumnMappingEntity>> columnMappingDAO_getAllColumnMapping;
    @Setter
    private static Supplier<List<ColumnPlusEntity>> columnPlusDAO_getAllColumnPlus;

    @Setter
    private static Supplier<List<RawDictInfoEntity>> rawDictInfoDao_getAll;
    @Setter
    private static Supplier<List<DictMappingInfoEntity>> dictMappingInfoDao;
    @Setter
    private static Function<Integer, NewDictInfoEntity> newDictInfoDao_getDictById;

    public static Supplier<List<RawDictInfoEntity>> rawDictInfoDao_getAll() {
        return rawDictInfoDao_getAll;
    }

    public static Supplier<List<DictMappingInfoEntity>> dictMappingInfoDao() {
        return dictMappingInfoDao;
    }

    public static Function<Integer, NewDictInfoEntity> newDictInfoDao_getDictById() {
        return newDictInfoDao_getDictById;
    }
    public static Supplier<List<ColumnMappingEntity>> columnMappingDAO_getAllColumnMapping() {
        return columnMappingDAO_getAllColumnMapping;
    }
    public static Supplier<List<ColumnPlusEntity>> columnPlusDAO_getAllColumnPlus() {
        return columnPlusDAO_getAllColumnPlus;
    }

}
