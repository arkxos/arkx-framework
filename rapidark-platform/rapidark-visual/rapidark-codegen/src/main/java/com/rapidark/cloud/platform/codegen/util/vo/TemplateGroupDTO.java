package com.rapidark.cloud.platform.codegen.util.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import com.rapidark.cloud.platform.codegen.entity.GenGroupEntity;

@Data
@Schema(description = "模板传输对象")
@EqualsAndHashCode(callSuper = true)
public class TemplateGroupDTO extends GenGroupEntity {

	/**
	 * 模板id集合
	 */
	@Schema(description = "模板id集合")
	private List<Long> templateId;

}
