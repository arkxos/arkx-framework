package com.rapidark.cloud.base.server.controller;

import com.rapidark.cloud.base.client.model.UserAccount;
import com.rapidark.cloud.base.client.model.entity.BaseDeveloper;
import com.rapidark.cloud.base.client.model.entity.BaseRole;
import com.rapidark.cloud.base.client.service.IBaseDeveloperServiceClient;

import com.rapidark.cloud.gateway.manage.service.command.ChangeDeveloperPasswordCommand;
import com.rapidark.cloud.gateway.manage.service.BaseDeveloperService;
import com.rapidark.cloud.gateway.manage.service.command.CreateDeveloperCommand;
import com.rapidark.cloud.gateway.manage.service.command.UpdateDeveloperCommand;
import com.rapidark.common.model.PageParams;
import com.rapidark.common.model.ResultBody;
import com.rapidark.common.utils.PageData;
import com.rapidark.common.utils.UuidUtil;
import com.rapidark.common.utils.WebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 系统用户-开发商信息
 * @author darkness
 * @date 2022/6/27 17:35
 * @version 1.0
 */
@Api(tags = "系统用户-开发者管理")
@RestController
public class BaseDeveloperController implements IBaseDeveloperServiceClient {

    @Autowired
    private BaseDeveloperService baseDeveloperService;

    @ApiOperation(value = "获取账号登录信息", notes = "仅限系统内部调用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", required = true, value = "登录名", paramType = "path"),
    })
    @PostMapping("/developer/login")
    @Override
    public ResultBody<UserAccount> developerLogin(@RequestParam(value = "username") String username) {
        Map<String, String> parameterMap = WebUtils.getParameterMap(WebUtils.getHttpServletRequest());
        HttpServletRequest request = WebUtils.getHttpServletRequest();
        String ip = WebUtils.getRemoteAddress(request);
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        UserAccount account = baseDeveloperService.login(username, parameterMap, ip, userAgent);
        return ResultBody.ok().data(account);
    }

    @ApiOperation(value = "系统分页用户列表", notes = "系统分页用户列表")
    @GetMapping("/developer")
    public ResultBody<PageData<BaseDeveloper>> getUserList(@RequestParam(required = false) Map map) {
        PageData<BaseDeveloper> data = baseDeveloperService.findListPage(new PageParams(map));
        return ResultBody.ok().data(data);
    }

    @ApiOperation(value = "获取所有用户列表", notes = "获取所有用户列表")
    @GetMapping("/developer/all")
    public ResultBody<List<BaseRole>> getUserAllList() {
        return ResultBody.ok().data(baseDeveloperService.findAllList());
    }

    @ApiOperation(value = "添加系统用户", notes = "添加系统用户")
    @PostMapping("/developer/add")
    public ResultBody<Long> addUser(@Valid @RequestBody CreateDeveloperCommand command) {
        BaseDeveloper developer = new BaseDeveloper();
        developer.setId(UuidUtil.base58Uuid());
        developer.setCompanyName(command.getCompanyName());
        developer.setPersonName(command.getPersonName());
        developer.setType(command.getType());
        developer.setUserName(command.getUserName());
        developer.setPassword(command.getPassword());
        developer.setNickName(command.getNickName());
        developer.setEmail(command.getEmail());
        developer.setMobile(command.getMobile());
        developer.setUserDesc(command.getUserDesc());
        developer.setAvatar(command.getAvatar());
        developer.setStatus(command.getStatus());
        baseDeveloperService.addUser(developer);
        return ResultBody.ok();
    }

    @ApiOperation(value = "更新系统用户", notes = "更新系统用户")
    @PostMapping("/developer/update")
    public ResultBody updateUser(@Valid @RequestBody UpdateDeveloperCommand command) {
        baseDeveloperService.updateUser(command);
        return ResultBody.ok();
    }

    @ApiOperation(value = "修改用户密码", notes = "修改用户密码")
    @PostMapping("/developer/update/password")
    public ResultBody updatePassword(@Valid @RequestBody ChangeDeveloperPasswordCommand command) {
        baseDeveloperService.updatePassword(command);
        return ResultBody.ok().msg("修改密码成功");
    }

    @ApiOperation(value = "注册第三方系统登录账号", notes = "仅限系统内部调用")
    @PostMapping("/developer/add/thirdParty")
    @Override
    public ResultBody addDeveloperThirdParty(
            @RequestParam(value = "account") String account,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "accountType") String accountType,
            @RequestParam(value = "nickName") String nickName,
            @RequestParam(value = "avatar") String avatar
    ) {
        BaseDeveloper developer = new BaseDeveloper();
        developer.setNickName(nickName);
        developer.setUserName(account);
        developer.setPassword(password);
        developer.setAvatar(avatar);
        baseDeveloperService.addUserThirdParty(developer, accountType);
        return ResultBody.ok();
    }
}
