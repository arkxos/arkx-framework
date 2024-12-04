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

import com.rapidark.cloud.platform.admin.api.entity.SysDictItem;
import com.rapidark.cloud.base.server.modules.system.service.SysDictItemService;
import com.rapidark.cloud.base.server.modules.system.service.dto.DictDetailDto;
import com.rapidark.cloud.base.server.modules.system.service.dto.DictDetailQueryCriteria;
import com.rapidark.framework.common.model.ResponseResult;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
//import com.rapidark.framework.commons.annotation.Log;
import com.rapidark.framework.common.exception.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@RestController
@RequiredArgsConstructor
@Schema(title = "系统：字典详情管理")
public class SysDictItemController {

    private final SysDictItemService sysDictItemService;
    private static final String ENTITY_NAME = "dictDetail";

    @Schema(title = "查询字典详情")
    @GetMapping
    public ResponseEntity<Object> query(DictDetailQueryCriteria criteria,
                                        @PageableDefault(sort = {"dictSort"}, direction = Sort.Direction.ASC) Pageable pageable){
        return new ResponseEntity<>(sysDictItemService.queryAll(criteria,pageable),HttpStatus.OK);
    }

	/**
	 * 分页查询
	 * @param page 分页对象
	 * @param sysDictItem 字典项
	 * @return
	 */
//	@GetMapping("/item/page")
//	public ResponseResult getSysDictItemPage(Page page, SysDictItem sysDictItem) {
//		return ResponseResult.ok(sysDictItemService.page(page, Wrappers.query(sysDictItem)));
//	}

	/**
	 * 通过id查询字典项
	 * @param id id
	 * @return ResponseResult
	 */
//	@GetMapping("/item/details/{id}")
//	public ResponseResult getDictItemById(@PathVariable("id") Long id) {
//		return ResponseResult.ok(sysDictItemService.getById(id));
//	}

	/**
	 * 查询字典项详情
	 * @param query 查询条件
	 * @return ResponseResult
	 */
//	@GetMapping("/item/details")
//	public ResponseResult getDictItemDetails(SysDictItem query) {
//		return ResponseResult.ok(sysDictItemService.getOne(Wrappers.query(query), false));
//	}

	@Schema(title = "查询多个字典详情")
    @GetMapping(value = "/map")
    public ResponseEntity<Object> getDictDetailMaps(@RequestParam String dictName){
        String[] names = dictName.split("[,，]");
        Map<String, List<DictDetailDto>> dictMap = new HashMap<>(16);
        for (String name : names) {
            dictMap.put(name, sysDictItemService.getDictByName(name));
        }
        return new ResponseEntity<>(dictMap, HttpStatus.OK);
    }

//    @Log("新增字典详情")
    @Schema(title = "新增字典详情")
    @PostMapping
    @PreAuthorize("@el.check('dict:add')")
    public ResponseResult<Object> create(@Validated @RequestBody SysDictItem resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        sysDictItemService.create(resources);
        return ResponseResult.ok();
    }

//    @Log("修改字典详情")
    @Schema(title = "修改字典详情")
    @PutMapping
    @PreAuthorize("@el.check('dict:edit')")
    public ResponseResult<Object> update(@Validated(SysDictItem.Update.class) @RequestBody SysDictItem resources){
        sysDictItemService.update(resources);
        return ResponseResult.ok();
    }

//    @Log("删除字典详情")
    @Schema(title = "删除字典详情")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("@el.check('dict:del')")
    public ResponseResult<Object> delete(@PathVariable Long id){
        sysDictItemService.delete(id);
        return ResponseResult.ok();
    }
}