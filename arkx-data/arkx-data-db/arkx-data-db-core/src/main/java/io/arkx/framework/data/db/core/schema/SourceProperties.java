package io.arkx.framework.data.db.core.schema;

import lombok.Getter;
import lombok.Setter;
import io.arkx.framework.data.db.common.type.ProductTypeEnum;

import java.util.List;

@Setter
@Getter
public class SourceProperties {

    private ProductTypeEnum productType;

    private String driverClass;
    private String jdbcUrl;
    private String username;
    private String password;

    private String schemaName;
    private String tableName;
    private List<String> columnNames;
    private List<String> distributedKeys;

}
