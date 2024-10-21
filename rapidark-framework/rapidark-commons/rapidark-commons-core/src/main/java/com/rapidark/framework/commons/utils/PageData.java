package com.rapidark.framework.commons.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/6 15:23
 */
@Data
@ApiModel(value = "分页数据")
public class PageData<T> {

    private int currentPage = Constants.CURRENT_PAGE;
    //分页数量
    private int pageSize = Constants.PAGE_SIZE;

    @ApiModelProperty(required = true, value = "总记录数", example = "0", position=1)
    private Long totalElements;

    @ApiModelProperty(required = true, value = "数据", position=2)
    private List<T> content;

}
