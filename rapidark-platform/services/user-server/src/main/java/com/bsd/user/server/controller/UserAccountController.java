package com.bsd.user.server.controller;

import com.bsd.user.server.constants.UserConstants;
import com.bsd.user.server.model.UserAccountPo;
import com.bsd.user.server.model.dto.UserAccountDTO;
import com.bsd.user.server.service.UserAccountService;
import com.bsd.user.server.service.WechatAuthService;
import com.google.common.collect.Maps;
import com.rapidark.framework.common.model.ResultBody;
import com.rapidark.framework.common.security.oauth2.client.OpenOAuth2ClientDetails;
import com.rapidark.framework.common.security.oauth2.client.OpenOAuth2Service;
import com.rapidark.framework.common.utils.BeanConvertUtils;
import com.rapidark.framework.common.utils.WebUtils;



import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 用户-第三方账号 前端控制器
 *
 * @author lisongmao
 * @date 2019-06-26
 */
@RestController
@RequestMapping("/user/third")
@Schema(title = "第三方渠道登录")
public class UserAccountController {
    @Autowired
    @Qualifier("qqAuthServiceImpl")
    private OpenOAuth2Service qqAuthService;

    @Autowired
    Map<String, WechatAuthService> wechatAuthServiceMap;


    @Autowired
    private UserAccountService userAccountService;

    /**
     * 第三方qq账号登陆回调
     *
     * @param code
     * @return
     */
    @Schema(title = "qq账号登录回调", name = "qq回调给客户端，客户端调用服务的此接口,服务端返回qq账号是否绑定用户中心账号信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "code", required = true, value = "qq服务回调带的code参数", paramType = "form"),
//    })
    @PostMapping("/qq/callback")
    public ResultBody callbackByQQ(@RequestParam(value = "code", required = true) String code) {
        return ResultBody.ok(userAccountService.isBindingsByQq(code));
    }

    /**
     * 第三方微信账号登录回调
     *
     * @param code
     * @return
     */
    @Schema(title = "微信账号登录回调", name = "微信回调给客户端，客户端调用服务的此接口,服务端返回微信账号是否绑定用户中心账号信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "code", required = true, value = "回调带的code参数", paramType = "form"),
//            @ApiImplicitParam(name = "platform", required = true, value = "第三方登录平台,1-微信移动应用 、2-微信网站应用、3-微信公众号 4-日历小程序", paramType = "form"),
//    })
    @GetMapping("/wechat/callback")
    public ResultBody callbackByWechat(@RequestParam(value = "code") String code,
                                       @RequestParam(value = "platform", required = false) Integer platform) {
        //platform不传，设置默认值
        if (platform == null) {
            platform = UserConstants.PLATFORM_WECHAT_PC;
        }
        //校验platform
        if (!Arrays.asList(UserConstants.PLATFORM_ALL).contains(platform)) {
            return ResultBody.failed("第三方平台参数错误");
        }
        if (userAccountService.isBindingsByWechat(code, platform)) {
            return ResultBody.ok();
        }
        return ResultBody.failed("您暂未绑定用户中心账号，请前往绑定");
    }


    /**
     * 第三方账号登录
     *
     * @param code qq服务回调带的code参数
     * @return
     */
    @Schema(title = "第三方账号登录token获取", name = "第三方账号登录token获取")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "code", required = true, value = "第三方服务回调带的code参数", paramType = "form"),
//            @ApiImplicitParam(name = "platform", required = true, value = "第三方登录平台,1-微信移动应用 、2-微信网站应用、3-微信公众号、4-微信小程序、5-QQ、6-微博", paramType = "form"),
//    })
    @PostMapping("/login")
    public ResultBody loginByThirdPlatform(@RequestParam(value = "code", required = true) String code,
                                           @RequestParam(value = "platform", required = true) Integer platform,
                                           HttpServletRequest request) {
        UserAccountPo accoutPo = new UserAccountPo();
        accoutPo.setCode(code);
        accoutPo.setPlatform(platform);
        accoutPo.setLoginIp(WebUtils.getRemoteAddress(request));
        return ResultBody.ok(userAccountService.loginByThirdPlatform(accoutPo));
    }

    /**
     * 第三方账号绑定用户中心账号
     *
     * @param code
     * @param mobile
     * @param password
     * @return
     */

    @Schema(title = "第三方账号绑定用户中心账号", name = "第三方账号绑定用户中心账号")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "code", required = true, value = "第三方服务回调带的code参数", paramType = "form"),
//            @ApiImplicitParam(name = "mobile", required = true, value = "手机号", paramType = "form"),
//            @ApiImplicitParam(name = "mobileCode", required = true, value = "手机验证码", paramType = "form"),
//            @ApiImplicitParam(name = "password", required = true, value = "密码", paramType = "form"),
//            @ApiImplicitParam(name = "platform", required = true, value = "第三方登录平台,1-微信移动应用 、2-微信网站应用、3-微信公众号、4-微信小程序、5-QQ、6-微博", allowableValues = "1,2,3,4,5,6", paramType = "form")
//    })
    @PostMapping("/bind")
    public ResultBody bindingsUser(@RequestParam(value = "code", required = true) String code,
                                   @RequestParam(value = "mobile", required = true) String mobile,
                                   @RequestParam(value = "password", required = true) String password,
                                   @RequestParam(value = "platform", required = true) Integer platform,
                                   @RequestParam(value = "mobileCode", required = true) String mobileCode,
                                   HttpServletRequest request
    ) {
        UserAccountPo accountPo = new UserAccountPo();
        accountPo.setCode(code);
        accountPo.setMobile(mobile);
        accountPo.setPassword(password);
        accountPo.setPlatform(platform);
        accountPo.setMobileCode(mobileCode);
        accountPo.setLoginIp(WebUtils.getRemoteAddress(request));
        userAccountService.bindingsUser(accountPo);
        return ResultBody.ok();
    }


    @Schema(title = "解绑第三方账号", name = "解绑第三方账号")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "token", required = true, value = "登录token", paramType = "header"),
//            @ApiImplicitParam(name = "sessionId", required = true, value = "登录会话id", paramType = "header"),
//            @ApiImplicitParam(name = "platform", required = true, value = "第三方登录平台,1-微信移动应用 、2-微信网站应用、3-微信公众号、4-微信小程序、5-QQ、6-微博", allowableValues = "1,2,3,4", paramType = "form")
//    })
    @PostMapping("/unbind")
    public ResultBody unBindingsUser(@RequestParam(value = "platform", required = true) Integer platform,
                                     HttpServletRequest request) {
        String LoginMoblie = (String) request.getAttribute(UserConstants.LOGIN_MOBILE);
        userAccountService.unbindingsUser(LoginMoblie, platform);
        return ResultBody.ok();
    }


    @Schema(title = "获取用户第三方账号信息", name = "获取用户第三方账号信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "uids", required = true, value = "用户ID,多个使用,分割开", paramType = "form"),})
    @PostMapping("/getByUserId")
    public ResultBody getUserAccountByUserId(@RequestParam(value = "uids", required = true) String uids) {
        List<String> idList = Arrays.asList(uids.split(","));
        if (idList == null || idList.size() == 0) {
            return ResultBody.failed("用户ID列表不能为空");
        }
        List<UserAccountDTO> users = BeanConvertUtils.copyList(userAccountService.selectBatchUserIds(idList), UserAccountDTO.class);
        return ResultBody.ok(users);
    }


    /**
     * 获取第三方登录配置
     *
     * @return
     */
    @Schema(title = "获取第三方登录URL", name = "获取第三方登录URL")
    @GetMapping("/login/url")
    @ResponseBody
    public ResultBody getLoginThirdConfig() {
        Map<String, String> map = Maps.newHashMap();
        map.put("qq", qqAuthService.getAuthorizationUrl());
        //添加微信授权配置
        wechatAuthServiceMap.forEach((k, v) -> {
            map.put(v.getConfigTag(), v.getAuthorizationUrl());
        });
        return ResultBody.ok(map);
    }

    /**
     * 获取第三方登录配置
     *
     * @return
     */
    @Schema(title = "获取第三方登录配置", name = "获取第三方登录配置")
    @GetMapping("/login/config")
    @ResponseBody
    public ResultBody getWechatConfig() {
        Map<String, Object> map = Maps.newHashMap();
        Map<String, String> qqMap = Maps.newHashMap();
        OpenOAuth2ClientDetails qqObj = qqAuthService.getClientDetails();
        qqMap.put("appid", qqObj.getClientId());
        qqMap.put("scope", qqObj.getScope());

        map.put("qq", qqMap);
        //添加微信授权配置
        wechatAuthServiceMap.forEach((k, v) -> {
            Map<String, String> vMap = Maps.newHashMap();
            OpenOAuth2ClientDetails vObj = v.getClientDetails();
            vMap.put("appid", vObj.getClientId());
            vMap.put("scope", vObj.getScope());
            map.put(v.getConfigTag(), vMap);
        });
        return ResultBody.ok(map);
    }
}
