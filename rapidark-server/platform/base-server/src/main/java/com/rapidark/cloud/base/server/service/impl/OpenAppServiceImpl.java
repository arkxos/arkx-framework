package com.rapidark.cloud.base.server.service.impl;

import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.base.client.model.entity.OpenApp;
import com.rapidark.cloud.base.server.repository.OpenAppRepository;
import com.rapidark.cloud.base.server.service.OpenAppService;
import com.rapidark.cloud.base.server.service.BaseAuthorityService;
import com.rapidark.cloud.base.server.service.dto.OpenAppDto;
import com.rapidark.cloud.base.server.service.dto.OpenClientQueryCriteria;
import com.rapidark.cloud.base.server.service.mapstruct.OpenAppMapper;
import com.rapidark.common.exception.OpenAlertException;
import com.rapidark.common.security.OpenClientDetails;
import com.rapidark.common.utils.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.QueryHelp;
import me.zhengjie.utils.ValidationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author: liuyadu
 * @date: 2018/11/12 16:26
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class OpenAppServiceImpl implements OpenAppService {
    /**
     * token有效期，默认12小时
     */
    public static final int ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 12;
    /**
     * token有效期，默认7天
     */
    public static final int REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 7;

    private final OpenAppRepository openAppRepository;
    private final OpenAppMapper openAppMapper;

    private final BaseAuthorityService baseAuthorityService;
    private final JdbcClientDetailsService jdbcClientDetailsService;

    @Override
    public PageData<OpenAppDto> queryAll(OpenClientQueryCriteria criteria, Pageable pageable){
        Page<OpenApp> page = openAppRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPageData(page.map(openAppMapper::toDto));
    }

    @Override
    public List<OpenAppDto> queryAll(OpenClientQueryCriteria criteria){
        return openAppMapper.toDto(openAppRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    /**
     * 获取app详情
     *
     * @param appId
     * @return
     */
    @Cacheable(value = "apps", key = "#appId")
    @Override
    @Transactional
    public OpenAppDto findById(String appId) {
        OpenApp openClient = openAppRepository.findById(appId).orElseGet(OpenApp::new);
        ValidationUtil.isNull(openClient.getAppId(),"OpenClient","appId",appId);
        return openAppMapper.toDto(openClient);
    }

    /**
     * 添加应用
     *
     * @param resources
     * @return 应用信息
     */
    @CachePut(value = "apps", key = "#app.appId")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OpenAppDto create(OpenApp resources) {
        String appId = UuidUtil.base58Uuid();
        String apiKey = RandomValueUtils.randomAlphanumeric(24);
        String secretKey = RandomValueUtils.randomAlphanumeric(32);
        resources.setAppId(appId);
        resources.setApiKey(apiKey);
        resources.setSecretKey(secretKey);
        if (resources.getIsPersist() == null) {
            resources.setIsPersist(0);
        }

        OpenApp saved = openAppRepository.save(resources);

        Map info = BeanConvertUtils.objectToMap(saved);
        // 功能授权
        BaseClientDetails client = new BaseClientDetails();
        client.setClientId(saved.getApiKey());
        client.setClientSecret(saved.getSecretKey());
        client.setAdditionalInformation(info);
        client.setAuthorizedGrantTypes(Arrays.asList("authorization_code", "client_credentials", "implicit", "refresh_token"));
        client.setAccessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
        client.setRefreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
        jdbcClientDetailsService.addClientDetails(client);

        return openAppMapper.toDto(saved);
    }

    /**
     * 修改应用
     *
     * @param app 应用
     * @return 应用信息
     */
    @Caching(evict = {
            @CacheEvict(value = {"apps"}, key = "#app.appId"),
            @CacheEvict(value = {"apps"}, key = "'client:'+#app.appId")
    })
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OpenApp app) {
        OpenApp openClient = openAppRepository.findById(app.getAppId()).orElseGet(OpenApp::new);
        ValidationUtil.isNull( openClient.getAppId(),"OpenClient","id",app.getAppId());
        openClient.copy(app);
        openAppRepository.save(openClient);

        // 修改客户端附加信息
//        OpenClientDto appInfo = this.findById(app.getAppId());
        Map info = BeanConvertUtils.objectToMap(openClient);
        BaseClientDetails client = (BaseClientDetails) jdbcClientDetailsService.loadClientByClientId(openClient.getApiKey());
        client.setAdditionalInformation(info);
        jdbcClientDetailsService.updateClientDetails(client);
    }

    @Override
    public void deleteAll(String[] ids) {
        for (String appId : ids) {
            removeApp(appId);
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
    public void removeApp(String appId) {
        Optional<OpenApp> appInfoOptional = openAppRepository.findById(appId);
        if (appInfoOptional.isEmpty()) {
            throw new OpenAlertException(appId + "应用不存在!");
        }
        OpenApp appInfo = appInfoOptional.get();
        if (appInfo.getIsPersist().equals(BaseConstants.ENABLED)) {
            throw new OpenAlertException(String.format("保留数据,不允许删除"));
        }
        // 移除应用权限
        baseAuthorityService.removeAuthorityApp(appId);
        openAppRepository.deleteById(appInfo.getAppId());
        jdbcClientDetailsService.removeClientDetails(appInfo.getApiKey());
    }

    @Override
    public void download(List<OpenAppDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OpenAppDto openClient : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("更新人", openClient.getUpdateBy());
            map.put("创建人", openClient.getCreateBy());
            map.put("API访问key", openClient.getApiKey());
            map.put("API访问密钥", openClient.getSecretKey());
            map.put("app名称", openClient.getAppName());
            map.put("app英文名称", openClient.getAppNameEn());
            map.put("应用图标", openClient.getAppIcon());
            map.put("app类型:server-服务应用 app-手机应用 pc-PC网页应用 wap-手机网页应用", openClient.getAppType());
            map.put("app描述", openClient.getAppDesc());
            map.put("移动应用操作系统:ios-苹果 android-安卓", openClient.getAppOs());
            map.put("官网地址", openClient.getWebsite());
            map.put("开发者ID:默认为0", openClient.getDeveloperId());
            map.put("创建时间", openClient.getCreateTime());
            map.put("更新时间", openClient.getUpdateTime());
            map.put("状态:0-无效 1-有效", openClient.getStatus());
            map.put("保留数据0-否 1-是 不允许删除", openClient.getIsPersist());
            map.put("是否验签:0-否 1-是 不允许删除", openClient.getIsSign());
            map.put("是否加密:0-否 1-是 不允许删除", openClient.getIsEncrypt());
            map.put("加密类型:DES TripleDES AES RSA", openClient.getEncryptType());
            map.put("RSA加解密公钥", openClient.getPublicKey());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 获取app和应用信息
     *
     * @return
     */
    @Override
    @Cacheable(value = "apps", key = "'client:'+#clientId")
    public OpenClientDetails getAppClientInfo(String clientId) {
        BaseClientDetails baseClientDetails = null;
        try {
            baseClientDetails = (BaseClientDetails) jdbcClientDetailsService.loadClientByClientId(clientId);
        } catch (Exception e) {
            return null;
        }
        String appId = baseClientDetails.getAdditionalInformation().get("appId").toString();
        OpenClientDetails openClient = new OpenClientDetails();
        BeanUtils.copyProperties(baseClientDetails, openClient);
        openClient.setAuthorities(baseAuthorityService.findAuthorityByApp(appId));
        return openClient;
    }

    /**
     * 更新应用开发信息
     *
     * @param client
     */
    @CacheEvict(value = {"apps"}, key = "'client:'+#client.clientId")
    @Override
    public void updateAppClientInfo(OpenClientDetails client) {
        jdbcClientDetailsService.updateClientDetails(client);
    }

    /**
     * 重置秘钥
     *
     * @param appId
     * @return
     */
    @Override
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
        if (appInfo.getIsPersist().equals(BaseConstants.ENABLED)) {
            throw new OpenAlertException(String.format("保留数据,不允许修改"));
        }
        // 生成新的密钥
        String secretKey = RandomValueUtils.randomAlphanumeric(32);
        appInfo.setSecretKey(secretKey);
//        appInfo.setUpdateTime(new Date());
        openAppRepository.save(appInfo);

        jdbcClientDetailsService.updateClientSecret(appInfo.getApiKey(), secretKey);
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
