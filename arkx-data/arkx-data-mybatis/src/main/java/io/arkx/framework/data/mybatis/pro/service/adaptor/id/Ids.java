package io.arkx.framework.data.mybatis.pro.service.adaptor.id;

import io.arkx.framework.data.mybatis.pro.service.adaptor.validator.ElementNotEmpty;
import lombok.Data;

import java.io.Serializable;

/**
 * 前端请求中只有多个id参数
 *
 * @author w.dehai
 */
@Data
public class Ids implements Serializable {

    @ElementNotEmpty
    private Long[] ids;

}
