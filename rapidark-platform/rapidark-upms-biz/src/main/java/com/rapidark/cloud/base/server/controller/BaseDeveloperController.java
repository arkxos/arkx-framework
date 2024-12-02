package com.rapidark.cloud.base.server.controller;

import com.rapidark.cloud.base.client.model.UserAccount;
import com.rapidark.cloud.base.client.model.entity.BaseDeveloper;
import com.rapidark.cloud.base.client.service.IBaseDeveloperServiceClient;

import com.rapidark.cloud.gateway.manage.service.command.ChangeDeveloperPasswordCommand;
import com.rapidark.cloud.base.server.service.BaseDeveloperService;
import com.rapidark.cloud.gateway.manage.service.command.CreateDeveloperCommand;
import com.rapidark.cloud.gateway.manage.service.command.UpdateDeveloperCommand;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.framework.common.model.ResultBody;
import com.rapidark.framework.common.utils.PageResult;
import com.rapidark.framework.common.utils.SystemIdGenerator;
import com.rapidark.framework.common.utils.WebUtils;
import com.rapidark.framework.data.jpa.entity.Status;



import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 系统用户-开发商信息
 * @author darkness
 * @date 2022/6/27 17:35
 * @version 1.0
 */
@Schema(title = "系统用户-开发者管理")
@RestController
public class BaseDeveloperController implements IBaseDeveloperServiceClient {

    @Autowired
    private BaseDeveloperService baseDeveloperService;

    @Autowired
    private SystemIdGenerator systemIdGenerator;

    @Schema(title = "获取账号登录信息", name = "仅限系统内部调用")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "username", required = true, value = "登录名", paramType = "path"),
//    })
    @PostMapping("/developer/login")
    @Override
    public ResultBody<UserAccount> developerLogin(@RequestParam(value = "username") String username) {
        Map<String, String> parameterMap = WebUtils.getParameterMap(WebUtils.getHttpServletRequest());
        HttpServletRequest request = WebUtils.getHttpServletRequest();
        String ip = WebUtils.getRemoteAddress(request);
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        UserAccount account = baseDeveloperService.login(username, parameterMap, ip, userAgent);
        return ResultBody.ok(account);
    }

    @Schema(title = "系统分页用户列表", name = "系统分页用户列表")
    @GetMapping("/developer")
    public ResultBody<PageResult<BaseDeveloper>> getUserList(@RequestParam(required = false) Map map) {
		PageResult<BaseDeveloper> data = baseDeveloperService.findListPage(new PageParams(map));
        return ResultBody.ok(data);
    }

    @Schema(title = "获取所有用户列表", name = "获取所有用户列表")
    @GetMapping("/developer/all")
    public ResultBody<List<BaseDeveloper>> getUserAllList() {
        return ResultBody.ok(baseDeveloperService.findAllList());
    }

    @Schema(title = "添加系统用户", name = "添加系统用户")
    @PostMapping("/developer/add")
    public ResultBody<Long> addUser(@Valid @RequestBody CreateDeveloperCommand command) {
        BaseDeveloper developer = new BaseDeveloper();
        developer.setId(systemIdGenerator.generate());//UuidUtil.base58Uuid());
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
        developer.setStatus(Status.codeOf(command.getStatus()));
        baseDeveloperService.addUser(developer);
        return ResultBody.ok();
    }

    @Schema(title = "更新系统用户", name = "更新系统用户")
    @PostMapping("/developer/update")
    public ResultBody updateUser(@Valid @RequestBody UpdateDeveloperCommand command) {
        baseDeveloperService.updateUser(command);
        return ResultBody.ok();
    }

    @Schema(title = "修改用户密码", name = "修改用户密码")
    @PostMapping("/developer/update/password")
    public ResultBody updatePassword(@Valid @RequestBody ChangeDeveloperPasswordCommand command) {
        baseDeveloperService.updatePassword(command);
        return ResultBody.ok().msg("修改密码成功");
    }

    @Schema(title = "注册第三方系统登录账号", name = "仅限系统内部调用")
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
