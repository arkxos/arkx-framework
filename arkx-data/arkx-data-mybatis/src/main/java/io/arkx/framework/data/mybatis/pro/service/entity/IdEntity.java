package io.arkx.framework.data.mybatis.pro.service.entity;

import io.arkx.framework.data.mybatis.pro.core.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class IdEntity {
    @Id
    private Long id;
}
