package io.arkx.framework.data.db.core.schema;

import java.util.List;

import io.arkx.framework.data.db.common.type.ProductTypeEnum;

import lombok.Getter;
import lombok.Setter;

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
