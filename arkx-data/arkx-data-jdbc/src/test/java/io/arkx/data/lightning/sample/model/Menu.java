package io.arkx.data.lightning.sample.model;

import io.arkx.framework.data.common.entity.BaseStringIdTreeEntity;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Nobody
 * @date 2025-07-28 2:49
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Table("TEST_MENU")
public class Menu extends BaseStringIdTreeEntity {

}
