package com.rapidark.cloud.base.server.controller;

import com.rapidark.cloud.base.client.model.entity.OpenApp;
import com.rapidark.cloud.base.client.service.IOpenAppServiceClient;
import com.rapidark.cloud.base.server.controller.cmd.CreateOpenAppCommand;
import com.rapidark.cloud.base.server.controller.cmd.UpdateAppClientInfoCommand;
import com.rapidark.cloud.base.server.controller.cmd.UpdateOpenClientCommand;
import com.rapidark.cloud.base.server.repository.OpenAppRepository;
import com.rapidark.cloud.base.server.service.BaseAuthorityService;
import com.rapidark.cloud.base.server.service.OpenAppService;
import com.rapidark.cloud.base.client.service.dto.OpenAppDto;
import com.rapidark.cloud.base.client.service.dto.OpenClientQueryCriteria;
import com.rapidark.cloud.platform.gateway.framework.bean.GatewayAppRouteRegServer;
import com.rapidark.cloud.platform.gateway.framework.service.ClientServerRegisterService;
import com.rapidark.cloud.platform.gateway.framework.service.CustomNacosConfigService;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.security.OpenAuthority;
import com.rapidark.framework.common.security.OpenClientDetails;
//import com.rapidark.framework.common.security.http.OpenRestTemplate;
import com.rapidark.framework.common.utils.BeanConvertUtils;
import com.rapidark.framework.common.utils.PageResult;
import com.rapidark.framework.data.jpa.entity.Status;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import com.rapidark.framework.common.annotation.Log;
import com.rapidark.framework.common.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * 系统用户信息
 * @website http://rapidark.com
 * @author Darkness
 * @date 2022-05-25
 **/
@Schema(title = "系统应用管理")
@RequiredArgsConstructor
@RestController
public class OpenAppController implements IOpenAppServiceClient {

    private final OpenAppService openAppService;
//    private final OpenRestTemplate openRestTemplate;
    private final CustomNacosConfigService customNacosConfigService;
    private final ClientServerRegisterService clientServerRegisterService;
    private final BaseAuthorityService baseAuthorityService;
    private final OpenAppRepository openAppRepository;

    @Log("导出数据")
    @Schema(title = "导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('openClient:list')")
    public void download(HttpServletResponse response, OpenClientQueryCriteria criteria) throws IOException {
        download(openAppService.queryAll(criteria), response);
    }

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
//            map.put("保留数据0-否 1-是 不允许删除", openClient.getIsPersist());
            map.put("是否验签:0-否 1-是 不允许删除", openClient.getIsSign());
            map.put("是否加密:0-否 1-是 不允许删除", openClient.getIsEncrypt());
            map.put("加密类型:DES TripleDES AES RSA", openClient.getEncryptType());
            map.put("RSA加解密公钥", openClient.getPublicKey());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 获取分页应用列表
     *
     * @return
     */
    @Schema(title = "获取分页应用列表", name = "获取分页应用列表")
    @GetMapping("/app")
    public ResponseResult<PageResult<OpenAppDto>> getAppListPage(@RequestParam(required = false) OpenClientQueryCriteria criteria, Pageable pageable) {
		PageResult<OpenAppDto> data = openAppService.queryAll(criteria, pageable);
        return ResponseResult.ok(data);
    }

    /**
     * 获取应用详情
     *
     * @param appId
     * @return
     */
    @Schema(title = "获取应用详情", name = "获取应用详情")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "appId", value = "应用ID", defaultValue = "1", required = true, paramType = "path"),
//    })
    @GetMapping("/app/{appId}/info")
    @Override
    public ResponseResult<OpenApp> getApp(
            @PathVariable("appId") String appId
    ) {
        OpenApp appInfo = openAppService.findById(appId);
        if(appInfo == null) {
            return ResponseResult.failed("该客户端不存在");
        }
        return ResponseResult.ok(appInfo);
    }

    /**
     * 获取应用详情
     *
     * @param ip
     * @return
     */
    @Schema(title = "获取应用详情", name = "获取应用详情")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "ip", value = "应用ID", defaultValue = "1", required = true, paramType = "path"),
//    })
    @GetMapping("/openClient/queryOpenClientByIp")
    @Override
    public ResponseResult<OpenApp> queryAppByIp(@RequestParam("ip") String ip) {
        Optional<OpenApp> openClientOptional = openAppRepository.findByIp(ip);
        if(openClientOptional.isEmpty()) {
            return ResponseResult.failed("该客户端不存在");
        }
        OpenApp openClient = openClientOptional.get();
        return ResponseResult.ok(openClient);
    }

    /**
     * 获取应用开发配置信息
     *
     * @param clientId
     * @return
     */
    @Schema(title = "获取应用开发配置信息", name = "获取应用开发配置信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "clientId", value = "应用ID", defaultValue = "1", required = true, paramType = "path"),
//    })
    @GetMapping("/app/client/{clientId}/info")
    @Override
    public ResponseResult<OpenClientDetails> getAppClientInfo(
            @PathVariable("clientId") String clientId
    ) {
        OpenClientDetails clientInfo = openAppService.getAppClientInfo(clientId);
        return ResponseResult.ok(clientInfo);
    }

    /**
     * 添加应用信息
     *
     * @return
     */
    @Schema(title = "添加应用信息", name = "添加应用信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "appName", value = "应用名称", required = true, paramType = "form"),
//            @ApiImplicitParam(name = "appNameEn", value = "应用英文名称", required = true, paramType = "form"),
//            @ApiImplicitParam(name = "appType", value = "应用类型(server-应用服务 app-手机应用 pc-PC网页应用 wap-手机网页应用)", allowableValues = "server,app,pc,wap", required = true, paramType = "form"),
//            @ApiImplicitParam(name = "appIcon", value = "应用图标", paramType = "form"),
//            @ApiImplicitParam(name = "appOs", value = "手机应用操作系统", allowableValues = "android,ios", required = false, paramType = "form"),
//            @ApiImplicitParam(name = "appDesc", value = "应用说明", paramType = "form"),
//            @ApiImplicitParam(name = "status", required = true, defaultValue = "1", allowableValues = "0,1", value = "是否启用", paramType = "form"),
//            @ApiImplicitParam(name = "website", value = "官网地址", paramType = "form"),
//            @ApiImplicitParam(name = "developerId", value = "开发者", paramType = "form"),
//            @ApiImplicitParam(name = "isSign", value = "是否开启验签", paramType = "form"),
//            @ApiImplicitParam(name = "isEncrypt", value = "是否开启加密", paramType = "form"),
//            @ApiImplicitParam(name = "encryptType", value = "加密类型", paramType = "form"),
//            @ApiImplicitParam(name = "publicKey", value = "RSA公钥", paramType = "form")
//    })
    @Log("添加应用信息")
    @PostMapping("/app/add")
    public ResponseResult<String> addApp(@Validated @RequestBody CreateOpenAppCommand command) {
        OpenApp app = new OpenApp();
        app.setAppName(command.getAppName());
        app.setAppNameEn(command.getAppNameEn());
        app.setGroupCode(command.getGroupCode());
        app.setIp(command.getIp());
        app.setAppType(command.getAppType());
        app.setAppOs(command.getAppOs());
        app.setAppIcon(command.getAppIcon());
        app.setAppDesc(command.getAppDesc());
        app.setStatus(Status.codeOf(command.getStatus()));
        app.setWebsite(command.getWebsite());
        app.setDeveloperId(command.getDeveloperId());
        app.setIsSign(command.getIsSign());
        app.setIsEncrypt(command.getIsEncrypt());
        app.setEncryptType(command.getEncryptType());
        app.setPublicKey(command.getPublicKey());
        OpenAppDto result = openAppService.create(app);
        String appId = null;
        if (result != null) {
            appId = result.getAppId();
        }
        customNacosConfigService.publishClientNacosConfig(app.getAppId());

        return ResponseResult.ok(appId);
    }

    /**
     * 编辑应用信息
     *
     * @param appId
     * @param appName     应用名称
     * @param appNameEn   应用英文名称
     * @param appOs       手机应用操作系统:ios-苹果 android-安卓
     * @param appType     应用类型:server-应用服务 app-手机应用 pc-PC网页应用 wap-手机网页应用
     * @param appIcon     应用图标
     * @param appDesc     应用说明
     * @param status      状态
     * @param website     官网地址
     * @param developerId 开发者
     * @return
     * @
     */
    @Schema(title = "编辑应用信息", name = "编辑应用信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "appId", value = "应用Id", required = true, paramType = "form"),
//            @ApiImplicitParam(name = "appName", value = "应用名称", required = true, paramType = "form"),
//            @ApiImplicitParam(name = "appNameEn", value = "应用英文名称", required = true, paramType = "form"),
//            @ApiImplicitParam(name = "appType", value = "应用类型(server-应用服务 app-手机应用 pc-PC网页应用 wap-手机网页应用)", allowableValues = "server,app,pc,wap", required = true, paramType = "form"),
//            @ApiImplicitParam(name = "appIcon", value = "应用图标", required = false, paramType = "form"),
//            @ApiImplicitParam(name = "appOs", value = "手机应用操作系统", allowableValues = "android,ios", required = false, paramType = "form"),
//            @ApiImplicitParam(name = "appDesc", value = "应用说明", paramType = "form"),
//            @ApiImplicitParam(name = "status", required = true, defaultValue = "1", allowableValues = "0,1", value = "是否启用", paramType = "form"),
//            @ApiImplicitParam(name = "website", value = "官网地址", paramType = "form"),
//            @ApiImplicitParam(name = "developerId", value = "开发者", paramType = "form"),
//            @ApiImplicitParam(name = "isSign", value = "是否开启验签", paramType = "form"),
//            @ApiImplicitParam(name = "isEncrypt", value = "是否开启加密", paramType = "form"),
//            @ApiImplicitParam(name = "encryptType", value = "加密类型", paramType = "form"),
//            @ApiImplicitParam(name = "publicKey", value = "RSA公钥", paramType = "form")
//    })
    @Log("编辑应用信息")
    @PostMapping("/app/update")
    public ResponseResult updateApp(@Validated @RequestBody UpdateOpenClientCommand command) {
        OpenApp app = new OpenApp();
        app.setAppId(command.getAppId());
        app.setAppName(command.getAppName());
        app.setAppNameEn(command.getAppNameEn());
        app.setAppType(command.getAppType());
        app.setGroupCode(command.getGroupCode());
        app.setIp(command.getIp());
        app.setAppOs(command.getAppOs());
        app.setAppIcon(command.getAppIcon());
        app.setAppDesc(command.getAppDesc());
        app.setStatus(Status.codeOf(command.getStatus()));
        app.setWebsite(command.getWebsite());
        app.setDeveloperId(command.getDeveloperId());
        app.setIsSign(command.getIsSign());
        app.setIsEncrypt(command.getIsEncrypt());
        app.setEncryptType(command.getEncryptType());
        app.setPublicKey(command.getPublicKey());
        openAppService.update(app);
        // openRestTemplate.refreshGateway();
        customNacosConfigService.publishClientNacosConfig(app.getAppId());

        return ResponseResult.ok();
    }


    /**
     * 完善应用开发信息
     * @return
     */
    @Schema(title = "完善应用开发信息", name = "完善应用开发信息")
    @PostMapping("/app/client/update")
    public ResponseResult<String> updateAppClientInfo(@Valid @RequestBody UpdateAppClientInfoCommand command) {
        OpenApp app = openAppService.findById(command.getAppId());
        OpenClientDetails client = new OpenClientDetails(app.getApiKey(), "",
                command.getScopes(), command.getGrantTypes(), "", command.getRedirectUrls());
        client.setAccessTokenValiditySeconds(command.getAccessTokenValidity());
        client.setRefreshTokenValiditySeconds(command.getRefreshTokenValidity());
        client.setAutoApproveScopes(command.getAutoApproveScopes() != null ? Arrays.asList(command.getAutoApproveScopes().split(",")) : null);
        Map info = BeanConvertUtils.objectToMap(app);
        client.setAdditionalInformation(info);
        openAppService.updateAppClientInfo(client);
        return ResponseResult.ok();
    }

    /**
     * 重置应用秘钥
     *
     * @param appId 应用Id
     * @return
     */
    @Schema(title = "重置应用秘钥", name = "重置应用秘钥")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "appId", value = "应用Id", required = true, paramType = "form"),
//    })
    @PostMapping("/app/reset")
    public ResponseResult<String> resetAppSecret(
            @RequestParam("appId") String appId
    ) {
        String result = openAppService.restSecret(appId);
        return ResponseResult.ok(result);
    }

    /**
     * 删除应用信息
     *
     * @param appId
     * @return
     */
    @Log("删除应用信息")
    @Schema(title = "删除应用信息", name = "删除应用信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "appId", value = "应用Id", required = true, paramType = "form"),
//    })
    @PostMapping("/app/remove")
    public ResponseResult removeApp(
            @RequestParam("appId") String appId
    ) {
        openAppService.deleteById(appId);
        // openRestTemplate.refreshGateway();
        customNacosConfigService.publishClientNacosConfig(appId);
        return ResponseResult.ok();
    }

    /**
     * 设置客户端状态为启用
     * @param id
     * @return
     */
    @RequestMapping(value = "/start", method = {RequestMethod.POST})
    public ResponseResult start(@RequestParam String id) {
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        OpenApp dbClient = openAppService.findById(id);
        dbClient.setStatus(Status.ENABLED);
        openAppService.update(dbClient);
        customNacosConfigService.publishClientNacosConfig(id);
        return ResponseResult.ok();
    }

    /**
     * 设置客户端状态为禁用
     * @param id
     * @return
     */
    @RequestMapping(value = "/stop", method = {RequestMethod.POST})
    public ResponseResult stop(@RequestParam String id) {
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        OpenApp dbClient = openAppService.findById(id);
        dbClient.setStatus(Status.DISABLED);
        openAppService.update(dbClient);
        customNacosConfigService.publishClientNacosConfig(id);
        return ResponseResult.ok();
    }

    /**
     * 获取网关路由服务列表
     */
    @Override
    @GetMapping(value = "/openClient/queryClientRegisterAppsByAppId")
    public ResponseResult<List<GatewayAppRouteRegServer>> queryClientRegisterAppsByAppId(@RequestParam("clientId") String clientId) {
        Assert.isTrue(StringUtils.isNotBlank(clientId), "未获取到对象查询ID");
        List<GatewayAppRouteRegServer> data = clientServerRegisterService.queryClientRegisterAppsByAppId(clientId);
        for(GatewayAppRouteRegServer regServer : data) {
            String systemCode = regServer.getSystemCode();
            List<OpenAuthority> authrities = baseAuthorityService.findAuthorityByApp(clientId, systemCode);
            regServer.setAuthorities(authrities);
        }
        return ResponseResult.ok(data);
    }

}
