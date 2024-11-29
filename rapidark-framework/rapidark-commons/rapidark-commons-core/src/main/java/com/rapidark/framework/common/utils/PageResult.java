package com.rapidark.framework.common.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/6 15:23
 */
@Data
@Schema(description = "分页数据")
public class PageResult<T> {

	@Schema(description = "总记录数", example = "0")
	private long totalNum;

    private int currentPage = Constants.CURRENT_PAGE;

    //分页数量
    private int pageSize = Constants.PAGE_SIZE;
	
    @Schema(description = "数据")
    private List<T> lists;

	private Object data;

	public static <T> PageResult<T> of(Page<T> page) {
		PageResult<T> pageResult = new PageResult<>();
		pageResult.setTotalNum(page.getTotalElements());
		pageResult.setCurrentPage(page.getNumber());
		pageResult.setPageSize(page.getSize());
		pageResult.setLists(page.getContent());
		return pageResult;
	}

}
