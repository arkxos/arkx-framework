//package com.rapidark.cloud.portal.uaa.server.controller;
//
//import com.rapidark.framework.common.model.ResultBody;
//import com.rapidark.framework.common.security.OpenHelper;
//import com.rapidark.framework.common.security.oauth2.client.OpenOAuth2ClientDetails;
//import com.rapidark.framework.common.security.oauth2.client.OpenOAuth2ClientProperties;
//
//
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.security.oauth2.common.OAuth2AccessToken;
////import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
////import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.security.Principal;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author: liuyadu
// * @date: 2018/11/9 15:43
// * @description:
// */
//@Schema(title = "用户认证中心")
//@RestController
//public class LoginController {
//    @Autowired
//    private OpenOAuth2ClientProperties clientProperties;
//    @Autowired
//    private TokenStore tokenStore;
//    @Autowired
//    AuthorizationServerEndpointsConfiguration endpoints;
//
//    /**
//     * 获取用户基础信息
//     *
//     * @return
//     */
//    @Schema(title = "获取用户基础信息")
//    @GetMapping("/current/user")
//    public ResultBody getUserProfile() {
//        return ResultBody.ok(OpenHelper.getUser());
//    }
//
//    /**
//     * 获取当前登录用户信息-SSO单点登录
//     *
//     * @param principal
//     * @return
//     */
//    @Schema(title = "获取当前登录用户信息-SSO单点登录", name = "获取当前登录用户信息-SSO单点登录")
//    @GetMapping("/current/user/sso")
//    public Principal principal(Principal principal) {
//        return principal;
//    }
//
//    /**
//     * 获取用户访问令牌
//     * 基于oauth2密码模式登录
//     *
//     * @param username
//     * @param password
//     * @return access_token
//     */
//    @Schema(title = "获取用户访问令牌", name = "基于oauth2密码模式登录,无需签名,返回access_token")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "username", required = true, value = "登录名", paramType = "form"),
//            @ApiImplicitParam(name = "password", required = true, value = "登录密码", paramType = "form")
//    })
//    @PostMapping("/login/token")
//    public ResultBody<OAuth2AccessToken> getLoginToken(@RequestParam String username, @RequestParam String password) throws Exception {
//        OAuth2AccessToken result = getToken(username, password, null);
//        return ResultBody.ok(result);
//    }
//
//    /**
//     * 退出移除令牌
//     *
//     * @param token
//     */
//    @Schema(title = "退出移除令牌", name = "退出移除令牌")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "token", required = true, value = "访问令牌", paramType = "form")
//    })
//    @PostMapping("/logout/token")
//    public ResultBody removeToken(@RequestParam String token) {
//        tokenStore.removeAccessToken(tokenStore.readAccessToken(token));
//        return ResultBody.ok();
//    }
//
//
//    /**
//     * 生成 oauth2 token
//     *
//     * @param userName
//     * @param password
//     * @param type
//     * @return
//     */
//    public OAuth2AccessToken getToken(String userName, String password, String type) throws Exception {
//        OpenOAuth2ClientDetails clientDetails = clientProperties.getOauth2().get("portal");
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
//}
