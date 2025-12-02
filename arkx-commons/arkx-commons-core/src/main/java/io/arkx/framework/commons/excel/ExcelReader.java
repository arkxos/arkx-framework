package io.arkx.framework.commons.excel;

import java.util.List;
import java.util.Map;

/**
 * Excel数据读取工具类，POI实现，兼容Excel2003，及Excel2007
 *
 * @author Darkness
 * @date 2014-5-11 下午7:38:53
 * @version V1.0
 */
public class ExcelReader {

    public static final String SIMPLE = "SIMPLE";
    public static final String MERGE_WITH_VALUE = "MERGE_WITH_VALUE";

    public static Map<String, List<String[]>> read(String path) {
        return readExcel(path, MERGE_WITH_VALUE);
    }

    public static Map<String, List<String[]>> readExcel(String path, String policy) {
        if (SIMPLE.equals(policy)) {
            return new SimpleExcelReader(path).getAllData();
        }
        if (MERGE_WITH_VALUE.equals(policy)) {
            return new MergeWithValueExcelReader(path).getAllData();
        }
        throw new RuntimeException("policy [" + policy + "] not found, please check your code again...");
    }
}
