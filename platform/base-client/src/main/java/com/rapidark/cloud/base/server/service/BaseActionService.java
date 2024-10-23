package com.rapidark.cloud.base.server.service;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.base.client.constants.ResourceType;
import com.rapidark.cloud.base.client.model.entity.BaseAction;
import com.rapidark.cloud.base.server.repository.BaseActionRepository;
import com.rapidark.cloud.gateway.formwork.base.BaseService;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.common.model.PageParams;
import com.rapidark.framework.common.utils.CriteriaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作资源管理
 * @author darkness
 * @date 2022/5/27 11:55
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseActionService extends BaseService<BaseAction, Long, BaseActionRepository> {

    @Autowired
    private BaseAuthorityService baseAuthorityService;
    @Value("${spring.application.name}")
    private String DEFAULT_SERVICE_ID;

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    public Page<BaseAction> findListPage(PageParams pageParams) {
        BaseAction query = pageParams.mapToObject(BaseAction.class);
        CriteriaQueryWrapper<BaseAction> queryWrapper = new CriteriaQueryWrapper<>();
        queryWrapper
                .likeRight(ObjectUtils.isNotEmpty(query.getActionCode()), BaseAction::getActionCode, query.getActionCode())
                .likeRight(ObjectUtils.isNotEmpty(query.getActionName()), BaseAction::getActionName, query.getActionName());
//        queryWrapper.orderByDesc("create_time");
        Pageable pageable = PageRequest.of(pageParams.getPage(), pageParams.getLimit(),
                Sort.by(Sort.Direction.DESC, "createTime"));
        return findAllByCriteria(queryWrapper, pageable);
//        return baseActionMapper.selectPage(new Page(pageParams.getPage(), pageParams.getLimit()), queryWrapper);
    }

    /**
     * 查询菜单下所有操作
     *
     * @param menuId
     * @return
     */
    public List<BaseAction> findListByMenuId(Long menuId) {
        CriteriaQueryWrapper<BaseAction> queryWrapper = new CriteriaQueryWrapper();
        queryWrapper.eq(BaseAction::getMenuId, menuId);
        List<BaseAction> list = findAllByCriteria(queryWrapper);
        //根据优先级从小到大排序
        list.sort((BaseAction h1, BaseAction h2) -> h1.getPriority().compareTo(h2.getPriority()));
        return list;
    }

    /**
     * 根据主键获取Action
     *
     * @param actionId
     * @return
     */
    public BaseAction getAction(Long actionId) {
        return findById(actionId);
    }


    /**
     * 检查Action编码是否存在
     *
     * @param acitonCode
     * @return
     */
    public Boolean isExist(String acitonCode) {
        BaseAction example = new BaseAction();
        example.setActionCode(acitonCode);
        long count = count(example);
        return count > 0 ? true : false;
    }

    /**
     * 添加Action操作
     *
     * @param aciton
     * @return
     */
    public BaseAction addAction(BaseAction aciton) {
        if (isExist(aciton.getActionCode())) {
            throw new OpenAlertException(String.format("%s编码已存在!", aciton.getActionCode()));
        }
        if (aciton.getPriority() == null) {
            aciton.setPriority(0);
        }
//        if (aciton.getStatus() == null) {
//            aciton.setStatus(BaseConstants.ENABLED);
//        }
//        if (aciton.getIsPersist() == null) {
//            aciton.setIsPersist(BaseConstants.DISABLED);
//        }
        aciton.setCreateTime(LocalDateTime.now());
        aciton.setServiceId(DEFAULT_SERVICE_ID);
        aciton.setUpdateTime(aciton.getCreateTime());
        save(aciton);
        // 同步权限表里的信息
        baseAuthorityService.saveOrUpdateAuthority(aciton.getActionId(), ResourceType.action);
        return aciton;
    }

    /**
     * 修改Action操作
     *
     * @param aciton
     * @return
     */
    public BaseAction updateAction(BaseAction aciton) {
        BaseAction saved = getAction(aciton.getActionId());
        if (saved == null) {
            throw new OpenAlertException(String.format("%s信息不存在", aciton.getActionId()));
        }
        if (!saved.getActionCode().equals(aciton.getActionCode())) {
            // 和原来不一致重新检查唯一性
            if (isExist(aciton.getActionCode())) {
                throw new OpenAlertException(String.format("%s编码已存在!", aciton.getActionCode()));
            }
        }

        if (aciton.getPriority() == null) {
            aciton.setPriority(0);
        }
        aciton.setUpdateTime(LocalDateTime.now());

        save(aciton);

        // 同步权限表里的信息
        baseAuthorityService.saveOrUpdateAuthority(aciton.getActionId(), ResourceType.action);
        return aciton;
    }

    /**
     * 移除Action
     *
     * @param actionId
     * @return
     */
    public void removeAction(Long actionId) {
        BaseAction aciton = getAction(actionId);
//        if (aciton != null && aciton.getIsPersist().equals(BaseConstants.ENABLED)) {
//            throw new OpenAlertException(String.format("保留数据,不允许删除"));
//        }
        baseAuthorityService.removeAuthorityAction(actionId);
        baseAuthorityService.removeAuthority(actionId, ResourceType.action);
        deleteById(actionId);
    }

    /**
     * 移除菜单相关资源
     *
     * @param menuId
     */
    public void removeByMenuId(Long menuId) {
        List<BaseAction> actionList = findListByMenuId(menuId);
        if (actionList != null && actionList.size() > 0) {
            for (BaseAction action : actionList) {
                removeAction(action.getActionId());
            }
        }
    }
}
