package org.ark.framework.orm;

import io.arkx.framework.commons.util.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * 从导出的数据库文件中读取SchemaSet数据，支持读取后直接处理，无需导入数据库
 *
 * @author Darkness
 * @date 2023-06-01
 * @version V1.0
 */
@Slf4j
public class SchemaSetImporter {

    /**
     * 导入结果类，包含从文件中加载的SchemaSet对象和导入统计信息
     */
    @Getter
    public class ImportResult {
        // 所有读取到的SchemaSet，不合并
        private final List<SchemaSet<?>> schemaSets = new ArrayList<>();
        // 表名到SchemaSet的多值映射，一个表名可能对应多个SchemaSet
        private final Map<String, List<SchemaSet<?>>> tableSchemaMap = new HashMap<>();
        private int totalRecords = 0;
        private int totalTables = 0;

        /**
         * 添加一个SchemaSet到结果集中，不进行合并
         */
        public void addSchemaSet(SchemaSet<?> schemaSet) {
            if (schemaSet == null) {
                return;
            }

            // 添加到列表中
            schemaSets.add(schemaSet);

            // 记录到表映射中
            String tableCode = schemaSet.getSchema().getTableCode();
            String schemaClassName = schemaSet.getSchema().getClass().getName();

            List<SchemaSet<?>> tableSchemaSets = tableSchemaMap.computeIfAbsent(tableCode, k -> new ArrayList<>());
            tableSchemaSets.add(schemaSet);

            // 更新统计信息
            totalRecords += schemaSet.size();

            // 只有第一次添加表时才增加表计数
            if (tableSchemaSets.size() == 1) {
                totalTables++;
                log.debug("添加新表 {}, 类型: {}, 记录数: {}", tableCode, schemaClassName, schemaSet.size());
            } else {
                log.debug("添加表 {} 的另一个SchemaSet, 类型: {}, 当前分片记录数: {}",
                        tableCode, schemaClassName, schemaSet.size());
            }
        }

        /**
         * 根据表名获取对应的SchemaSet列表
         */
        public List<SchemaSet<?>> getSchemaSetsByTableCode(String tableCode) {
            return tableSchemaMap.getOrDefault(tableCode, Collections.emptyList());
        }

        /**
         * 根据表名获取对应表的总记录数
         */
        public int getTableTotalRecords(String tableCode) {
            List<SchemaSet<?>> sets = tableSchemaMap.get(tableCode);
            if (sets == null) {
                return 0;
            }

            int total = 0;
            for (SchemaSet<?> set : sets) {
                total += set.size();
            }
            return total;
        }

        /**
         * 重新计算总记录数
         */
        public void recalculateTotalRecords() {
            totalRecords = 0;
            for (SchemaSet<?> set : schemaSets) {
                totalRecords += set.size();
            }
            log.debug("重新计算总记录数完成，总计: {}", totalRecords);
        }
    }

    /**
     * 从文件中导入SchemaSet数据
     *
     * @param filePath 数据文件路径
     * @return 包含所有SchemaSet的导入结果对象
     */
    public ImportResult importFromFile(String filePath) {
        ImportResult result = new ImportResult();

        log.info("开始从文件 {} 导入SchemaSet数据", filePath);

        try (BufferedRandomAccessFile braf = new BufferedRandomAccessFile(filePath, "r")) {
            long fileLength = braf.length();
            int pageIndex = 1;
            Map<String, Integer> tablePageCounts = new HashMap<>();

            while (braf.getFilePointer() < fileLength) {
                SchemaSet<?> schemaSet = readSchemaSetFromFile(braf, pageIndex++);

                if (schemaSet != null) {
                    String tableCode = schemaSet.getSchema().getTableCode();
                    String schemaClassName = schemaSet.getSchema().getClass().getName();

                    // 记录每个表的页数
                    tablePageCounts.put(tableCode, tablePageCounts.getOrDefault(tableCode, 0) + 1);

                    try {
                        result.addSchemaSet(schemaSet);
                        log.debug("已读取表 {} (类型: {}) 的SchemaSet数据第 {} 页，包含 {} 条记录",
                                tableCode, schemaClassName, tablePageCounts.get(tableCode), schemaSet.size());
                    } catch (Exception e) {
                        log.error("添加表 {} 的SchemaSet失败: {}", tableCode, e.getMessage(), e);
                    }
                }
            }

            // 确保总记录数计算正确
            result.recalculateTotalRecords();

            // 统计每个表的页数和记录总数
            for (Map.Entry<String, Integer> entry : tablePageCounts.entrySet()) {
                String tableCode = entry.getKey();
                int pageCount = entry.getValue();
                int totalRecords = result.getTableTotalRecords(tableCode);

                log.info("表 {} 共有 {} 页数据，总记录数: {}",
                        tableCode, pageCount, totalRecords);
            }

            log.info("从文件中读取数据完成，共 {} 个表，{} 条记录",
                    result.getTotalTables(), result.getTotalRecords());

        } catch (Exception e) {
            log.error("从文件 {} 读取数据失败: {}", filePath, e.getMessage(), e);
        }

        return result;
    }

    /**
     * 从文件中读取单个SchemaSet对象
     *
     * @param braf 随机访问文件
     * @param pageIndex 页码索引（用于日志）
     * @return SchemaSet对象，如果读取失败则返回null
     */
    private SchemaSet<?> readSchemaSetFromFile(BufferedRandomAccessFile braf, int pageIndex) throws IOException {
        try {
            // 读取schema名称长度和schema名称
            byte[] bs = new byte[4];
            braf.read(bs);
            int schemaNameLength = NumberUtil.toInt(bs);

            bs = new byte[schemaNameLength];
            braf.read(bs);
            String schemaName = new String(bs);

            // 读取SchemaSet数据长度和数据
            bs = new byte[4];
            braf.read(bs);
            int dataLength = NumberUtil.toInt(bs);

            bs = new byte[dataLength];
            braf.read(bs);

            int pageByteLength = 4 + schemaNameLength + 4 + dataLength;
            log.debug("第 {} 页: 数据长度: {} 字节, schema: {}", pageIndex, pageByteLength, schemaName);

            // 解压和反序列化SchemaSet
            byte[] unzippedData = ZipUtil.unzip(bs);
            Object obj = FileUtil.unserialize(unzippedData);

            if (obj instanceof SchemaSet) {
                return (SchemaSet<?>) obj;
            }

            return null;
        } catch (Exception e) {
            log.warn("读取第 {} 页的SchemaSet数据失败: {}", pageIndex, e.getMessage());
            return null;
        }
    }

    /**
     * 从文件中获取指定表的所有SchemaSet数据
     *
     * @param filePath 数据文件路径
     * @param tableCode 表名
     * @return 表对应的SchemaSet对象列表，如果未找到则返回空列表
     */
    public List<SchemaSet<?>> getSchemaSetsByTableCode(String filePath, String tableCode) {
        List<SchemaSet<?>> result = new ArrayList<>();

        try (BufferedRandomAccessFile braf = new BufferedRandomAccessFile(filePath, "r")) {
            long fileLength = braf.length();
            int pageCount = 0;

            while (braf.getFilePointer() < fileLength) {
                SchemaSet<?> schemaSet = readSchemaSetFromFile(braf, 0);

                if (schemaSet != null && schemaSet.getSchema().getTableCode().equals(tableCode)) {
                    pageCount++;
                    result.add(schemaSet);
                }
            }

            if (!result.isEmpty()) {
                int totalRecords = 0;
                for (SchemaSet<?> set : result) {
                    totalRecords += set.size();
                }

                log.info("已找到表 {} 的 {} 个SchemaSet，共 {} 条记录",
                        tableCode, result.size(), totalRecords);
            } else {
                log.warn("文件中未找到表 {} 的数据", tableCode);
            }
        } catch (Exception e) {
            log.error("从文件 {} 读取表 {} 数据失败: {}", filePath, tableCode, e.getMessage(), e);
        }

        return result;
    }

    /**
     * 获取文件中包含的所有表名
     *
     * @param filePath 数据文件路径
     * @return 表名列表
     */
    public List<String> getAllTableCodes(String filePath) {
        List<String> tableCodes = new ArrayList<>();

        try (BufferedRandomAccessFile braf = new BufferedRandomAccessFile(filePath, "r")) {
            long fileLength = braf.length();

            while (braf.getFilePointer() < fileLength) {
                SchemaSet<?> schemaSet = readSchemaSetFromFile(braf, 0);

                if (schemaSet != null) {
                    String tableCode = schemaSet.getSchema().getTableCode();
                    if (!tableCodes.contains(tableCode)) {
                        tableCodes.add(tableCode);
                    }
                }
            }

            log.info("文件中共包含 {} 个表", tableCodes.size());
        } catch (Exception e) {
            log.error("从文件 {} 获取表名列表失败: {}", filePath, e.getMessage(), e);
        }

        return tableCodes;
    }

    /**
     * 创建SchemaSetSource模式的示例用法
     */
    public static void main(String[] args) {

        //先加载jar包
        try {
            ClassLoadUtil.addJarPath("E:\\db-data\\");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // 示例用法
        SchemaSetImporter importer = new SchemaSetImporter();
        ImportResult result = importer.importFromFile("E:\\db-data\\rapidark.db");

        System.out.println("共导入 " + result.getTotalTables() + " 个表，包含 " + result.getTotalRecords() + " 条记录");

        // 遍历所有SchemaSet进行处理
        for (SchemaSet<?> schemaSet : result.getSchemaSets()) {
            String tableCode = schemaSet.getSchema().getTableCode();
            int recordCount = schemaSet.size();
            System.out.println("表 " + tableCode + " 分片包含 " + recordCount + " 条记录");

            // 这里可以对SchemaSet中的数据进行自定义处理
            // 例如：将数据转换为其他格式、进行数据分析等
        }

//        // 根据表名获取特定表的所有SchemaSet
//        List<SchemaSet<?>> userSchemaSets = result.getSchemaSetsByTableCode("SYS_USER");
//        if (!userSchemaSets.isEmpty()) {
//            int totalRecords = 0;
//            for (SchemaSet<?> set : userSchemaSets) {
//                totalRecords += set.size();
//            }
//            System.out.println("用户表共有 " + userSchemaSets.size() + " 个分片，包含 " + totalRecords + " 条记录");
//        }
    }
}
