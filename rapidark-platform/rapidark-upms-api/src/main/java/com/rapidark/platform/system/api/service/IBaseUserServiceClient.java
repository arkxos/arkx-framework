package com.rapidark.platform.system.api.service;

import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.platform.system.api.command.AddUserCommand;
import com.rapidark.platform.system.api.entity.SysRole;
import com.rapidark.platform.system.api.entity.UserAccount;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author liuyadu
 */
public interface IBaseUserServiceClient {
    /**
     * 系统用户登录
     *
     * @param username
     * @return
     */
    @PostMapping("/user/login")
    ResponseResult<UserAccount> userLogin(@RequestParam(value = "username") String username);


    /**
     * 注册第三方系统登录账号
     *
     * @param account
     * @param password
     * @param accountType
     * @return
     */
    @PostMapping("/user/register/thirdParty")
    ResponseResult addUserThirdParty(
            @RequestParam(value = "account") String account,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "accountType") String accountType,
            @RequestParam(value = "nickName") String nickName,
            @RequestParam(value = "avatar") String avatar
    );


    /**
     * 获取用户角色
     *
     * @param userId
     * @return
     */
    @GetMapping("/user/roles")
    ResponseResult<List<SysRole>> getUserRoles(@RequestParam(value = "userId") Long userId);

    /**
     * 添加系统用户
     *
     * @return
     */
    @PostMapping("/user/add")
    ResponseResult addUser(@Valid @RequestBody AddUserCommand command);
}
