package com.rapidark.cloud.base.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rapidark.cloud.base.client.model.entity.OpenApp;
import com.rapidark.cloud.base.client.service.IOpenAppServiceClient;
import com.rapidark.cloud.base.server.controller.cmd.CreateAppCommand;
import com.rapidark.cloud.base.server.controller.cmd.UpdateAppClientInfoCommand;
import com.rapidark.cloud.base.server.service.OpenAppService;
import com.rapidark.cloud.base.server.service.dto.OpenAppDto;
import com.rapidark.cloud.base.server.service.dto.OpenClientQueryCriteria;
import com.rapidark.common.model.ResultBody;
import com.rapidark.common.security.OpenClientDetails;
import com.rapidark.common.security.http.OpenRestTemplate;
import com.rapidark.common.utils.BeanConvertUtils;
import com.rapidark.common.utils.PageData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * 系统用户信息
 * @website http://rapidark.com
 * @author Darkness
 * @date 2022-05-25
 **/
@Api(tags = "系统应用管理")
@RequiredArgsConstructor
@RestController
public class OpenAppController implements IOpenAppServiceClient {

    private final OpenAppService openAppService;
    private final OpenRestTemplate openRestTemplate;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('openClient:list')")
    public void download(HttpServletResponse response, OpenClientQueryCriteria criteria) throws IOException {
        openAppService.download(openAppService.queryAll(criteria), response);
    }

    /**
     * 获取分页应用列表
     *
     * @return
     */
    @ApiOperation(value = "获取分页应用列表", notes = "获取分页应用列表")
    @GetMapping("/app")
    public ResultBody<IPage<OpenApp>> getAppListPage(@RequestParam(required = false) OpenClientQueryCriteria criteria, Pageable pageable) {
        PageData<OpenAppDto> data = openAppService.queryAll(criteria, pageable);
        return ResultBody.ok().data(data);
    }

    /**
     * 获取应用详情
     *
     * @param appId
     * @return
     */
    @ApiOperation(value = "获取应用详情", notes = "获取应用详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用ID", defaultValue = "1", required = true, paramType = "path"),
    })
    @GetMapping("/app/{appId}/info")
    @Override
    public ResultBody<OpenApp> getApp(
            @PathVariable("appId") String appId
    ) {
        OpenAppDto appInfo = openAppService.findById(appId);
        if(appInfo == null) {
            return ResultBody.failed().msg("该客户端不存在");
        }
        return ResultBody.ok().data(appInfo);
    }

    /**
     * 获取应用开发配置信息
     *
     * @param clientId
     * @return
     */
    @ApiOperation(value = "获取应用开发配置信息", notes = "获取应用开发配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "应用ID", defaultValue = "1", required = true, paramType = "path"),
    })
    @GetMapping("/app/client/{clientId}/info")
    @Override
    public ResultBody<OpenClientDetails> getAppClientInfo(
            @PathVariable("clientId") String clientId
    ) {
        OpenClientDetails clientInfo = openAppService.getAppClientInfo(clientId);
        return ResultBody.ok().data(clientInfo);
    }

    /**
     * 添加应用信息
     *
     * @return
     */
    @ApiOperation(value = "添加应用信息", notes = "添加应用信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appName", value = "应用名称", required = true, paramType = "form"),
            @ApiImplicitParam(name = "appNameEn", value = "应用英文名称", required = true, paramType = "form"),
            @ApiImplicitParam(name = "appType", value = "应用类型(server-应用服务 app-手机应用 pc-PC网页应用 wap-手机网页应用)", allowableValues = "server,app,pc,wap", required = true, paramType = "form"),
            @ApiImplicitParam(name = "appIcon", value = "应用图标", paramType = "form"),
            @ApiImplicitParam(name = "appOs", value = "手机应用操作系统", allowableValues = "android,ios", required = false, paramType = "form"),
            @ApiImplicitParam(name = "appDesc", value = "应用说明", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, defaultValue = "1", allowableValues = "0,1", value = "是否启用", paramType = "form"),
            @ApiImplicitParam(name = "website", value = "官网地址", paramType = "form"),
            @ApiImplicitParam(name = "developerId", value = "开发者", paramType = "form"),
            @ApiImplicitParam(name = "isSign", value = "是否开启验签", paramType = "form"),
            @ApiImplicitParam(name = "isEncrypt", value = "是否开启加密", paramType = "form"),
            @ApiImplicitParam(name = "encryptType", value = "加密类型", paramType = "form"),
            @ApiImplicitParam(name = "publicKey", value = "RSA公钥", paramType = "form")
    })
    @Log("添加应用信息")
    @PostMapping("/app/add")
    public ResultBody<String> addApp(@Validated @RequestBody CreateAppCommand command) {
        OpenApp app = new OpenApp();
        app.setAppName(command.getAppName());
        app.setAppNameEn(command.getAppNameEn());
        app.setAppType(command.getAppType());
        app.setAppOs(command.getAppOs());
        app.setAppIcon(command.getAppIcon());
        app.setAppDesc(command.getAppDesc());
        app.setStatus(command.getStatus());
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
        return ResultBody.ok().data(appId);
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
    @ApiOperation(value = "编辑应用信息", notes = "编辑应用信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用Id", required = true, paramType = "form"),
            @ApiImplicitParam(name = "appName", value = "应用名称", required = true, paramType = "form"),
            @ApiImplicitParam(name = "appNameEn", value = "应用英文名称", required = true, paramType = "form"),
            @ApiImplicitParam(name = "appType", value = "应用类型(server-应用服务 app-手机应用 pc-PC网页应用 wap-手机网页应用)", allowableValues = "server,app,pc,wap", required = true, paramType = "form"),
            @ApiImplicitParam(name = "appIcon", value = "应用图标", required = false, paramType = "form"),
            @ApiImplicitParam(name = "appOs", value = "手机应用操作系统", allowableValues = "android,ios", required = false, paramType = "form"),
            @ApiImplicitParam(name = "appDesc", value = "应用说明", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, defaultValue = "1", allowableValues = "0,1", value = "是否启用", paramType = "form"),
            @ApiImplicitParam(name = "website", value = "官网地址", paramType = "form"),
            @ApiImplicitParam(name = "developerId", value = "开发者", paramType = "form"),
            @ApiImplicitParam(name = "isSign", value = "是否开启验签", paramType = "form"),
            @ApiImplicitParam(name = "isEncrypt", value = "是否开启加密", paramType = "form"),
            @ApiImplicitParam(name = "encryptType", value = "加密类型", paramType = "form"),
            @ApiImplicitParam(name = "publicKey", value = "RSA公钥", paramType = "form")
    })
    @Log("编辑应用信息")
    @PostMapping("/app/update")
    public ResultBody updateApp(
            @RequestParam("appId") String appId,
            @RequestParam(value = "appName") String appName,
            @RequestParam(value = "appNameEn") String appNameEn,
            @RequestParam(value = "appType") String appType,
            @RequestParam(value = "appIcon", required = false) String appIcon,
            @RequestParam(value = "appOs", required = false) String appOs,
            @RequestParam(value = "appDesc", required = false) String appDesc,
            @RequestParam(value = "status", defaultValue = "1") Integer status,
            @RequestParam(value = "website", required = false) String website,
            @RequestParam(value = "developerId", required = false) Long developerId,
            @RequestParam(value = "isSign", required = false, defaultValue = "0") Integer isSign,
            @RequestParam(value = "isEncrypt", required = false, defaultValue = "0") Integer isEncrypt,
            @RequestParam(value = "encryptType", required = false, defaultValue = "") String encryptType,
            @RequestParam(value = "publicKey", required = false, defaultValue = "") String publicKey
    ) {
        OpenApp app = new OpenApp();
        app.setAppId(appId);
        app.setAppName(appName);
        app.setAppNameEn(appNameEn);
        app.setAppType(appType);
        app.setAppOs(appOs);
        app.setAppIcon(appIcon);
        app.setAppDesc(appDesc);
        app.setStatus(status);
        app.setWebsite(website);
        app.setDeveloperId(developerId);
        app.setIsSign(isSign);
        app.setIsEncrypt(isEncrypt);
        app.setEncryptType(encryptType);
        app.setPublicKey(publicKey);
        openAppService.update(app);
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }


    /**
     * 完善应用开发信息
     * @return
     */
    @ApiOperation(value = "完善应用开发信息", notes = "完善应用开发信息")
    @PostMapping("/app/client/update")
    public ResultBody<String> updateAppClientInfo(@Valid @RequestBody UpdateAppClientInfoCommand command) {
        OpenAppDto app = openAppService.findById(command.getAppId());
        OpenClientDetails client = new OpenClientDetails(app.getApiKey(), "",
                command.getScopes(), command.getGrantTypes(), "", command.getRedirectUrls());
        client.setAccessTokenValiditySeconds(command.getAccessTokenValidity());
        client.setRefreshTokenValiditySeconds(command.getRefreshTokenValidity());
        client.setAutoApproveScopes(command.getAutoApproveScopes() != null ? Arrays.asList(command.getAutoApproveScopes().split(",")) : null);
        Map info = BeanConvertUtils.objectToMap(app);
        client.setAdditionalInformation(info);
        openAppService.updateAppClientInfo(client);
        return ResultBody.ok();
    }

    /**
     * 重置应用秘钥
     *
     * @param appId 应用Id
     * @return
     */
    @ApiOperation(value = "重置应用秘钥", notes = "重置应用秘钥")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用Id", required = true, paramType = "form"),
    })
    @PostMapping("/app/reset")
    public ResultBody<String> resetAppSecret(
            @RequestParam("appId") String appId
    ) {
        String result = openAppService.restSecret(appId);
        return ResultBody.ok().data(result);
    }

    /**
     * 删除应用信息
     *
     * @param appId
     * @return
     */
    @Log("删除应用信息")
    @ApiOperation(value = "删除应用信息", notes = "删除应用信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用Id", required = true, paramType = "form"),
    })
    @PostMapping("/app/remove")
    public ResultBody removeApp(
            @RequestParam("appId") String appId
    ) {
        openAppService.removeApp(appId);
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }
}
