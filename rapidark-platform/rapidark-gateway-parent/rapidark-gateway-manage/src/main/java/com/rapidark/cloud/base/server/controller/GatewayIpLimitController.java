package com.rapidark.cloud.base.server.controller;

import com.rapidark.cloud.base.client.model.entity.GatewayIpLimit;
import com.rapidark.cloud.base.client.model.entity.GatewayIpLimitApi;
import com.rapidark.cloud.base.server.service.GatewayIpLimitService;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.framework.common.model.ResponseResult;
//import com.rapidark.framework.common.security.http.OpenRestTemplate;
import com.rapidark.framework.common.utils.StringUtils;



import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 网关IP访问控制
 *
 * @author: liuyadu
 * @date: 2019/3/12 15:12
 * @description:
 */
@Schema(title = "网关IP访问控制")
@RestController
public class GatewayIpLimitController {
    @Autowired
    private GatewayIpLimitService gatewayIpLimitService;
//    @Autowired
//    private OpenRestTemplate openRestTemplate;

    /**
     * 获取分页接口列表
     *
     * @return
     */
    @Schema(title = "获取分页接口列表", name = "获取分页接口列表")
    @GetMapping("/gateway/limit/ip")
    public ResponseResult<Page<GatewayIpLimit>> getIpLimitListPage(@RequestParam(required = false) Map map) {
        return ResponseResult.ok(gatewayIpLimitService.findListPage(new PageParams(map)));
    }

    /**
     * 查询策略已绑定API列表
     *
     * @param policyId
     * @return
     */
    @Schema(title = "查询策略已绑定API列表", name = "获取分页接口列表")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "policyId", value = "策略ID", paramType = "form"),
//    })
    @GetMapping("/gateway/limit/ip/api/list")
    public ResponseResult<List<GatewayIpLimitApi>> getIpLimitApiList(
            @RequestParam("policyId") Long policyId
    ) {
        return ResponseResult.ok(gatewayIpLimitService.findIpLimitApiList(policyId));
    }

    /**
     * 绑定API
     *
     * @param policyId 策略ID
     * @param apiIds   API接口ID.多个以,隔开.选填
     * @return
     */
    @Schema(title = "绑定API", name = "一个API只能绑定一个策略")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "policyId", value = "策略ID", defaultValue = "", required = true, paramType = "form"),
//            @ApiImplicitParam(name = "apiIds", value = "API接口ID.多个以,隔开.选填", defaultValue = "", required = false, paramType = "form")
//    })
    @PostMapping("/gateway/limit/ip/api/add")
    public ResponseResult addIpLimitApis(
            @RequestParam("policyId") Long policyId,
            @RequestParam(value = "apiIds", required = false) String apiIds
    ) {
        gatewayIpLimitService.addIpLimitApis(policyId, StringUtils.isNotBlank(apiIds) ? apiIds.split(",") : new String[]{});
        // openRestTemplate.refreshGateway();
        return ResponseResult.ok();
    }

    /**
     * 获取IP限制
     *
     * @param policyId
     * @return
     */
    @Schema(title = "获取IP限制", name = "获取IP限制")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "policyId", required = true, value = "策略ID", paramType = "path"),
//    })
    @GetMapping("/gateway/limit/ip/{policyId}/info")
    public ResponseResult<GatewayIpLimit> getIpLimit(@PathVariable("policyId") Long policyId) {
        return ResponseResult.ok(gatewayIpLimitService.getIpLimitPolicy(policyId));
    }

    /**
     * 添加IP限制
     *
     * @param policyName 策略名称
     * @param policyType 策略类型:0-拒绝/黑名单 1-允许/白名单
     * @param ipAddress  ip地址/IP段:多个用隔开;最多10个
     * @return
     */
    @Schema(title = "添加IP限制", name = "添加IP限制")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "policyName", required = true, value = "策略名称", paramType = "form"),
//            @ApiImplicitParam(name = "policyType", required = true, value = "策略类型:0-拒绝/黑名单 1-允许/白名单", allowableValues = "0,1", paramType = "form"),
//            @ApiImplicitParam(name = "ipAddress", required = true, value = "ip地址/IP段:多个用隔开;最多10个", paramType = "form")
//    })
    @PostMapping("/gateway/limit/ip/add")
    public ResponseResult<Long> addIpLimit(
            @RequestParam(value = "policyName") String policyName,
            @RequestParam(value = "policyType") Integer policyType,
            @RequestParam(value = "ipAddress") String ipAddress
    ) {
        GatewayIpLimit ipLimit = new GatewayIpLimit();
        ipLimit.setPolicyName(policyName);
        ipLimit.setPolicyType(policyType);
        ipLimit.setIpAddress(ipAddress);
        Long policyId = null;
        GatewayIpLimit result = gatewayIpLimitService.addIpLimitPolicy(ipLimit);
        if (result != null) {
            policyId = result.getPolicyId();
        }
        return ResponseResult.ok(policyId);
    }

    /**
     * 编辑IP限制
     *
     * @param policyId   IP限制ID
     * @param policyName 策略名称
     * @param policyType 策略类型:0-拒绝/黑名单 1-允许/白名单
     * @param ipAddress  ip地址/IP段:多个用隔开;最多10个
     * @return
     */
    @Schema(title = "编辑IP限制", name = "编辑IP限制")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "policyId", required = true, value = "接口Id", paramType = "form"),
//            @ApiImplicitParam(name = "policyName", required = true, value = "策略名称", paramType = "form"),
//            @ApiImplicitParam(name = "policyType", required = true, value = "策略类型:0-拒绝/黑名单 1-允许/白名单", allowableValues = "0,1", paramType = "form"),
//            @ApiImplicitParam(name = "ipAddress", required = true, value = "ip地址/IP段:多个用隔开;最多10个", paramType = "form")
//    })
    @PostMapping("/gateway/limit/ip/update")
    public ResponseResult updateIpLimit(
            @RequestParam("policyId") Long policyId,
            @RequestParam(value = "policyName") String policyName,
            @RequestParam(value = "policyType") Integer policyType,
            @RequestParam(value = "ipAddress") String ipAddress
    ) {
        GatewayIpLimit ipLimit = new GatewayIpLimit();
        ipLimit.setPolicyId(policyId);
        ipLimit.setPolicyName(policyName);
        ipLimit.setPolicyType(policyType);
        ipLimit.setIpAddress(ipAddress);
        gatewayIpLimitService.updateIpLimitPolicy(ipLimit);
        // openRestTemplate.refreshGateway();
        return ResponseResult.ok();
    }


    /**
     * 移除IP限制
     *
     * @param policyId
     * @return
     */
    @Schema(title = "移除IP限制", name = "移除IP限制")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "policyId", required = true, value = "policyId", paramType = "form"),
//    })
    @PostMapping("/gateway/limit/ip/remove")
    public ResponseResult removeIpLimit(
            @RequestParam("policyId") Long policyId
    ) {
        gatewayIpLimitService.removeIpLimitPolicy(policyId);
        // 刷新网关
        // openRestTemplate.refreshGateway();
        return ResponseResult.ok();
    }
}
