package org.ark.framework.orm.db.create;

import java.util.*;

import org.ark.framework.orm.SchemaColumn;

import io.arkx.framework.commons.util.ObjectUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @class org.ark.framework.orm.db.create.MYSQLTableCreator
 * @author Darkness
 * @date 2013-1-29 下午04:31:11
 * @version V1.0
 */
@Slf4j
public class MYSQLTableCreator extends AbstractTableCreator {

    // MySQL行大小限制为65535字节
    private static final int MAX_ROW_SIZE = 65535;

    // 设置一个适中的安全阈值
    private static final int SAFE_ROW_SIZE_THRESHOLD = 50000;

    // 超过此长度的VARCHAR自动考虑转为TEXT类型(4000字符约12000字节utf8mb4)
    private static final int LARGE_VARCHAR_THRESHOLD = 4000;

    // 每列的额外开销（字节）
    private static final int COLUMN_OVERHEAD = 2;

    @Override
    protected String convert(int columnType, int length, int precision) {
        if (columnType == 3)
            return "int";
        else if (columnType == 2)
            return "longblob";
        else if (columnType == 12)
            return "datetime";
        else if (columnType == 4)
            return "decimal";
        else if (columnType == 6)
            return "int";
        else if (columnType == 5)
            return "int";
        else if (columnType == 8)
            return "int";
        else if (columnType == 7)
            return "bigint";
        else if (columnType == 9)
            return "int";
        else if (columnType == 1)
            return "varchar";
        else if (columnType == 10) {
            return "mediumtext";
        }
        return null;
    }

    @Override
    public String createTableSql(SchemaColumn[] scs, String tableCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table " + tableCode + "\n(\n");
        StringBuilder ksb = new StringBuilder();

        // 使用LinkedHashMap存储字段,自动去重并保持顺序,忽略大小写
        Map<String, SchemaColumn> uniqueColumns = new LinkedHashMap<>();
        for (SchemaColumn sc : scs) {
            uniqueColumns.put(sc.getColumnName().toLowerCase(), sc);
        }

        // 智能优化列类型以避免行大小超出MySQL限制
        Map<String, String> optimizedColumnTypes = optimizeColumnTypes(uniqueColumns.values(), tableCode);

        int i = 0;
        for (SchemaColumn sc : uniqueColumns.values()) {
            if (i != 0) {
                sb.append(",\n");
            }
            sb.append("\t`" + sc.getColumnName() + "` ");// 字段名称可能会是关键词

            // 使用优化后的列类型(如果有)，否则使用原始类型
            String columnName = sc.getColumnName().toLowerCase();
            String sqlType = optimizedColumnTypes.containsKey(columnName)
                    ? optimizedColumnTypes.get(columnName)
                    : toSQLType(sc.getColumnType(), sc.getLength(), sc.getPrecision());

            sb.append(sqlType + " ");
            if (sc.isMandatory()) {
                sb.append("not null");
            }
            if (sc.isPrimaryKey()) {
                if (ksb.length() == 0)
                    ksb.append("\tprimary key (");
                else {
                    ksb.append(",");
                }
                ksb.append(sc.getColumnName());
            }
            i++;
        }
        if (ksb.length() != 0) {
            ksb.append(")");
            sb.append(",\n" + ksb);
        }
        sb.append("\n)");
        return sb.toString();
    }

    public String toSQLType(int columnType, int length, int precision) {
        String type = convert(columnType, length, precision);
        if (type == "CLOB") {
            length = 0;
        }
        if (ObjectUtil.empty(type)) {
            throw new RuntimeException("Unknown DataType" + columnType);
        }
        if ((length == 0) && (columnType == 1)) {
            throw new RuntimeException("varchar's length can't be empty!");
        }

        if ("double".equalsIgnoreCase(type)) {
            return type;
        } else if ("datetime".equalsIgnoreCase(type)) {
            return type;
        } else if ("varchar".equalsIgnoreCase(type) || "mediumtext".equalsIgnoreCase(type)) {
            if (length > 3000) {
                return "text";
            }
        }

        return type + getFieldExtDesc(length, precision);
    }

    /**
     * 智能优化列类型以避免行大小超出MySQL限制 仅在必要时转换最小数量的列
     */
    private Map<String, String> optimizeColumnTypes(Iterable<SchemaColumn> columns, String tableCode) {
        Map<String, String> optimizedTypes = new LinkedHashMap<>();
        List<ColumnSizeInfo> columnSizeInfos = new ArrayList<>();
        int totalColumnsCount = 0;
        int estimatedRowSize = 0;

        // 第一遍：收集所有列的信息并计算初始行大小
        for (SchemaColumn sc : columns) {
            totalColumnsCount++;

            // 非VARCHAR类型或小VARCHAR直接添加到总大小
            if (sc.getColumnType() != 1 || sc.getLength() <= 255) {
                int size = estimateColumnSize(sc.getColumnType(), sc.getLength());
                estimatedRowSize += size;
                continue;
            }

            // 对于超大VARCHAR，直接转为TEXT（这是显而易见的优化）
            if (sc.getLength() >= LARGE_VARCHAR_THRESHOLD) {
                String textType = determineTextType(sc.getLength());
                optimizedTypes.put(sc.getColumnName().toLowerCase(), textType);
                log.info("表{}的超大列{}(长度:{})自动转换为{}类型", tableCode, sc.getColumnName(), sc.getLength(), textType);
                estimatedRowSize += 20; // TEXT指针大小
                continue;
            }

            // 收集中等大小的VARCHAR列信息，稍后可能需要优化
            int varcharSize = estimateVarcharSize(sc.getLength());
            ColumnSizeInfo info = new ColumnSizeInfo(sc, varcharSize);
            columnSizeInfos.add(info);
            estimatedRowSize += varcharSize;
        }

        // 添加行开销
        estimatedRowSize += totalColumnsCount * COLUMN_OVERHEAD;

        // 如果总大小已经在安全范围内，无需进一步转换
        if (estimatedRowSize <= SAFE_ROW_SIZE_THRESHOLD) {
            log.info("表{}的预估行大小({}字节)在安全范围内，无需转换更多列", tableCode, estimatedRowSize);
            return optimizedTypes;
        }

        // 第二遍：如果行大小超出安全阈值，优先转换较大的VARCHAR列
        log.warn("表{}的预估行大小({}字节)超出安全阈值({}字节)，需要转换部分列", tableCode, estimatedRowSize, SAFE_ROW_SIZE_THRESHOLD);

        // 按列大小降序排序，优先转换最大的列
        columnSizeInfos.sort(Comparator.comparing(ColumnSizeInfo::getSize).reversed());

        // 计算需要减少的大小
        int sizeToReduce = estimatedRowSize - SAFE_ROW_SIZE_THRESHOLD;
        int reducedSize = 0;
        int convertedColumns = 0;

        // 只转换必要数量的列
        for (ColumnSizeInfo info : columnSizeInfos) {
            SchemaColumn sc = info.getColumn();
            int currentSize = info.getSize();

            // 计算转换这一列能节省的空间(VARCHAR大小 - TEXT指针大小)
            int savedSpace = currentSize - 20;

            // 如果已经减少足够的大小，停止转换
            if (reducedSize >= sizeToReduce) {
                break;
            }

            // 转换为TEXT类型
            String textType = determineTextType(sc.getLength());
            optimizedTypes.put(sc.getColumnName().toLowerCase(), textType);
            log.info("表{}的列{}(长度:{})转换为{}类型，节省约{}字节", tableCode, sc.getColumnName(), sc.getLength(), textType,
                    savedSpace);

            reducedSize += savedSpace;
            convertedColumns++;
        }

        // 最终行大小
        int finalRowSize = estimatedRowSize - reducedSize;
        log.info("表{}的优化结果: 转换了{}个列，减少了{}字节，最终行大小约{}字节", tableCode, convertedColumns, reducedSize, finalRowSize);

        return optimizedTypes;
    }

    /**
     * 估算VARCHAR列的大小
     */
    private int estimateVarcharSize(int length) {
        // UTF8mb4每字符最多4字节 + 变长字段前缀(长度<=255用1字节，>255用2字节)
        int prefix = length <= 255 ? 1 : 2;
        // 实际大小不会超过65535
        return Math.min(length * 3 + prefix, 65535);
    }

    /**
     * 估算列在MySQL中的存储大小
     */
    private int estimateColumnSize(int columnType, int length) {
        switch (columnType) {
            case 1 : // VARCHAR
                return estimateVarcharSize(length);
            case 2 : // BLOB
            case 10 : // CLOB/TEXT
                return 20; // 外部存储，只计指针大小
            case 3 : // DOUBLE
                return 8;
            case 4 : // DECIMAL
                return 8;
            case 5 : // FLOAT
                return 4;
            case 6 : // NUMBER
            case 8 : // INTEGER
            case 9 : // INT
                return 4;
            case 7 : // BIGINT
                return 8;
            case 12 : // DATETIME
                return 8;
            default :
                return 8; // 默认估算值
        }
    }

    /**
     * 根据VARCHAR长度确定合适的TEXT类型
     */
    private String determineTextType(int length) {
        if (length <= 65535) {
            return "TEXT";
        } else if (length <= 16777215) {
            return "MEDIUMTEXT";
        } else {
            return "LONGTEXT";
        }
    }

    /**
     * 列大小信息，用于排序和优化
     */
    private static class ColumnSizeInfo {

        private final SchemaColumn column;

        private final int size;

        public ColumnSizeInfo(SchemaColumn column, int size) {
            this.column = column;
            this.size = size;
        }

        public SchemaColumn getColumn() {
            return column;
        }

        public int getSize() {
            return size;
        }

    }

}
