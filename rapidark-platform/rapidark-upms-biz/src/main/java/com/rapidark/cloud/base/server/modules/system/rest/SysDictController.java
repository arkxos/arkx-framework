/*
 *  Copyright 2019-2021 RapidArk
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.rapidark.cloud.base.server.modules.system.rest;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.rapidark.cloud.base.server.modules.system.service.SysDictItemService;
import com.rapidark.cloud.platform.admin.api.entity.SysDict;
import com.rapidark.cloud.base.server.modules.system.service.SysDictService;
import com.rapidark.cloud.base.server.modules.system.service.dto.DictQueryCriteria;
import com.rapidark.cloud.platform.admin.api.entity.SysDictItem;
import com.rapidark.cloud.platform.common.core.constant.CacheConstants;
import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.common.log.annotation.SysLog;
import com.rapidark.cloud.platform.common.security.annotation.Inner;
import com.rapidark.framework.common.model.ResultBody;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
//import com.rapidark.framework.commons.annotation.Log;
import com.rapidark.framework.common.model.IdsParam;
import com.rapidark.framework.common.exception.BadRequestException;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@Tag(description = "dict", name = "字典管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
@RestController
@RequiredArgsConstructor
@RequestMapping("/dict")
public class SysDictController {

	private static final String ENTITY_NAME = "dict";

    private final SysDictService sysDictService;
	private final SysDictItemService sysDictItemService;

    @Schema(title = "导出字典数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dict:list')")
    public void download(HttpServletResponse response, DictQueryCriteria criteria) throws IOException {
        sysDictService.download(sysDictService.queryAll(criteria), response);
    }

    @Schema(title = "查询字典")
    @GetMapping(value = "/all")
    @PreAuthorize("@el.check('dict:list')")
    public ResponseEntity<Object> queryAll(){
        return new ResponseEntity<>(sysDictService.queryAll(new DictQueryCriteria()),HttpStatus.OK);
    }

	@Schema(title = "查询字典")
	@GetMapping
	@PreAuthorize("@el.check('dict:list')")
	public ResponseResult<Object> query(DictQueryCriteria resources, Pageable pageable){
		return ResponseResult.ok(sysDictService.queryAll(resources,pageable));
	}

	/**
	 * 分页查询字典信息
	 * @param page 分页对象
	 * @return 分页对象
	 */
//	@GetMapping("/page")
//	public ResponseResult<IPage> getDictPage(@ParameterObject Page page, @ParameterObject SysDict sysDict) {
//		return ResponseResult.ok(sysDictService.page(page,
//				Wrappers.<SysDict>lambdaQuery()
//						.eq(StrUtil.isNotBlank(sysDict.getSystemFlag()), SysDict::getSystemFlag, sysDict.getSystemFlag())
//						.like(StrUtil.isNotBlank(sysDict.getCode()), SysDict::getCode, sysDict.getCode())));
//	}

	/**
	 * 分页查询
	 * @param name 名称或者字典项
	 * @return
	 */
//	@GetMapping("/list")
//	public ResponseResult getDictList(String name) {
//		return ResponseResult.ok(sysDictService.list(Wrappers.<SysDict>lambdaQuery()
//				.like(StrUtil.isNotBlank(name), SysDict::getCode, name)
//				.or()
//				.like(StrUtil.isNotBlank(name), SysDict::getName, name)));
//	}

	/**
	 * 通过ID查询字典信息
	 * @param id ID
	 * @return 字典信息
	 */
	@GetMapping("/dict/item/{id}")
	public ResponseResult findById(@PathVariable Long id) {
		return ResponseResult.ok(sysDictService.findById(id));
	}
//
//	/**
//	 * 通过ID查询字典信息
//	 * @param id ID
//	 * @return 字典信息
//	 */
//	@GetMapping("/dict/item/{id}")
//	public ResponseResult getById(@RequestBody SysDict example) {
//		return ResponseResult.ok(sysDictService.findOneByExample(example));
//	}

//    @Log("新增字典")
    @Schema(title = "新增字典")
    @PostMapping
	@SysLog("添加字典")
//    @PreAuthorize("@el.check('dict:add')")
//	@PreAuthorize("@pms.hasPermission('sys_dict_add')")
    public ResultBody<Object> create(@Validated @RequestBody SysDict resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        sysDictService.create(resources);
        return ResultBody.ok();
    }

//    @Log("修改字典")
    @Schema(title = "修改字典")
    @PutMapping
    @PreAuthorize("@el.check('dict:edit')")
    public ResultBody<Object> update(@Validated(SysDict.Update.class) @RequestBody SysDict resources){
        sysDictService.update(resources);
        return ResultBody.ok();
    }

//    @Log("删除字典")
    @Schema(title = "删除字典")
    @DeleteMapping
    @PreAuthorize("@el.check('dict:del')")
	@SysLog("删除字典")
	// @CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
    public ResultBody<Object> delete(@RequestBody IdsParam param){
        sysDictService.delete(param.getIds());
        return ResultBody.ok();
    }

	/**
	 * 同步缓存字典
	 * @return ResponseResult
	 */
	@SysLog("同步字典")
	@PutMapping("/sync")
	public ResponseResult sync() {
		return sysDictService.syncDictCache();
	}

//	@ResponseExcel
//	@GetMapping("/export")
//	public List<SysDictItem> export(SysDictItem sysDictItem) {
//		return sysDictItemService.list(Wrappers.query(sysDictItem));
//	}

	/**
	 * 通过字典类型查找字典
	 * @param type 类型
	 * @return 同类型字典
	 */
//	@GetMapping("/type/{type}")
//	@Cacheable(value = CacheConstants.DICT_DETAILS, key = "#type", unless = "#result.data.isEmpty()")
//	public ResponseResult<List<SysDictItem>> getDictByType(@PathVariable String type) {
//		return ResponseResult.ok(sysDictItemService.list(Wrappers.<SysDictItem>query().lambda().eq(SysDictItem::getDictCode, type)));
//	}

	/**
	 * 通过字典类型查找字典 (针对feign调用) TODO: 兼容性方案，代码重复
	 * @param type 类型
	 * @return 同类型字典
	 */
//	@Inner
//	@GetMapping("/remote/type/{type}")
//	@Cacheable(value = CacheConstants.DICT_DETAILS, key = "#type", unless = "#result.data.isEmpty()")
//	public ResponseResult<List<SysDictItem>> getRemoteDictByType(@PathVariable String type) {
//		return ResponseResult.ok(sysDictItemService.list(Wrappers.<SysDictItem>query().lambda().eq(SysDictItem::getDictCode, type)));
//	}
}