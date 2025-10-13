package io.arkx.framework.data.db.core.schema;

import io.arkx.framework.data.common.entity.BaseEntity;
import io.arkx.framework.data.common.entity.StringIdEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-12 23:20
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("ark_meta_column_foreign_key")
public class ColumnForeignKey extends StringIdEntity {

    private Long connectionConfigId;
    private String schemaName;
    private String tableName;
    private String fieldName;
    private String referencedTableName;
    private String referencedFieldName;

}
