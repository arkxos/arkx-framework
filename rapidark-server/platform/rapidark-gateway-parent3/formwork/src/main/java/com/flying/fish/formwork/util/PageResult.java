package com.flying.fish.formwork.util;

import lombok.Data;

import java.util.List;

/**
 * @Description 分页实体类
 * @Author jianglong
 * @Date 2019/07/02
 * @Version V1.0
 */
@Data
public class PageResult<T> {
    //总数
    private long totalNum=1;
    //下一步
    private int currentPage= Constants.CURRENT_PAGE;
    //分页数量
    private int pageSize= Constants.PAGE_SIZE;
    private List<T> lists;
    private Object data = null;
}
