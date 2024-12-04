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
package com.rapidark.cloud.base.server.modules.mnt.rest;

import com.rapidark.cloud.base.server.modules.mnt.domain.Database;
import com.rapidark.cloud.base.server.modules.mnt.service.DatabaseService;
import com.rapidark.cloud.base.server.modules.mnt.service.dto.DatabaseDto;
import com.rapidark.cloud.base.server.modules.mnt.service.dto.DatabaseQueryCriteria;
import com.rapidark.cloud.base.server.modules.mnt.util.SqlUtils;
import com.rapidark.framework.common.model.ResponseResult;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import com.rapidark.framework.common.annotation.Log;
import com.rapidark.framework.common.exception.BadRequestException;
import com.rapidark.framework.common.utils.FileUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
* @author zhanghouying
* @date 2019-08-24
*/
@Schema(title = "运维：数据库管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/database")
public class DatabaseController {

	private final String fileSavePath = FileUtil.getTmpDirPath()+"/";
    private final DatabaseService databaseService;

	@Schema(title = "导出数据库数据")
	@GetMapping(value = "/download")
	@PreAuthorize("@el.check('database:list')")
	public void download(HttpServletResponse response, DatabaseQueryCriteria criteria) throws IOException {
		databaseService.download(databaseService.queryAll(criteria), response);
	}

    @Schema(title = "查询数据库")
    @GetMapping
	@PreAuthorize("@el.check('database:list')")
    public ResponseEntity<Object> query(DatabaseQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(databaseService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增数据库")
    @Schema(title = "新增数据库")
    @PostMapping
	@PreAuthorize("@el.check('database:add')")
    public ResponseResult<Object> create(@Validated @RequestBody Database resources){
		databaseService.create(resources);
        return ResponseResult.ok();
    }

    @Log("修改数据库")
    @Schema(title = "修改数据库")
    @PutMapping
	@PreAuthorize("@el.check('database:edit')")
    public ResponseResult<Object> update(@Validated @RequestBody Database resources){
        databaseService.update(resources);
        return ResponseResult.ok();
    }

    @Log("删除数据库")
    @Schema(title = "删除数据库")
    @DeleteMapping
	@PreAuthorize("@el.check('database:del')")
    public ResponseResult<Object> delete(@RequestBody Set<String> ids){
        databaseService.delete(ids);
        return ResponseResult.ok();
    }

	@Log("测试数据库链接")
	@Schema(title = "测试数据库链接")
	@PostMapping("/testConnect")
	@PreAuthorize("@el.check('database:testConnect')")
	public ResponseResult<Object> testConnect(@Validated @RequestBody Database resources){
		boolean success = databaseService.testConnection(resources);
		return ResponseResult.ok(success);
	}

	@Log("执行SQL脚本")
	@Schema(title = "执行SQL脚本")
	@PostMapping(value = "/upload")
	@PreAuthorize("@el.check('database:add')")
	public ResponseEntity<Object> upload(@RequestBody MultipartFile file, HttpServletRequest request)throws Exception{
		String id = request.getParameter("id");
		DatabaseDto database = databaseService.findById(id);
		String fileName;
		if(database != null){
			fileName = file.getOriginalFilename();
			File executeFile = new File(fileSavePath+fileName);
			FileUtil.del(executeFile);
			file.transferTo(executeFile);
			String result = SqlUtils.executeFile(database.getJdbcUrl(), database.getUserName(), database.getPwd(), executeFile);
			return new ResponseEntity<>(result,HttpStatus.OK);
		}else{
			throw new BadRequestException("Database not exist");
		}
	}
}
