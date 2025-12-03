package io.arkx.data.lightning.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryResult {

    private String sql; // 生成的SQL语句

    private String error;

}
