package com.rapidark.cloud.base.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rapidark.cloud.base.client.model.entity.OpenApp;
import com.rapidark.common.model.PageParams;
import com.rapidark.common.mybatis.base.service.IBaseService;
import com.rapidark.common.security.OpenClientDetails;

/**
 * 应用信息管理
 *
 * @author liuyadu
 */
public interface OpenAppService extends IBaseService<OpenApp> {
    /**
     * 查询应用列表
     *
     * @param pageParams
     * @return
     */
    IPage<OpenApp> findListPage(PageParams pageParams);

    /**
     * 获取app信息
     *
     * @param appId
     * @return
     */
    OpenApp getAppInfo(String appId);

    /**
     * 获取app和应用信息
     *
     * @param clientId
     * @return
     */
    OpenClientDetails getAppClientInfo(String clientId);


    /**
     * 更新应用开发新型
     *
     * @param client
     */
    void updateAppClientInfo(OpenClientDetails client);

    /**
     * 添加应用
     *
     * @param app 应用
     * @return 应用信息
     */
    OpenApp addAppInfo(OpenApp app);

    /**
     * 修改应用
     *
     * @param app 应用
     * @return 应用信息
     */
    OpenApp updateInfo(OpenApp app);


    /**
     * 重置秘钥
     *
     * @param appId
     * @return
     */
    String restSecret(String appId);

    /**
     * 删除应用
     *
     * @param appId
     * @return
     */
    void removeApp(String appId);
}
