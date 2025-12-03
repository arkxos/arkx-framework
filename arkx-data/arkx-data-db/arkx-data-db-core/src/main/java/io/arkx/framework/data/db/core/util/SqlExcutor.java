package io.arkx.framework.data.db.core.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Pair;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlExcutor {

    @Data
    @Builder
    public static class ScriptExecuteResult {

        private boolean isSelect;

        private String sql;

        private String resultSummary;

        private List<Pair<String, String>> resultHeader;

        private List<Map<String, Object>> resultData;

    }

    public static ScriptExecuteResult execute(Connection connection, String sql, int page, int size)
            throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setQueryTimeout(300);
        statement.setFetchSize(isMySqlConnection(connection) ? Integer.MIN_VALUE : (size < 10) ? 100 : size);
        log.info("ExecuteSQL:{}\n{}", sql);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        boolean b = statement.execute();
        stopWatch.stop();
        String seconds = "%.6f s".formatted(stopWatch.getTotalTimeSeconds());
        if (b) {
            int skipNumber = size * (page - 1);
            try (ResultSet rs = statement.getResultSet()) {
                List<Pair<String, String>> columns = new ArrayList<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String columnName = rs.getMetaData().getColumnLabel(i);
                    String columnTypeName = rs.getMetaData().getColumnTypeName(i);
                    columns.add(Pair.of(columnName, columnTypeName));
                }
                List<Map<String, Object>> list = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (Pair<String, String> column : columns) {
                        String columnName = column.getKey();
                        Object columnValue = null;
                        try {
                            columnValue = rs.getObject(columnName);
                        } catch (SQLException se) {
                            log.warn("Failed to call jdbc ResultSet::getObject(): {}", se.getMessage(), se);
                        }
                        row.put(columnName, columnValue);
                    }
                    if (skipNumber <= 0) {
                        list.add(row);
                        if (list.size() >= size) {
                            break;
                        }
                    } else {
                        skipNumber--;
                    }
                }
                return ScriptExecuteResult.builder().isSelect(true).sql(sql).resultSummary("Time: " + seconds)
                        .resultHeader(columns).resultData(list).build();
            }
        } else {
            int updateCount = statement.getUpdateCount();
            return ScriptExecuteResult.builder().isSelect(false).sql(sql)
                    .resultSummary("affected : " + updateCount + ", Time: " + seconds)
                    .resultHeader(Collections.emptyList()).resultData(Collections.emptyList()).build();
        }
    }

    private static boolean isMySqlConnection(Connection connection) {
        try {
            String productName = connection.getMetaData().getDatabaseProductName();
            return productName.contains("MySQL") || productName.contains("MariaDB");
        } catch (Exception e) {
            return false;
        }
    }

    public void execute(Connection connection, String sql) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setQueryTimeout(300);
        log.info("ExecuteSQL:{}\n{}", sql);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        statement.execute();
        stopWatch.stop();
        log.info("Time: {}", stopWatch.getTotalTimeSeconds());

        /*
         * try (CloseableDataSource dataSource =
         * connectionService.getDataSource(databaseConn)) { try (Connection connection =
         * dataSource.getConnection()) { List<SqlInput> summaries = new
         * ArrayList<>(statements.size()); List<SqlResult> results = new
         * ArrayList<>(statements.size()); for (String sql : statements) { try {
         * ScriptExecuteResult result = DBSqlUtils.execute(connection, sql, page, size);
         * summaries.add(SqlInput.builder().sql(sql).summary(result.getResultSummary()).
         * build()); if (CollectionUtils.isNotEmpty(result.getResultHeader())) {
         * results.add( SqlResult.builder() .columns(result.getResultHeader().stream()
         * .map(one -> ColumnItem.builder() .columnName(specialReplace(one.getKey()))
         * .columnType(one.getValue()) .build() ).collect(Collectors.toList()))
         * .rows(convertRows(result.getResultData())) .build()); } } catch (Exception e)
         * { summaries.add( SqlInput.builder() .sql(sql) .summary(e.getMessage())
         * .build()); } } return Result.success( OnlineSqlDataResponse.builder()
         * .summaries(summaries) .results(results) .build()); } } catch (Exception e) {
         * throw new RuntimeException(e); }
         */

    }

}
