package com.bsd.org.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bsd.org.server.model.entity.Dingtalk;
import com.bsd.org.server.model.vo.DingtalkVO;
import com.bsd.org.server.service.DingtalkService;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.security.OpenHelper;



import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 钉钉配置信息 前端控制器
 *
 * @author lrx
 * @date 2019-08-14
 */
@Schema(title = "钉钉配置信息", name = "钉钉配置信息")
@RestController
@RequestMapping("dingtalk")
public class DingtalkController {
    @Autowired
    private DingtalkService dingtalkService;

    /**
     * 分页获取钉钉配置信息
     *
     * @return
     */
    @Schema(title = "分页获取钉钉配置信息", name = "分页获取钉钉配置信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "companyId", value = "企业ID", paramType = "form"),
//            @ApiImplicitParam(name = "corpId", value = "企业corpid", paramType = "form"),
//            @ApiImplicitParam(name = "agentdId", value = "应用的agentdId", paramType = "form"),
//            @ApiImplicitParam(name = "appKey", value = "应用的AppKey", paramType = "form"),
//            @ApiImplicitParam(name = "appSecret", value = "应用的AppSecret", paramType = "form"),
//            @ApiImplicitParam(name = "encodingAesKey", value = "数据加密密钥", paramType = "form"),
//            @ApiImplicitParam(name = "token", value = "加解密需要用到的token", paramType = "form"),
//            @ApiImplicitParam(name = "pageIndex", value = "页码", paramType = "form"),
//            @ApiImplicitParam(name = "pageSize", value = "每页大小", paramType = "form")
//    })
    @GetMapping(value = "/page")
    public ResponseResult page(@RequestParam(value = "companyId", required = false) Long companyId,
                               @RequestParam(value = "corpId", required = false) String corpId,
                               @RequestParam(value = "agentdId", required = false) String agentdId,
                               @RequestParam(value = "appKey", required = false) String appKey,
                               @RequestParam(value = "appSecret", required = false) String appSecret,
                               @RequestParam(value = "encodingAesKey", required = false) String encodingAesKey,
                               @RequestParam(value = "token", required = false) String token,
                               @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        //查询条件
        DingtalkVO dingtalkVO = new DingtalkVO();
        dingtalkVO.setCompanyId(companyId);
        dingtalkVO.setCorpId(corpId);
        dingtalkVO.setAgentdId(agentdId);
        dingtalkVO.setAppKey(appKey);
        dingtalkVO.setAppSecret(appSecret);
        dingtalkVO.setEncodingAesKey(encodingAesKey);
        dingtalkVO.setToken(token);
        //设置分页
        Page page = new Page<Dingtalk>(pageIndex, pageSize);
        return ResponseResult.ok(dingtalkService.pageByParam(dingtalkVO, page));
    }


    /**
     * 查找钉钉配置信息
     */
    @Schema(title = "查找钉钉配置信息", name = "根据公司ID查找钉钉配置信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "companyId", required = true, value = "companyId", paramType = "form")
//    })
    @GetMapping("/get")
    public ResponseResult<Dingtalk> get(@RequestParam("companyId") Long companyId) {
        Dingtalk dingtalk = dingtalkService.getById(companyId);
        if (dingtalk == null) {
            return ResponseResult.failed("未找到钉钉配置信息");
        }
        return ResponseResult.ok(dingtalk);
    }

    /**
     * 添加钉钉配置信息
     *
     * @return
     */
    @Schema(title = "添加钉钉配置信息", name = "添加钉钉配置信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "companyId", required = true, value = "企业ID", example = "1173825172121944065", paramType = "form"),
//            @ApiImplicitParam(name = "corpId", required = true, value = "企业corpid", paramType = "form"),
//            @ApiImplicitParam(name = "agentdId", required = true, value = "应用的agentdId", paramType = "form"),
//            @ApiImplicitParam(name = "appKey", required = true, value = "应用的AppKey", paramType = "form"),
//            @ApiImplicitParam(name = "appSecret", required = true, value = "应用的AppSecret", paramType = "form"),
//            @ApiImplicitParam(name = "encodingAesKey", required = false, value = "数据加密密钥", paramType = "form"),
//            @ApiImplicitParam(name = "token", required = false, value = "加解密需要用到的token", paramType = "form")
//    })
    @PostMapping("/add")
    public ResponseResult add(
            @RequestParam(value = "companyId") Long companyId,
            @RequestParam(value = "corpId") String corpId,
            @RequestParam(value = "agentdId") String agentdId,
            @RequestParam(value = "appKey") String appKey,
            @RequestParam(value = "appSecret") String appSecret,
            @RequestParam(value = "encodingAesKey", required = false) String encodingAesKey,
            @RequestParam(value = "token", required = false) String token
    ) {
        Dingtalk dingtalk = new Dingtalk();
        dingtalk.setCompanyId(companyId);
        dingtalk.setCorpId(corpId);
        dingtalk.setAgentdId(agentdId);
        dingtalk.setAppKey(appKey);
        dingtalk.setAppSecret(appSecret);
        dingtalk.setEncodingAesKey(encodingAesKey);
        dingtalk.setToken(token);
        dingtalk.setCreateBy(OpenHelper.getUser().getUserId()+"");
        dingtalkService.saveDingtalk(dingtalk);
        return ResponseResult.ok();
    }

    /**
     * 编辑钉钉配置信息
     *
     * @return
     */
    @Schema(title = "编辑钉钉配置信息", name = "编辑钉钉配置信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "companyId", required = true, value = "企业ID", paramType = "form"),
//            @ApiImplicitParam(name = "corpId", required = true, value = "企业corpid", paramType = "form"),
//            @ApiImplicitParam(name = "agentdId", required = true, value = "应用的agentdId", paramType = "form"),
//            @ApiImplicitParam(name = "appKey", required = true, value = "应用的AppKey", paramType = "form"),
//            @ApiImplicitParam(name = "appSecret", required = true, value = "应用的AppSecret", paramType = "form"),
//            @ApiImplicitParam(name = "encodingAesKey", required = false, value = "数据加密密钥", paramType = "form"),
//            @ApiImplicitParam(name = "token", required = false, value = "加解密需要用到的token", paramType = "form")
//    })
    @PostMapping("/update")
    public ResponseResult update(
            @RequestParam(value = "companyId") Long companyId,
            @RequestParam(value = "corpId") String corpId,
            @RequestParam(value = "agentdId") String agentdId,
            @RequestParam(value = "appKey") String appKey,
            @RequestParam(value = "appSecret") String appSecret,
            @RequestParam(value = "encodingAesKey", required = false) String encodingAesKey,
            @RequestParam(value = "token", required = false) String token
    ) {
        Dingtalk dingtalk = new Dingtalk();
        dingtalk.setCompanyId(companyId);
        dingtalk.setCorpId(corpId);
        dingtalk.setAgentdId(agentdId);
        dingtalk.setAppKey(appKey);
        dingtalk.setAppSecret(appSecret);
        dingtalk.setEncodingAesKey(encodingAesKey);
        dingtalk.setToken(token);
        dingtalk.setCreateBy(OpenHelper.getUser().getUserId()+"");
        dingtalkService.updateDingtalk(dingtalk);
        return ResponseResult.ok();
    }

    /**
     * 删除钉钉配置信息
     *
     * @return
     */
    @Schema(title = "删除钉钉配置信息", name = "删除钉钉配置信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "companyId", required = true, value = "公司ID", paramType = "form")
//    })
    @PostMapping("/remove")
    public ResponseResult remove(@RequestParam(value = "companyId") Long companyId) {
        Dingtalk dingtalk = dingtalkService.getById(companyId);
        if (dingtalk == null) {
            return ResponseResult.failed("钉钉配置信息不存在");
        }

        boolean isSuc = dingtalkService.removeById(companyId);
        if (!isSuc) {
            return ResponseResult.failed("删除钉钉配置信息失败");
        }

        return ResponseResult.ok();
    }
}
