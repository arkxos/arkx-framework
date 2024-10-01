package com.rapidark.cloud.base.server.mapper;

import com.rapidark.cloud.base.client.model.entity.BaseRole;
import com.rapidark.cloud.base.client.model.entity.BaseRoleUser;
import com.rapidark.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuyadu
 */
@Repository
public interface BaseRoleUserMapper extends SuperMapper<BaseRoleUser> {
    /**
     * 查询系统用户角色
     *
     * @param userId
     * @return
     */
    List<BaseRole> selectRoleUserList(@Param("userId") Long userId);

    /**
     * 查询用户角色ID列表
     *
     * @param userId
     * @return
     */
    List<Long> selectRoleUserIdList(@Param("userId") Long userId);
}
