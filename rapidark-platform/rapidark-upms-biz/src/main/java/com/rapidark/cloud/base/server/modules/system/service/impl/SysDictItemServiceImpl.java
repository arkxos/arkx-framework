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
package com.rapidark.cloud.base.server.modules.system.service.impl;

import com.rapidark.cloud.platform.admin.api.entity.SysDictItem;
import com.rapidark.cloud.platform.admin.api.entity.SysDict;
import com.rapidark.cloud.base.server.modules.system.repository.SysDictItemRepository;
import com.rapidark.cloud.base.server.modules.system.repository.SysDictRepository;
import com.rapidark.cloud.base.server.modules.system.service.SysDictItemService;
import com.rapidark.cloud.base.server.modules.system.service.dto.DictDetailDto;
import com.rapidark.cloud.base.server.modules.system.service.dto.DictDetailQueryCriteria;
import com.rapidark.cloud.base.server.modules.system.service.mapstruct.DictDetailMapper;
import com.rapidark.cloud.platform.common.core.constant.enums.DictTypeEnum;
import com.rapidark.cloud.platform.common.core.exception.ErrorCodes;
import com.rapidark.cloud.platform.common.core.util.MsgUtils;
import com.rapidark.framework.common.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "dict")
public class SysDictItemServiceImpl implements SysDictItemService {

    private final SysDictRepository sysDictRepository;
    private final SysDictItemRepository sysDictItemRepository;
    private final DictDetailMapper dictDetailMapper;
    private final RedisUtils redisUtils;

    @Override
    public Map<String,Object> queryAll(DictDetailQueryCriteria criteria, Pageable pageable) {
        Page<SysDictItem> page = sysDictItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dictDetailMapper::toDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysDictItem resources) {
        sysDictItemRepository.save(resources);
        // 清理缓存
        delCaches(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysDictItem resources) {
        SysDictItem sysDictItem = sysDictItemRepository.findById(resources.getId()).orElseGet(SysDictItem::new);
        ValidationUtil.isNull( sysDictItem.getId(),"DictDetail","id",resources.getId());
		SysDict dict = sysDictItem.getDict();
		// 系统内置
		if (DictTypeEnum.SYSTEM.getType().equals(dict.getSystemFlag())) {
			throw new RuntimeException(MsgUtils.getMessage(ErrorCodes.SYS_DICT_DELETE_SYSTEM));
		}

		resources.setId(sysDictItem.getId());
        sysDictItemRepository.save(resources);
        // 清理缓存
        delCaches(resources);
    }

    @Override
    @Cacheable(key = "'name:' + #p0")
    public List<DictDetailDto> getDictByName(String name) {
        return dictDetailMapper.toDto(sysDictItemRepository.findByDictCode(name));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
	//@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
    public void delete(Long id) {
        SysDictItem sysDictItem = sysDictItemRepository.findById(id).orElseGet(SysDictItem::new);
		SysDict dict = sysDictItem.getDict();
		// 系统内置
		if (DictTypeEnum.SYSTEM.getType().equals(dict.getSystemFlag())) {
			throw new RuntimeException(MsgUtils.getMessage(ErrorCodes.SYS_DICT_DELETE_SYSTEM));
		}
		// 清理缓存
        delCaches(sysDictItem);
        sysDictItemRepository.deleteById(id);
    }

    public void delCaches(SysDictItem sysDictItem){
        SysDict sysDict = sysDictRepository.findById(sysDictItem.getDict().getId()).orElseGet(SysDict::new);
        redisUtils.del(CacheKey.DICT_NAME + sysDict.getCode());
    }
}