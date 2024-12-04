package com.bsd.org.server.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bsd.org.server.enums.DepartmentLevelEnum;
import com.bsd.org.server.model.entity.Department;
import com.bsd.org.server.model.vo.DepartmentVO;
import com.bsd.org.server.service.CompanyService;
import com.bsd.org.server.service.DepartmentService;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.security.OpenHelper;
import com.rapidark.framework.common.security.OpenUserDetails;



import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 部门信息 前端控制器
 *
 * @author lrx
 * @date 2019-08-14
 */
@Schema(title = "部门信息", name = "部门信息")
@RestController
@RequestMapping("department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private CompanyService companyService;

    /**
     * 分页获取部门数据
     *
     * @return
     */
    @Schema(title = "分页获取部门数据", name = "分页获取部门数据")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentId", value = "部门ID", paramType = "form"),
//            @ApiImplicitParam(name = "parentId", value = "上级部门ID", paramType = "form"),
//            @ApiImplicitParam(name = "departmentCode", value = "部门代码", paramType = "form"),
//            @ApiImplicitParam(name = "departmentName", value = "部门名称", paramType = "form"),
//            @ApiImplicitParam(name = "status", value = "状态:0-禁用 1-启用", paramType = "form"),
//            @ApiImplicitParam(name = "companyId", value = "所属企业ID", paramType = "form"),
//            @ApiImplicitParam(name = "pageIndex", value = "页码", paramType = "form"),
//            @ApiImplicitParam(name = "pageSize", value = "每页大小", paramType = "form")
//    })
    @GetMapping(value = "/page")
    public ResponseResult page(
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "departmentCode", required = false) String departmentCode,
            @RequestParam(value = "departmentName", required = false) String departmentName,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        DepartmentVO departmentVO = new DepartmentVO();
        departmentVO.setCompanyId(companyId);
        departmentVO.setDepartmentCode(departmentCode);
        departmentVO.setDepartmentId(departmentId);
        departmentVO.setDepartmentName(departmentName);
        departmentVO.setParentId(parentId);
        departmentVO.setStatus(status);
        //设置分页
        Page pageConf = new Page<DepartmentVO>(pageIndex, pageSize);
        //查询
        IPage<DepartmentVO> page = departmentService.pageByParam(pageConf, departmentVO);
        return ResponseResult.ok(page);
    }


    @Schema(title = "获取所有部门select数据", name = "获取所有部门select数据")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentId", value = "需要移除部门ID", required = false, paramType = "form"),
//            @ApiImplicitParam(name = "companyId", value = "所属企业ID", required = true, paramType = "form")
//    })
    @GetMapping(value = "/select/list")
    public ResponseResult selectDepartmentList(@RequestParam(value = "departmentId", required = false) Long departmentId,
                                               @RequestParam(value = "companyId", required = true) Long companyId) {
        DepartmentVO departmentVO = new DepartmentVO();
        departmentVO.setCompanyId(companyId);
        departmentVO.setDepartmentId(departmentId);
        List<DepartmentVO> departments = departmentService.listSelectDepartments(departmentVO);
        return ResponseResult.ok(departments);
    }


    /**
     * 查找部门信息
     */
    @Schema(title = "查找部门信息", name = "根据ID查找部门信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentId", required = true, value = "departmentId", paramType = "form")
//    })
    @GetMapping("/get")
    public ResponseResult get(@RequestParam("departmentId") Long departmentId) {
        Department department = departmentService.getById(departmentId);
        if (department == null) {
            return ResponseResult.failed("未查找到部门信息");
        }
        return ResponseResult.ok(department);
    }


    /**
     * 获取所有部门信息
     *
     * @return
     */
    @Schema(title = "获取所有部门信息", name = "获取所有部门信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "companyId", value = "所属企业ID", required = true, paramType = "form")
//    })
    @GetMapping("/list")
    public ResponseResult list(@RequestParam(value = "companyId", required = true) Long companyId) {
        DepartmentVO departmentVO = new DepartmentVO();
        departmentVO.setCompanyId(companyId);
        return ResponseResult.ok(departmentService.listByParam(departmentVO));
    }

    /**
     * 获取所有启用的部门信息
     *
     * @return
     */
    @Schema(title = "获取所有启用的部门信息", name = "获取所有启用的部门信息")
    @GetMapping("/availableList")
    public ResponseResult availableList() {
        return ResponseResult.ok(departmentService.availableList());
    }


    /**
     * 获取所有下级部门
     *
     * @param departmentId
     * @return
     */
    @Schema(title = "获取所有下级部门", name = "根据ID获取所有下级部门")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentId", required = true, value = "部门ID", paramType = "form")
//    })
    @GetMapping("/children")
    public ResponseResult children(@RequestParam("departmentId") Long departmentId) {
        return ResponseResult.ok(departmentService.getChildrenDepartments(departmentId, null));
    }

    /**
     * 获取所有启用的下级部门
     *
     * @param departmentId
     * @return
     */
    @Schema(title = "获取所有启用的下级部门", name = "根据ID获取所有启用的下级部门")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentId", required = true, value = "departmentId", paramType = "form")
//    })
    @GetMapping("/availableChildrens")
    public ResponseResult availableChildrens(@RequestParam("departmentId") Long departmentId) {
        return ResponseResult.ok(departmentService.getChildrenDepartments(departmentId, true));
    }


    /**
     * 添加部门
     *
     * @return
     */
    @Schema(title = "添加部门", name = "添加部门")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "parentId", required = false, value = "上级部门ID", example = "0", paramType = "form"),
//            @ApiImplicitParam(name = "departmentCode", required = true, value = "部门代码", example = "test", paramType = "form"),
//            @ApiImplicitParam(name = "departmentName", required = true, value = "部门名称", example = "测试", paramType = "form"),
//            @ApiImplicitParam(name = "level", required = true, value = "部门级别", example = "1", paramType = "form"),
//            @ApiImplicitParam(name = "seq", required = false, value = "显示顺序", example = "1", paramType = "form"),
//            @ApiImplicitParam(name = "status", required = false, value = "状态:0-禁用 1-启用", example = "1", paramType = "form"),
//            @ApiImplicitParam(name = "companyId", required = true, value = "所属企业ID", example = "1173825172121944065", paramType = "form")
//    })
    @PostMapping("/add")
    public ResponseResult add(
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "departmentCode") String departmentCode,
            @RequestParam(value = "departmentName") String departmentName,
            @RequestParam(value = "level") Integer level,
            @RequestParam(value = "seq", required = false) Long seq,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "companyId") Long companyId
    ) {
        //获取用户
        OpenUserDetails openUserDetails = OpenHelper.getUser();
        //创建新增数据
        Department department = new Department();
        department.setParentId(parentId);
        department.setDepartmentCode(departmentCode);
        department.setDepartmentName(departmentName);
        department.setLevel(level);
        department.setSeq(seq);
        department.setStatus(status);
        department.setCompanyId(companyId);
        department.setCreateBy(openUserDetails.getUserId()+"");
        departmentService.saveDepartment(department);
        //删除菜单缓存信息
        companyService.removeMeunCache();
        return ResponseResult.ok();
    }

    /**
     * 编辑部门
     *
     * @return
     */
    @Schema(title = "编辑部门", name = "编辑部门")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentId", required = true, value = "部门ID", example = "1162211202827141121", paramType = "form"),
//            @ApiImplicitParam(name = "parentId", required = false, value = "上级部门ID", example = "0", paramType = "form"),
//            @ApiImplicitParam(name = "departmentCode", required = true, value = "部门代码", example = "test", paramType = "form"),
//            @ApiImplicitParam(name = "departmentName", required = true, value = "部门名称", example = "测试", paramType = "form"),
//            @ApiImplicitParam(name = "level", required = true, value = "部门级别", example = "1", paramType = "form"),
//            @ApiImplicitParam(name = "seq", required = false, value = "显示顺序", example = "1", paramType = "form"),
//            @ApiImplicitParam(name = "status", required = false, value = "状态:0-禁用 1-启用", example = "1", paramType = "form"),
//            @ApiImplicitParam(name = "companyId", required = true, value = "所属企业ID", example = "1173825172121944065", paramType = "form")
//    })
    @PostMapping("/update")
    public ResponseResult update(
            @RequestParam(value = "departmentId") Long departmentId,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "departmentCode") String departmentCode,
            @RequestParam(value = "departmentName") String departmentName,
            @RequestParam(value = "level") Integer level,
            @RequestParam(value = "seq", required = false) Long seq,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "companyId") Long companyId
    ) {
        //获取当前授权用户
        OpenUserDetails openUserDetails = OpenHelper.getUser();
        //更新数据
        Department department = new Department();
        department.setDepartmentId(departmentId);
        department.setParentId(parentId);
        department.setDepartmentCode(departmentCode);
        department.setDepartmentName(departmentName);
        department.setLevel(level);
        department.setSeq(seq);
        department.setStatus(status);
        department.setCompanyId(companyId);
        department.setCreateBy(openUserDetails.getUserId()+"");
        departmentService.updateDepartment(department);
        //删除菜单缓存信息
        companyService.removeMeunCache();
        return ResponseResult.ok();
    }


    /**
     * 禁用/启用部门信息
     *
     * @return
     */
    @Schema(title = "禁用/启用部门信息", name = "禁用/启用部门信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentId", required = true, value = "departmentId", paramType = "form"),
//            @ApiImplicitParam(name = "status", required = true, value = "状态:0-禁用 1-启用", paramType = "form"),
//    })
    @PostMapping("/status")
    public ResponseResult status(@RequestParam(value = "departmentId") Long departmentId, @RequestParam(value = "status") Boolean status) {
        departmentService.changeStatus(departmentId, status);
        //删除菜单缓存信息
        companyService.removeMeunCache();
        return ResponseResult.ok();
    }


    @Schema(title = "部门级别列表", name = "部门级别列表")
    @PostMapping("/levels")
    public ResponseResult levels() {
        JSONArray levelArray = new JSONArray();
        Arrays.asList(DepartmentLevelEnum.values()).forEach(x -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", x.getLevelCode());
            jsonObject.put("name", x.getLevelName());
            levelArray.add(jsonObject);
        });
        JSONObject levels = new JSONObject();
        levels.put("levels", levelArray);
        return ResponseResult.ok(levels);
    }


    /**
     * 删除部门
     *
     * @return
     */
    /*@Schema(title = "删除部门", name = "根据部门ID删除部门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", required = true, value = "部门ID", example = "1", paramType = "form")
    })
    @PostMapping("/remove")
    public ResultBody remove(@RequestParam(value = "departmentId", required = true) Long departmentId) {
        Department department = departmentService.getById(departmentId);
        if (department == null) {
            return ResultBody.failed("部门信息不存在");
        }
        boolean isSuc = departmentService.removeById(departmentId);
        if (!isSuc) {
            return ResultBody.failed("删除部门信息失败");
        }
        return ResultBody.ok();
    }*/
}
