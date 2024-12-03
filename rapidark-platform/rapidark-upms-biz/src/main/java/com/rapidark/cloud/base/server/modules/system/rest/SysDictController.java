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

import com.rapidark.cloud.platform.admin.api.entity.SysDict;
import com.rapidark.cloud.base.server.modules.system.service.SysDictService;
import com.rapidark.cloud.base.server.modules.system.service.dto.DictQueryCriteria;
import com.rapidark.framework.common.model.ResultBody;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
//import com.rapidark.framework.commons.annotation.Log;
import com.rapidark.framework.common.model.IdsParam;
import com.rapidark.framework.common.exception.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@RestController
@RequiredArgsConstructor
@Schema(title = "系统：字典管理")
@RequestMapping("/dict")
public class SysDictController {

    private final SysDictService sysDictService;
    private static final String ENTITY_NAME = "dict";

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
    public ResponseEntity<Object> query(DictQueryCriteria resources, Pageable pageable){
        return new ResponseEntity<>(sysDictService.queryAll(resources,pageable),HttpStatus.OK);
    }

//    @Log("新增字典")
    @Schema(title = "新增字典")
    @PostMapping
    @PreAuthorize("@el.check('dict:add')")
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
    public ResultBody<Object> delete(@RequestBody IdsParam param){
        sysDictService.delete(param.getIds());
        return ResultBody.ok();
    }
}