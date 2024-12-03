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

import com.rapidark.cloud.base.server.modules.system.domain.SysDictItem;
import com.rapidark.cloud.platform.admin.api.entity.SysDict;
import com.rapidark.cloud.base.server.modules.system.repository.DictDetailRepository;
import com.rapidark.cloud.base.server.modules.system.repository.DictRepository;
import com.rapidark.cloud.base.server.modules.system.service.DictDetailService;
import com.rapidark.cloud.base.server.modules.system.service.dto.DictDetailDto;
import com.rapidark.cloud.base.server.modules.system.service.dto.DictDetailQueryCriteria;
import com.rapidark.cloud.base.server.modules.system.service.mapstruct.DictDetailMapper;
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
public class DictDetailServiceImpl implements DictDetailService {

    private final DictRepository dictRepository;
    private final DictDetailRepository dictDetailRepository;
    private final DictDetailMapper dictDetailMapper;
    private final RedisUtils redisUtils;

    @Override
    public Map<String,Object> queryAll(DictDetailQueryCriteria criteria, Pageable pageable) {
        Page<SysDictItem> page = dictDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dictDetailMapper::toDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysDictItem resources) {
        dictDetailRepository.save(resources);
        // 清理缓存
        delCaches(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysDictItem resources) {
        SysDictItem sysDictItem = dictDetailRepository.findById(resources.getId()).orElseGet(SysDictItem::new);
        ValidationUtil.isNull( sysDictItem.getId(),"DictDetail","id",resources.getId());
        resources.setId(sysDictItem.getId());
        dictDetailRepository.save(resources);
        // 清理缓存
        delCaches(resources);
    }

    @Override
    @Cacheable(key = "'name:' + #p0")
    public List<DictDetailDto> getDictByName(String name) {
        return dictDetailMapper.toDto(dictDetailRepository.findByDictName(name));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysDictItem sysDictItem = dictDetailRepository.findById(id).orElseGet(SysDictItem::new);
        // 清理缓存
        delCaches(sysDictItem);
        dictDetailRepository.deleteById(id);
    }

    public void delCaches(SysDictItem sysDictItem){
        SysDict sysDict = dictRepository.findById(sysDictItem.getSysDict().getId()).orElseGet(SysDict::new);
        redisUtils.del(CacheKey.DICT_NAME + sysDict.getCode());
    }
}