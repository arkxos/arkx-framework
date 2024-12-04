package com.rapidark.cloud.uaa.admin.server.controller;

import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.security.OpenHelper;
import com.rapidark.framework.common.security.OpenUserDetails;
import com.rapidark.framework.common.security.oauth2.client.OpenOAuth2ClientProperties;
import com.rapidark.framework.common.utils.BeanConvertUtils;
import com.rapidark.cloud.uaa.admin.server.service.feign.BaseUserServiceClient;
import com.rapidark.boot.RsaProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
//import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author darkness
 * @date 2021/5/25 13:44
 * @version 1.0
 */
@Schema(title = "用户认证中心")
@RestController
public class LoginController {

    @Autowired
    private OpenOAuth2ClientProperties clientProperties;
//    @Autowired
//    private TokenStore tokenStore;
//    @Autowired
//    AuthorizationServerEndpointsConfiguration endpoints;
    @Autowired
    private BaseUserServiceClient baseUserServiceClient;
    @Autowired
    private RsaProperties rsaProperties;

    /**
     * 获取用户基础信息
     *
     * @return
     */
    @Schema(title = "获取当前登录用户信息", name = "获取当前登录用户信息")
    @GetMapping("/current/user")
    public ResponseResult getUserProfile() {
        OpenUserDetails user = OpenHelper.getUser();
        Map map = BeanConvertUtils.objectToMap(user);
        map.put("roles", baseUserServiceClient.getUserRoles(user.getUserId()).getData());
        return ResponseResult.ok(map);
    }

    /**
     * 获取当前登录用户信息-SSO单点登录
     *
     * @param principal
     * @return
     */
//    @Schema(value = "获取当前登录用户信息-SSO单点登录", name = "获取当前登录用户信息-SSO单点登录")
//    @GetMapping("/current/user/sso")
//    public Principal principal(Principal principal) {
//        return principal;
//    }

    /**
     * 获取公钥
     * 基于oauth2密码模式登录
     *
     * @return publicKey
     */
//    @Schema(value = "获取公钥", name = "获取公钥")
//    @GetMapping("/publicKey")
//    public ResultBody<String> getPublicKey() {
//        String result = rsaProperties.getPublicKey();
//        return ResultBody.ok(result);
//    }

    /**
     * 获取用户访问令牌
     * 基于oauth2密码模式登录
     *
     * @return access_token
     */
//    @ApiEncrypt
//    @Schema(value = "登录获取用户访问令牌", name = "基于oauth2密码模式登录,无需签名,返回access_token")
//    @PostMapping("/login/token")
//    public ResultBody<OAuth2AccessToken> getLoginToken(@Valid @RequestBody LoginInfo loginInfo) throws Exception {
////        String loginInfoString = RSAUtils.decryptByPrivateKey(command.getLoginInfo(), rsaProperties.getPrivateKey());
////        LoginInfo loginInfo = JSON.parseObject(loginInfoString, LoginInfo.class);
//        OAuth2AccessToken result = getFrontToken(loginInfo.getUsername(), loginInfo.getPassword(), null);
//        return ResultBody.ok(result);
//    }

    /**
     * 获取用户访问令牌
     * 基于oauth2密码模式登录
     *
     * @return access_token
     */
//    @Schema(value = "第三方客户端系统登录获取用户访问令牌", name = "基于oauth2密码模式登录,无需签名,返回access_token")
//    @PostMapping("/login/thirdpart/token")
//    public ResultBody<OAuth2AccessToken> getThirtpartLoginToken(@Valid @RequestBody ThirdpartSystemLoginCommand command) throws Exception {
//        // 使用oauth2密码模式登录.
//        Map<String, String> postParameters = new HashMap<>();
//        postParameters.put("client_id", command.getClientId());
//        postParameters.put("client_secret", command.getClientSecret());
//        postParameters.put("grant_type", "client_credentials");
//        OAuth2AccessToken result = OpenHelper.createAccessToken(endpoints, postParameters);
//        return ResultBody.ok(result);
//    }

    /**
     * 退出移除令牌
     *
     */
//    @Schema(value = "退出并移除令牌", name = "退出并移除令牌,令牌将失效")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "token", required = true, value = "访问令牌", paramType = "form")
//    })
//    @PostMapping("/logout/token")
//    public ResultBody removeToken(@Valid @RequestBody LogoutCommand command) {
//        tokenStore.removeAccessToken(tokenStore.readAccessToken(command.getToken()));
//        return ResultBody.ok();
//    }

    /**
     * 生成 oauth2 token
     *
     * @param userName
     * @param password
     * @param type
     * @return
     */
//    public OAuth2AccessToken getFrontToken(String userName, String password, String type) throws Exception {
//        OpenOAuth2ClientDetails clientDetails = clientProperties.getOauth2().get("admin");
//        // 使用oauth2密码模式登录.
//        Map<String, String> postParameters = new HashMap<>();
//        postParameters.put("username", userName);
//        postParameters.put("password", password);
//        postParameters.put("client_id", clientDetails.getClientId());
//        postParameters.put("client_secret", clientDetails.getClientSecret());
//        postParameters.put("grant_type", "password");
//        // 添加参数区分,第三方登录
//        postParameters.put("login_type", type);
//        return OpenHelper.createAccessToken(endpoints, postParameters);
//    }
}
