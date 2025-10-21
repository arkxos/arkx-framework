package io.arkx.data.lightning.dict.entity;

import lombok.Data;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-20 21:08
 * @since 1.0
 */
@Data
public class ExistDictMappingInfo {

    private Integer rawId;
    private String rawDictType;
    private String rawDictName;
    private Integer newDictId;
    private String newDictCode;
    private String newDictName;

}
