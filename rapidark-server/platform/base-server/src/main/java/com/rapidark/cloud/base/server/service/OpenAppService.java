package com.rapidark.cloud.base.server.service;

import com.rapidark.cloud.base.client.model.entity.OpenApp;
import com.rapidark.cloud.base.server.service.dto.OpenAppDto;
import com.rapidark.cloud.base.server.service.dto.OpenClientQueryCriteria;
import com.rapidark.common.security.OpenClientDetails;
import com.rapidark.common.utils.PageData;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 应用信息管理
 *
 * @website http://rapidark.com
 * @description 服务接口
 * @author Darkness
 * @date 2022-05-25
 **/
public interface OpenAppService {//} extends IBaseService<OpenApp> {
    /**
     * 查询数据分页
     * @param criteria 条件
     * @param pageable 分页参数
     * @return Map<String,Object>
     */
    PageData<OpenAppDto> queryAll(OpenClientQueryCriteria criteria, Pageable pageable);

    /**
     * 查询所有数据不分页
     * @param criteria 条件参数
     * @return List<OpenClientDto>
     */
    List<OpenAppDto> queryAll(OpenClientQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param appId ID
     * @return OpenClientDto
     */
    OpenAppDto findById(String appId);

    /**
     * 创建
     * @param resources /
     * @return OpenClientDto
     */
    OpenAppDto create(OpenApp resources);

    /**
     * 编辑
     * @param resources /
     */
    void update(OpenApp resources);

    /**
     * 多选删除
     * @param ids /
     */
    void deleteAll(String[] ids);

    /**
     * 导出数据
     * @param all 待导出的数据
     * @param response /
     * @throws IOException /
     */
//    void download(List<OpenAppDto> all, HttpServletResponse response) throws IOException;


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
     * 重置秘钥
     *
     * @param appId
     * @return
     */
    String restSecret(String appId);

    void removeApp(String appId);

}
