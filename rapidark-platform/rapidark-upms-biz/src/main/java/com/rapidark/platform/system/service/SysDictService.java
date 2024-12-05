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
package com.rapidark.platform.system.service;

import com.rapidark.platform.system.repository.SysDictRepository;
import com.rapidark.platform.system.api.entity.SysDict;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.data.jpa.service.IBaseService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
public interface SysDictService extends IBaseService<SysDict, Long, SysDictRepository> {

    /**
     * 创建
     * @param resources /
     * @return /
     */
    void create(SysDict resources);

    /**
     * 编辑
     * @param resources /
     */
    void update(SysDict resources);

    /**
     * 删除
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * 导出数据
     * @param queryAll 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<SysDict> queryAll, HttpServletResponse response) throws IOException;

	/**
	 * 同步缓存 （清空缓存）
	 * @return ResponseResult
	 */
	ResponseResult syncDictCache();

	boolean exists(String code);
}