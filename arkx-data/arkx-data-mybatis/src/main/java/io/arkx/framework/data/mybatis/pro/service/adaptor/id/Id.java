package io.arkx.framework.data.mybatis.pro.service.adaptor.id;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 前端请求中只只有1个id参数
 *
 * @author w.dehai
 */
@Data
public class Id implements Serializable {

    @NotNull
    @Min(value = Long.MIN_VALUE)
    @Max(value = Long.MAX_VALUE)
    private Long id;

}
