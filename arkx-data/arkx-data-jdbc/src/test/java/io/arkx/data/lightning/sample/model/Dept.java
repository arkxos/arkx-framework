package io.arkx.data.lightning.sample.model;

import org.springframework.data.relational.core.mapping.Table;

import io.arkx.framework.data.common.entity.StringIdTreeEntity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Nobody
 * @date 2025-07-28 2:01
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Builder
@Table("TEST_DEPT")
// 部门实体（公共闭包表）
public class Dept extends StringIdTreeEntity {

}
