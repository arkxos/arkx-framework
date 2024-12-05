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


import com.rapidark.platform.system.repository.SysDictItemRepository;
import com.rapidark.platform.system.api.entity.SysDictItem;
import com.rapidark.platform.system.dto.DictItemDto;
import com.rapidark.framework.data.jpa.service.IBaseService;

import java.util.List;
/**
* @author Zheng Jie
* @date 2019-04-10
*/
public interface SysDictItemService extends IBaseService<SysDictItem, Long, SysDictItemRepository> {

    /**
     * 创建
     * @param resources /
     */
    void create(SysDictItem resources);

    /**
     * 编辑
     * @param resources /
     */
    void update(SysDictItem resources);

    /**
     * 删除
     * @param id /
     */
    void delete(Long id);


    /**
     * 根据字典名称获取字典详情
     * @param code 字典名称
     * @return /
     */
    List<DictItemDto> getDictItemsByCode(String code);
}