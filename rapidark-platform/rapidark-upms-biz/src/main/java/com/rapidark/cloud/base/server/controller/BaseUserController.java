package com.rapidark.cloud.base.server.controller;

import com.rapidark.cloud.base.client.model.UserAccount;
import com.rapidark.cloud.base.client.model.entity.BaseRole;
import com.rapidark.cloud.base.client.model.entity.BaseUser;
import com.rapidark.cloud.base.client.service.IBaseUserServiceClient;
import com.rapidark.cloud.base.client.service.command.AddUserCommand;
import com.rapidark.cloud.base.server.controller.cmd.AddUserRolesCommand;
import com.rapidark.cloud.base.server.service.BaseRoleService;
import com.rapidark.cloud.base.server.service.BaseUserService;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.utils.StringUtils;
import com.rapidark.framework.common.utils.WebUtils;
import com.rapidark.framework.data.jpa.entity.Status;



import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 系统用户信息
 *
 * @author liuyadu
 */
@Schema(title = "系统用户管理")
@RestController
public class BaseUserController implements IBaseUserServiceClient {

    @Autowired
    private BaseUserService baseUserService;
    @Autowired
    private BaseRoleService baseRoleService;


    /**
     * 获取登录账号信息
     *
     * @param username 登录名
     * @return
     */
    @Schema(title = "获取账号登录信息", name = "仅限系统内部调用")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "username", required = true, value = "登录名", paramType = "path"),
//    })
    @PostMapping("/user/login")
    @Override
    public ResponseResult<UserAccount> userLogin(@RequestParam(value = "username") String username) {
        Map<String, String> parameterMap = WebUtils.getParameterMap(WebUtils.getHttpServletRequest());

        HttpServletRequest request = WebUtils.getHttpServletRequest();
        String ip = WebUtils.getRemoteAddress(request);
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        UserAccount account = baseUserService.login(username, parameterMap , ip, userAgent);
        return ResponseResult.ok(account);
    }

    /**
     * 系统分页用户列表
     *
     * @return
     */
    @Schema(title = "系统分页用户列表", name = "系统分页用户列表")
    @GetMapping("/user")
    public ResponseResult<Page<BaseUser>> getUserList(@RequestParam(required = false) Map map) {
        return ResponseResult.ok(baseUserService.findListPage(new PageParams(map)));
    }

    /**
     * 获取所有用户列表
     *
     * @return
     */
    @Schema(title = "获取所有用户列表", name = "获取所有用户列表")
    @GetMapping("/user/all")
    public ResponseResult<List<BaseUser>> getUserAllList() {
        return ResponseResult.ok(baseUserService.findAllList());
    }

    /**
     * 添加系统用户
     *
     * @return
     */
    @Override
    @Schema(title = "添加系统用户", name = "添加系统用户")
    @PostMapping("/user/add")
    public ResponseResult<Long> addUser(@Valid @RequestBody AddUserCommand command) {
        BaseUser user = new BaseUser();
        user.setUserName(command.getUserName());
        user.setPassword(command.getPassword());
        user.setNickName(command.getNickName());
        user.setUserType(command.getUserType());
        user.setEmail(command.getEmail());
        user.setMobile(command.getMobile());
        user.setUserDesc(command.getUserDesc());
        user.setAvatar(command.getAvatar());
        user.setStatus(Status.codeOf(command.getStatus()));
        baseUserService.addUser(user);
        return ResponseResult.ok(user.getUserId());
    }

    /**
     * 更新系统用户
     *
     * @param userId
     * @param nickName
     * @param status
     * @param userType
     * @param email
     * @param mobile
     * @param userDesc
     * @param avatar
     * @return
     */
    @Schema(title = "更新系统用户", name = "更新系统用户")
    @PostMapping("/user/update")
    public ResponseResult updateUser(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "nickName") String nickName,
            @RequestParam(value = "status") Integer status,
            @RequestParam(value = "userType") String userType,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "userDesc", required = false) String userDesc,
            @RequestParam(value = "avatar", required = false) String avatar
    ) {
        BaseUser user = new BaseUser();
        user.setUserId(userId);
        user.setNickName(nickName);
        user.setUserType(userType);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setUserDesc(userDesc);
        user.setAvatar(avatar);
        user.setStatus(Status.codeOf(status));
        baseUserService.updateUser(user);
        return ResponseResult.ok();
    }


    /**
     * 修改用户密码
     *
     * @param userId
     * @param password
     * @return
     */
    @Schema(title = "修改用户密码", name = "修改用户密码")
    @PostMapping("/user/update/password")
    public ResponseResult updatePassword(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "password") String password
    ) {
        baseUserService.updatePassword(userId, password);
        return  ResponseResult.ok().msg("修改密码成功");
    }

    /**
     * 用户分配角色
     *
     * @return
     */
    @Schema(title = "用户分配角色", name = "用户分配角色")
    @PostMapping("/user/roles/add")
    public ResponseResult addUserRoles(@Valid @RequestBody AddUserRolesCommand command) {
        baseRoleService.saveUserRoles(command.getUserId(), StringUtils.isNotBlank(command.getRoleIds()) ? command.getRoleIds().split(",") : new String[]{});
        return ResponseResult.ok();
    }

    /**
     * 获取用户角色
     *
     * @param userId
     * @return
     */
    @Override
    @Schema(title = "获取用户已分配角色", name = "获取用户已分配角色")
    @GetMapping("/user/roles")
    public ResponseResult<List<BaseRole>> getUserRoles(
            @RequestParam(value = "userId") Long userId
    ) {
        return ResponseResult.ok(baseRoleService.getUserRoles(userId));
    }


    /**
     * 注册第三方系统登录账号
     *
     * @param account
     * @param password
     * @param accountType
     * @return
     */
    @Schema(title = "注册第三方系统登录账号", name = "仅限系统内部调用")
    @PostMapping("/user/add/thirdParty")
    @Override
    public ResponseResult addUserThirdParty(
            @RequestParam(value = "account") String account,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "accountType") String accountType,
            @RequestParam(value = "nickName") String nickName,
            @RequestParam(value = "avatar") String avatar
    ) {
        BaseUser user = new BaseUser();
        user.setNickName(nickName);
        user.setUserName(account);
        user.setPassword(password);
        user.setAvatar(avatar);
        baseUserService.addUserThirdParty(user, accountType);
        return ResponseResult.ok();
    }
}
