package com.rapidark.cloud.base.server.service;

import com.rapidark.cloud.base.client.model.entity.OpenApp;
import com.rapidark.cloud.base.server.repository.OpenAppRepository;
import com.rapidark.cloud.base.client.service.dto.OpenAppDto;
import com.rapidark.cloud.base.client.service.dto.OpenClientQueryCriteria;
import com.rapidark.cloud.base.client.service.mapstruct.OpenAppMapper;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.common.security.OpenClientDetails;
import com.rapidark.framework.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.oauth2.provider.client.BaseClientDetails;
//import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 客户端管理业务类
 * @website http://rapidark.com
 * @author Darkness
 * @date 2022-05-25
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class OpenAppService extends BaseService<OpenApp, String, OpenAppRepository> {

    /**
     * token有效期，默认12小时
     */
    public static final int ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 12;
    /**
     * token有效期，默认7天
     */
    public static final int REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 7;

    private final OpenAppRepository openAppRepository;
    private final OpenAppMapper openClientMapper;

    private final BaseAuthorityService baseAuthorityService;
//    private final JdbcClientDetailsService jdbcClientDetailsService;
//
//    private final ClientServerRegisterService clientServerRegisterService;
    private final SystemIdGenerator systemIdGenerator;

    /**
     * 查询数据分页
     * @param criteria 条件
     * @param pageable 分页参数
     * @return Map<String,Object>
     */
    public PageResult<OpenAppDto> queryAll(OpenClientQueryCriteria criteria, Pageable pageable){
        Page<OpenApp> page = openAppRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPageData(page.map(openClientMapper::toDto));
    }

    /**
     * 查询所有数据不分页
     * @param criteria 条件参数
     * @return List<OpenClientDto>
     */
    public List<OpenAppDto> queryAll(OpenClientQueryCriteria criteria){
        return openClientMapper.toDto(openAppRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    /**
     * 根据ID查询
     * @param appId ID
     * @return OpenClientDto
     */
    @Cacheable(value = "apps", key = "#appId")
    @Transactional
    public OpenAppDto findDtoById(String appId) {
        OpenApp openClient = openAppRepository.findById(appId).orElseGet(OpenApp::new);
        ValidationUtil.isNull(openClient.getAppId(),"OpenClient","appId",appId);
        return openClientMapper.toDto(openClient);
    }

    /**
     * 添加应用
     *
     * @param resources
     * @return 应用信息
     */
    @CachePut(value = "apps", key = "#app.appId")
    @Transactional(rollbackFor = Exception.class)
    public OpenAppDto create(OpenApp resources) {

        //验证名称是否重复
        OpenApp qClinet = new OpenApp();
        qClinet.setAppName(resources.getAppName());
        long count = this.count(qClinet);
        Assert.isTrue(count <= 0, "客户端名称已存在，不能重复");

        //验证编码是否重复
        OpenApp qClinet1 = new OpenApp();
        qClinet.setAppNameEn(resources.getAppNameEn());
        long count1 = this.count(qClinet1);
        Assert.isTrue(count1 <= 0, "客户端编码已存在，不能重复");

        String appId = systemIdGenerator.generate()+"";//UuidUtil.base58Uuid();
        String apiKey = RandomValueUtils.randomAlphanumeric(24);
        String secretKey = RandomValueUtils.randomAlphanumeric(32);
        resources.setAppId(appId);
        resources.setApiKey(apiKey);
        resources.setSecretKey(secretKey);
//        if (resources.getIsPersist() == null) {
//            resources.setIsPersist(0);
//        }

        OpenApp saved = openAppRepository.save(resources);

        Map info = BeanConvertUtils.objectToMap(saved);
        // 功能授权
//        BaseClientDetails client = new BaseClientDetails();
//        client.setClientId(saved.getApiKey());
//        client.setClientSecret(saved.getSecretKey());
//        client.setAdditionalInformation(info);
//        client.setAuthorizedGrantTypes(Arrays.asList("authorization_code", "client_credentials", "implicit", "refresh_token"));
//        client.setAccessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
//        client.setRefreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
//        jdbcClientDetailsService.addClientDetails(client);

        return openClientMapper.toDto(saved);
    }

    /**
     * 修改应用
     * @param app 应用
     */
    @Caching(evict = {
            @CacheEvict(value = {"apps"}, key = "#app.appId"),
            @CacheEvict(value = {"apps"}, key = "'client:'+#app.appId")
    })
    @Transactional(rollbackFor = Exception.class)
    public void update(OpenApp app) {
        OpenApp openClient = openAppRepository.findById(app.getAppId()).orElseGet(OpenApp::new);
        ValidationUtil.isNull( openClient.getAppId(),"OpenClient","id",app.getAppId());
        openClient.copy(app);
        openAppRepository.save(openClient);

        // 修改客户端附加信息
//        OpenClientDto appInfo = this.findById(app.getAppId());
        Map info = BeanConvertUtils.objectToMap(openClient);
//        BaseClientDetails client = (BaseClientDetails) jdbcClientDetailsService.loadClientByClientId(openClient.getApiKey());
//        client.setAdditionalInformation(info);
//        jdbcClientDetailsService.updateClientDetails(client);
    }

    /**
     * 多选删除
     * @param ids /
     */
    public void deleteAll(String[] ids) {
        for (String appId : ids) {
            deleteById(appId);
        }
    }

    /**
     * 删除应用
     *
     * @param appId
     * @return
     */
    @Caching(evict = {
            @CacheEvict(value = {"apps"}, key = "#appId"),
            @CacheEvict(value = {"apps"}, key = "'client:'+#appId")
    })
    @Override
    public void deleteById(String appId) {
        Optional<OpenApp> appInfoOptional = openAppRepository.findById(appId);
        if (appInfoOptional.isEmpty()) {
            throw new OpenAlertException(appId + "应用不存在!");
        }
        OpenApp appInfo = appInfoOptional.get();
//        if (appInfo.getStatus() == .equals(BaseConstants.ENABLED)) {
//            throw new OpenAlertException(String.format("保留数据,不允许删除"));
//        }

//        ClientServerRegister clientServerRegister = new ClientServerRegister();
//        clientServerRegister.setClientId(appId);
//        //查找是否有注册到其它网关服务上，如有一并删除
//        List<ClientServerRegister> clientServerRegisterList = clientServerRegisterService.findAll(clientServerRegister);
//        if (!CollectionUtils.isEmpty(clientServerRegisterList)){
//            clientServerRegisterService.delete(clientServerRegister);
//        }
//
//        // 移除应用权限
//        baseAuthorityService.removeAuthorityApp(appId);
//        super.deleteById(appId);
//        jdbcClientDetailsService.removeClientDetails(appInfo.getApiKey());
    }

    /**
     * 获取app和应用信息
     *
     * @param clientId
     * @return
     */
    @Cacheable(value = "apps", key = "'client:'+#clientId")
    public OpenClientDetails getAppClientInfo(String clientId) {
//        BaseClientDetails baseClientDetails = null;
//        try {
//            baseClientDetails = (BaseClientDetails) jdbcClientDetailsService.loadClientByClientId(clientId);
//        } catch (Exception e) {
//            return null;
//        }
//        String appId = baseClientDetails.getAdditionalInformation().get("appId") + "";
//        OpenClientDetails openClient = new OpenClientDetails();
//        BeanUtils.copyProperties(baseClientDetails, openClient);
//        openClient.setAuthorities(baseAuthorityService.findAuthorityByApp(appId, ""));
//        return openClient;
		return null;
    }

    /**
     * 更新应用开发信息
     *
     * @param client
     */
    @CacheEvict(value = {"apps"}, key = "'client:'+#client.clientId")
    public void updateAppClientInfo(OpenClientDetails client) {
//        jdbcClientDetailsService.updateClientDetails(client);
    }

    /**
     * 重置秘钥
     *
     * @param appId
     * @return
     */
    @Caching(evict = {
            @CacheEvict(value = {"apps"}, key = "#appId"),
            @CacheEvict(value = {"apps"}, key = "'client:'+#appId")
    })
    public String restSecret(String appId) {
        Optional<OpenApp> appInfoOptional = openAppRepository.findById(appId);
        if (appInfoOptional.isEmpty()) {
            throw new OpenAlertException(appId + "应用不存在!");
        }
        OpenApp appInfo = appInfoOptional.get();
//        if (appInfo.getIsPersist().equals(BaseConstants.ENABLED)) {
//            throw new OpenAlertException(String.format("保留数据,不允许修改"));
//        }
        // 生成新的密钥
        String secretKey = RandomValueUtils.randomAlphanumeric(32);
        appInfo.setSecretKey(secretKey);
//        appInfo.setUpdateTime(new Date());
        openAppRepository.save(appInfo);

//        jdbcClientDetailsService.updateClientSecret(appInfo.getApiKey(), secretKey);
        return secretKey;
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String apiKey = String.valueOf(RandomValueUtils.randomAlphanumeric(24));
        String secretKey = String.valueOf(RandomValueUtils.randomAlphanumeric(32));
        System.out.println("apiKey=" + apiKey);
        System.out.println("secretKey=" + secretKey);
        System.out.println("encodeSecretKey=" + encoder.encode(secretKey));
    }
}