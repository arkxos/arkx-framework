package com.rapidark.platform.system.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rapidark.platform.system.api.constants.BaseConstants;
import com.rapidark.platform.system.api.constants.ResourceType;
import com.rapidark.platform.system.repository.BaseApiRepository;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.framework.common.utils.CriteriaQueryWrapper;
import com.rapidark.platform.system.api.entity.BaseApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 接口资源管理
 * @author darkness
 * @date 2022/5/27 11:59
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseApiService extends BaseService<BaseApi, Long, BaseApiRepository> {

    @Autowired
    private BaseAuthorityService baseAuthorityService;

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    public Page<BaseApi> findListPage(PageParams pageParams) {
        BaseApi query = pageParams.mapToObject(BaseApi.class);
        CriteriaQueryWrapper<BaseApi> queryWrapper = new CriteriaQueryWrapper();
        queryWrapper
                .likeRight(ObjectUtils.isNotEmpty(query.getPath()), BaseApi::getPath, query.getPath())
                .likeRight(ObjectUtils.isNotEmpty(query.getApiName()), BaseApi::getApiName, query.getApiName())
                .likeRight(ObjectUtils.isNotEmpty(query.getApiCode()), BaseApi::getApiCode, query.getApiCode())
                .eq(ObjectUtils.isNotEmpty(query.getServiceId()), BaseApi::getServiceId, query.getServiceId())
                .eq(ObjectUtils.isNotEmpty(query.getStatus()), BaseApi::getStatus, query.getStatus()+"")
                .eq(ObjectUtils.isNotEmpty(query.getIsAuth()), BaseApi::getIsAuth, query.getIsAuth()+"");
//        queryWrapper.orderByDesc("create_time");
        Pageable pageable = PageRequest.of(pageParams.getPage(), pageParams.getLimit(), Sort.by(Sort.Direction.DESC, "createTime"));
        return findAllByCriteria(queryWrapper, pageable);
    }

    /**
     * 查询列表
     *
     * @return
     */
    public List<BaseApi> findAllList(String serviceId) {
        List<BaseApi> list = entityRepository.queryByServiceId(serviceId);
        return list;
    }

    /**
     * 根据主键获取接口
     *
     * @param apiId
     * @return
     */
    public BaseApi getApi(Long apiId) {
        return findById(apiId);
    }


    /**
     * 检查接口编码是否存在
     *
     * @param apiCode
     * @return
     */
    public Boolean isExist(String apiCode) {
        CriteriaQueryWrapper<BaseApi> queryWrapper = new CriteriaQueryWrapper();
        queryWrapper.eq(BaseApi::getApiCode, apiCode);
        int count = getCount(queryWrapper);
        return count > 0 ? true : false;
    }

    /**
     * 添加接口
     *
     * @param api
     * @return
     */
    public void addApi(BaseApi api) {
        if (isExist(api.getApiCode())) {
            throw new OpenAlertException(String.format("%s编码已存在!", api.getApiCode()));
        }
        if (api.getPriority() == null) {
            api.setPriority(0);
        }
//        if (api.getStatus() == null) {
//            api.setStatus(BaseConstants.ENABLED);
//        }
        if (api.getApiCategory() == null) {
            api.setApiCategory(BaseConstants.DEFAULT_API_CATEGORY);
        }
//        if (api.getIsPersist() == null) {
//            api.setIsPersist(0);
//        }
        if (api.getIsAuth() == null) {
            api.setIsAuth(0);
        }
        api.setCreateTime(LocalDateTime.now());
        api.setUpdateTime(api.getCreateTime());
        save(api);
        // 同步权限表里的信息
        baseAuthorityService.saveOrUpdateAuthority(api.getApiId(), ResourceType.api);
    }

    /**
     * 修改接口
     *
     * @param api
     * @return
     */
    public void updateApi(BaseApi api) {
        BaseApi saved = getApi(api.getApiId());
        if (saved == null) {
            throw new OpenAlertException("信息不存在!");
        }
        if (!saved.getApiCode().equals(api.getApiCode())) {
            // 和原来不一致重新检查唯一性
            if (isExist(api.getApiCode())) {
                throw new OpenAlertException(String.format("%s编码已存在!", api.getApiCode()));
            }
        }
        if (api.getPriority() == null) {
            api.setPriority(0);
        }
        if (api.getApiCategory() == null) {
            api.setApiCategory(BaseConstants.DEFAULT_API_CATEGORY);
        }
        api.setUpdateTime(LocalDateTime.now());

        BeanUtil.copyProperties(api, saved, CopyOptions.create().ignoreNullValue());

        save(saved);
        // 同步权限表里的信息
        baseAuthorityService.saveOrUpdateAuthority(api.getApiId(), ResourceType.api);
    }

    /**
     * 查询接口
     *
     * @param apiCode
     * @return
     */
    public BaseApi getApiByCode(String apiCode) {
        return entityRepository.findByApiCode(apiCode);
    }


    /**
     * 移除接口
     *
     * @param apiId
     * @return
     */
    public void removeApi(Long apiId) {
        BaseApi api = getApi(apiId);
//        if (api != null && api.getIsPersist().equals(BaseConstants.ENABLED)) {
//            throw new OpenAlertException(String.format("保留数据,不允许删除"));
//        }
        baseAuthorityService.removeAuthority(apiId, ResourceType.api);
        deleteById(apiId);
    }


    /**
     * 获取数量
     *
     * @param queryWrapper
     * @return
     */
    public int getCount(CriteriaQueryWrapper<BaseApi> queryWrapper) {
        return findAllByCriteria(queryWrapper).size();
    }
}
