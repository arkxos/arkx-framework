package com.bsd.org.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bsd.org.server.model.entity.Position;
import com.bsd.org.server.model.vo.PositionVO;
import com.bsd.org.server.service.PositionService;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.security.OpenHelper;
import com.rapidark.framework.common.security.OpenUserDetails;



import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 职位体系 前端控制器
 *
 * @author lrx
 * @date 2019-08-14
 */
@Schema(title = "职位体系", name = "职位体系")
@RestController
@RequestMapping("position")
public class PositionController {
    @Autowired
    private PositionService positionService;

    /**
     * 分页获取职位数据
     *
     * @return
     */
    @Schema(title = "分页获取职位数据", name = "分页获取职位数据")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "positionId", value = "职位ID", paramType = "form"),
//            @ApiImplicitParam(name = "positionCode", value = "职位代码", paramType = "form"),
//            @ApiImplicitParam(name = "positionName", value = "职位名称", paramType = "form"),
//            @ApiImplicitParam(name = "status", value = "状态:0-禁用 1-启用", paramType = "form"),
//            @ApiImplicitParam(name = "departmentId", value = "所属部门ID", paramType = "form"),
//            @ApiImplicitParam(name = "companyId", value = "公司ID", paramType = "form"),
//            @ApiImplicitParam(name = "pageIndex", value = "页码", paramType = "form"),
//            @ApiImplicitParam(name = "pageSize", value = "每页大小", paramType = "form")
//    })
    @GetMapping(value = "/page")
    public ResponseResult list(
            @RequestParam(value = "positionId", required = false) Long positionId,
            @RequestParam(value = "positionCode", required = false) String positionCode,
            @RequestParam(value = "positionName", required = false) String positionName,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        //设置查询条件
        PositionVO positionVO = new PositionVO();
        positionVO.setPositionId(positionId);
        positionVO.setPositionCode(positionCode);
        positionVO.setPositionName(positionName);
        positionVO.setStatus(status);
        positionVO.setDepartmentId(departmentId);
        positionVO.setCompanyId(companyId);
        //设置分页
        Page<PositionVO> page = new Page<PositionVO>(pageIndex, pageSize);
        return ResponseResult.ok(positionService.page(page, positionVO));
    }

    /**
     * 查找职位
     */
    @Schema(title = "查找职位", name = "根据ID查找职位")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "positionId", value = "职位ID", paramType = "form"),
//    })
    @GetMapping("/get")
    public ResponseResult<Position> get(@RequestParam("positionId") Long positionId) {
        Position position = positionService.getById(positionId);
        if (position == null) {
            return ResponseResult.failed("未找到职位信息");
        }
        return ResponseResult.ok(position);
    }


    /**
     * 获取所有职位
     *
     * @return
     */
    @Schema(title = "获取所有职位", name = "获取所有职位")
    @GetMapping("/list")
    public ResponseResult list() {
        return ResponseResult.ok(positionService.listByParam(null));
    }


    /**
     * 根据部门ID获取所有职位
     *
     * @param departmentId
     * @return
     */
    @Schema(title = "根据部门ID获取所有职位", name = "根据部门ID获取所有职位")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentId", required = true, value = "部门ID", paramType = "form")
//    })
    @GetMapping("/findByDepartmentId/all")
    public ResponseResult allPositions(@RequestParam("departmentId") Long departmentId) {
        return ResponseResult.ok(positionService.findByDepartmentIdAndStatus(departmentId, null));
    }

    @Schema(title = "根据部门ID列表获取所有职位", name = "根据部门ID列表获取所有职位")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentIds", required = true, value = "部门ID列表,多个用,号隔开", paramType = "form")
//    })
    @GetMapping("/findByDepartmentIds/all")
    public ResponseResult allPositions(@RequestParam(value = "departmentIds", required = true) String departmentIds) {
        return ResponseResult.ok(positionService.findByDepartmentIds(Arrays.asList(departmentIds.split(","))));
    }


    /**
     * 根据部门ID获取启用状态职位
     *
     * @param departmentId
     * @return
     */
    @Schema(title = "根据部门ID获取启用状态职位", name = "根据部门ID获取启用状态职位")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentId", required = true, value = "部门ID", paramType = "form")
//    })
    @GetMapping("/findByDepartmentId/available")
    public ResponseResult availablePositions(@RequestParam("departmentId") Long departmentId) {
        return ResponseResult.ok(positionService.findByDepartmentIdAndStatus(departmentId, true));
    }

    /**
     * 添加职位
     *
     * @return
     */
    @Schema(title = "添加职位", name = "添加职位")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "positionCode", required = true, value = "职位代码", example = "test", paramType = "form"),
//            @ApiImplicitParam(name = "positionName", required = true, value = "职位名称", example = "测试", paramType = "form"),
//            @ApiImplicitParam(name = "workContent", required = false, value = "工作内容", example = "测试", paramType = "form"),
//            @ApiImplicitParam(name = "workStandard", required = false, value = "工作标准", example = "测试BUG", paramType = "form"),
//            @ApiImplicitParam(name = "responsibilityWeight", required = false, value = "责任权重", example = "责任权重", paramType = "form"),
//            @ApiImplicitParam(name = "requiredQualifications", required = false, value = "所需资格条件", example = "所需资格条件", paramType = "form"),
//            @ApiImplicitParam(name = "status", required = false, value = "状态:0-禁用 1-启用", example = "1", paramType = "form"),
//            @ApiImplicitParam(name = "seq", required = false, value = "显示顺序", example = "1", paramType = "form"),
//            @ApiImplicitParam(name = "departmentId", required = true, value = "所属部门ID", example = "1162211202827141121", paramType = "form")
//    })
    @PostMapping("/add")
    public ResponseResult add(
            @RequestParam(value = "positionCode") String positionCode,
            @RequestParam(value = "positionName") String positionName,
            @RequestParam(value = "workContent", required = false) String workContent,
            @RequestParam(value = "workStandard", required = false) String workStandard,
            @RequestParam(value = "responsibilityWeight", required = false) String responsibilityWeight,
            @RequestParam(value = "requiredQualifications", required = false) String requiredQualifications,
            @RequestParam(value = "status", required = false, defaultValue = "1") Boolean status,
            @RequestParam(value = "seq", required = false) Integer seq,
            @RequestParam(value = "departmentId") Long departmentId
    ) {
        OpenUserDetails openUserDetails = OpenHelper.getUser();
        Position position = new Position();
        position.setPositionCode(positionCode);
        position.setPositionName(positionName);
        position.setWorkContent(workContent);
        position.setWorkStandard(workStandard);
        position.setResponsibilityWeight(responsibilityWeight);
        position.setRequiredQualifications(requiredQualifications);
        position.setStatus(status);
        position.setSeq(seq);
        position.setDepartmentId(departmentId);
        position.setCreateBy(openUserDetails.getUserId()+"");
        positionService.savePosition(position);
        return ResponseResult.ok();
    }

    /**
     * 编辑职位
     *
     * @return
     */
    @Schema(title = "编辑职位", name = "编辑职位")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "positionId", required = true, value = "职位ID", example = "1162303811142623234", paramType = "form"),
//            @ApiImplicitParam(name = "positionCode", required = true, value = "职位代码", example = "test", paramType = "form"),
//            @ApiImplicitParam(name = "positionName", required = true, value = "职位名称", example = "测试", paramType = "form"),
//            @ApiImplicitParam(name = "workContent", required = false, value = "工作内容", example = "测试", paramType = "form"),
//            @ApiImplicitParam(name = "workStandard", required = false, value = "工作标准", example = "测试BUG", paramType = "form"),
//            @ApiImplicitParam(name = "responsibilityWeight", required = false, value = "责任权重", example = "责任权重", paramType = "form"),
//            @ApiImplicitParam(name = "requiredQualifications", required = false, value = "所需资格条件", example = "所需资格条件", paramType = "form"),
//            @ApiImplicitParam(name = "status", required = false, value = "状态:0-禁用 1-启用", example = "1", paramType = "form"),
//            @ApiImplicitParam(name = "seq", required = false, value = "显示顺序", example = "1", paramType = "form"),
//            @ApiImplicitParam(name = "departmentId", required = true, value = "所属部门ID", example = "1162211202827141121", paramType = "form")
//    })
    @PostMapping("/update")
    public ResponseResult update(
            @RequestParam(value = "positionId") Long positionId,
            @RequestParam(value = "positionCode") String positionCode,
            @RequestParam(value = "positionName") String positionName,
            @RequestParam(value = "workContent", required = false) String workContent,
            @RequestParam(value = "workStandard", required = false) String workStandard,
            @RequestParam(value = "responsibilityWeight", required = false) String responsibilityWeight,
            @RequestParam(value = "requiredQualifications", required = false) String requiredQualifications,
            @RequestParam(value = "status", required = false, defaultValue = "1") Boolean status,
            @RequestParam(value = "seq", required = false) Integer seq,
            @RequestParam(value = "departmentId") Long departmentId
    ) {
        OpenUserDetails openUserDetails = OpenHelper.getUser();
        Position position = new Position();
        position.setPositionId(positionId);
        position.setPositionCode(positionCode);
        position.setPositionName(positionName);
        position.setWorkContent(workContent);
        position.setWorkStandard(workStandard);
        position.setResponsibilityWeight(responsibilityWeight);
        position.setRequiredQualifications(requiredQualifications);
        position.setStatus(status);
        position.setSeq(seq);
        position.setDepartmentId(departmentId);
        position.setCreateBy(openUserDetails.getUserId()+"");
        positionService.updatePosition(position);
        return ResponseResult.ok();
    }

    /**
     * 禁用/启用职位信息
     *
     * @return
     */
    @Schema(title = "禁用/启用职位信息", name = "禁用/启用职位信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "positionId", required = true, value = "职位ID", paramType = "form"),
//            @ApiImplicitParam(name = "status", required = true, value = "状态:0-禁用 1-启用", paramType = "form")
//    })
    @PostMapping("/status")
    public ResponseResult status(@RequestParam(value = "positionId") Long positionId, @RequestParam(value = "status") Boolean status) {
        positionService.status(positionId, status);
        return ResponseResult.ok();
    }


    /**
     * 删除岗位
     *
     * @return
     */
    /*@Schema(title = "删除岗位", name = "根据岗位ID删除岗位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "positionId", required = true, value = "岗位ID", example = "1", paramType = "form")
    })
    @PostMapping("/remove")
    public ResultBody remove(@RequestParam(value = "positionId", required = true) Long positionId) {
        Position position = positionService.getById(positionId);
        if (position == null) {
            return ResultBody.failed("岗位信息不存在");
        }
        boolean isSuc = positionService.removeById(position.getPositionId());
        if (!isSuc) {
            return ResultBody.failed("删除岗位失败");
        }
        return ResultBody.ok();
    }*/
}
