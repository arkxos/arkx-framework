package org.ark.framework.orm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author: Zhoulanzhen
 * @description: 表 更新或插入 具体信息
 * @date: 2025/5/14 9:24
 * @version: 1.0
 */
@Builder
@AllArgsConstructor
@Data
public class SchemaUpsertEntity {

    private Boolean success;

    private String type;

    private String execSql;

    private String errorMessage;

    public SchemaUpsertEntity() {
    }

}
