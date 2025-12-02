package io.arkx.framework.commons.model;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/6 15:23
 */
@Data
@Schema(description = "分页数据")
public class PageResult<T> {

	@Schema(description = "总记录数", example = "0")
	private long total;

	private int current = Constants.CURRENT_PAGE;

	// 分页数量
	private int size = Constants.PAGE_SIZE;

	@Schema(description = "数据")
	private List<T> records;

	private Object data;

	public static <T> PageResult<T> of(Page<T> page) {
		PageResult<T> pageResult = new PageResult<>();
		pageResult.setTotal(page.getTotalElements());
		pageResult.setCurrent(page.getNumber());
		pageResult.setSize(page.getSize());
		pageResult.setRecords(page.getContent());
		return pageResult;
	}

}
