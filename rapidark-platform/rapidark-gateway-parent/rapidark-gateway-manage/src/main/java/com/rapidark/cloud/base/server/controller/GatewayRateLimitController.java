package com.rapidark.cloud.base.server.controller;

import com.rapidark.cloud.base.server.service.GatewayRateLimitService;
import com.rapidark.framework.common.model.ResponseResult;
//import com.rapidark.framework.common.security.http.OpenRestTemplate;
import com.rapidark.framework.common.utils.PageResult;
import com.rapidark.framework.common.utils.StringUtils;


import com.rapidark.framework.commons.data.model.PageParams;
import com.rapidark.platform.system.api.entity.GatewayRateLimit;
import com.rapidark.platform.system.api.entity.GatewayRateLimitApi;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 网关流量控制
 *
 * @author: liuyadu
 * @date: 2019/3/12 15:12
 * @description:
 */
@Schema(title = "网关流量控制")
@RestController
public class GatewayRateLimitController {
    @Autowired
    private GatewayRateLimitService gatewayRateLimitService;
//    @Autowired
//    private OpenRestTemplate openRestTemplate;

    /**
     * 获取分页接口列表
     *
     * @return
     */
    @Schema(title = "获取分页接口列表", name = "获取分页接口列表")
    @GetMapping("/gateway/limit/rate")
    public ResponseResult<PageResult<GatewayRateLimit>> getRateLimitListPage(@RequestParam(required = false) Map map) {
        return ResponseResult.ok(gatewayRateLimitService.findListPage(new PageParams(map)));
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
    @GetMapping("/gateway/limit/rate/api/list")
    public ResponseResult<List<GatewayRateLimitApi>> getRateLimitApiList(
            @RequestParam("policyId") Long policyId
    ) {
        return ResponseResult.ok(gatewayRateLimitService.findRateLimitApiList(policyId));
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
    @PostMapping("/gateway/limit/rate/api/add")
    public ResponseResult addRateLimitApis(
            @RequestParam("policyId") Long policyId,
            @RequestParam(value = "apiIds", required = false) String apiIds
    ) {
        gatewayRateLimitService.addRateLimitApis(policyId, StringUtils.isNotBlank(apiIds) ? apiIds.split(",") : new String[]{});
        // openRestTemplate.refreshGateway();
        return ResponseResult.ok();
    }

    /**
     * 获取流量控制
     *
     * @param policyId
     * @return
     */
    @Schema(title = "获取流量控制", name = "获取流量控制")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "policyId", required = true, value = "策略ID", paramType = "path"),
//    })
    @GetMapping("/gateway/limit/rate/{policyId}/info")
    public ResponseResult<GatewayRateLimit> getRateLimit(@PathVariable("policyId") Long policyId) {
        return ResponseResult.ok(gatewayRateLimitService.getRateLimitPolicy(policyId));
    }

    /**
     * 添加流量控制
     *
     * @param policyName   策略名称
     * @param limitQuota   限制数
     * @param intervalUnit 单位时间
     * @param policyType   限流规则类型
     * @return
     */
    @Schema(title = "添加流量控制", name = "添加流量控制")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "policyName", required = true, value = "策略名称", paramType = "form"),
//            @ApiImplicitParam(name = "policyType", required = true, value = "限流规则类型:url,origin,user", allowableValues = "url,origin,user", paramType = "form"),
//            @ApiImplicitParam(name = "limitQuota", required = true, value = "限制数", paramType = "form"),
//            @ApiImplicitParam(name = "intervalUnit", required = true, value = "单位时间:seconds-秒,minutes-分钟,hours-小时,days-天", allowableValues = "seconds,minutes,hours,days", paramType = "form"),
//    })
    @PostMapping("/gateway/limit/rate/add")
    public ResponseResult<Long> addRateLimit(
            @RequestParam(value = "policyName") String policyName,
            @RequestParam(value = "policyType") String policyType,
            @RequestParam(value = "limitQuota") Long limitQuota,
            @RequestParam(value = "intervalUnit") String intervalUnit

    ) {
        GatewayRateLimit rateLimit = new GatewayRateLimit();
        rateLimit.setPolicyName(policyName);
        rateLimit.setLimitQuota(limitQuota);
        rateLimit.setIntervalUnit(intervalUnit);
        rateLimit.setPolicyType(policyType);
        Long policyId = null;
        GatewayRateLimit result = gatewayRateLimitService.addRateLimitPolicy(rateLimit);
        if (result != null) {
            policyId = result.getPolicyId();
        }
        return ResponseResult.ok(policyId);
    }

    /**
     * 编辑流量控制
     *
     * @param policyId     流量控制ID
     * @param policyName   策略名称
     * @param limitQuota   限制数
     * @param intervalUnit 单位时间
     * @param policyType   限流规则类型
     * @return
     */
    @Schema(title = "编辑流量控制", name = "编辑流量控制")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "policyId", required = true, value = "接口Id", paramType = "form"),
//            @ApiImplicitParam(name = "policyName", required = true, value = "策略名称", paramType = "form"),
//            @ApiImplicitParam(name = "policyType", required = true, value = "限流规则类型:url,origin,user", allowableValues = "url,origin,user", paramType = "form"),
//            @ApiImplicitParam(name = "limitQuota", required = true, value = "限制数", paramType = "form"),
//            @ApiImplicitParam(name = "intervalUnit", required = true, value = "单位时间:seconds-秒,minutes-分钟,hours-小时,days-天", allowableValues = "seconds,minutes,hours,days", paramType = "form"),
//    })
    @PostMapping("/gateway/limit/rate/update")
    public ResponseResult updateRateLimit(
            @RequestParam("policyId") Long policyId,
            @RequestParam(value = "policyName") String policyName,
            @RequestParam(value = "policyType") String policyType,
            @RequestParam(value = "limitQuota") Long limitQuota,
            @RequestParam(value = "intervalUnit") String intervalUnit
    ) {
        GatewayRateLimit rateLimit = new GatewayRateLimit();
        rateLimit.setPolicyId(policyId);
        rateLimit.setPolicyName(policyName);
        rateLimit.setLimitQuota(limitQuota);
        rateLimit.setIntervalUnit(intervalUnit);
        rateLimit.setPolicyType(policyType);
        gatewayRateLimitService.updateRateLimitPolicy(rateLimit);
        // openRestTemplate.refreshGateway();
        return ResponseResult.ok();
    }


    /**
     * 移除流量控制
     *
     * @param policyId
     * @return
     */
    @Schema(title = "移除流量控制", name = "移除流量控制")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "policyId", required = true, value = "policyId", paramType = "form"),
//    })
    @PostMapping("/gateway/limit/rate/remove")
    public ResponseResult removeRateLimit(
            @RequestParam("policyId") Long policyId
    ) {
        gatewayRateLimitService.removeRateLimitPolicy(policyId);
        // 刷新网关
        // openRestTemplate.refreshGateway();
        return ResponseResult.ok();
    }
}
