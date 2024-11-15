package com.bsd.org.server.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: linrongxin
 * @Date: 2019/9/25 15:16
 */
@Data
public class DepartmentMenuVO {
    /**
     * 部门ID
     */
    @Schema( value = "部门ID")
    private Long departmentId;
    /**
     * 部门编码
     */
    @Schema( value = "部门编码")
    private String departmentCode;
    /**
     * 菜单title
     */
    @Schema( value = "菜单title")
    private String title;
    /**
     * 部门名称
     */
    @Schema( value = "部门名称")
    private String departmentName;
    /**
     * 下级部门
     */
    @Schema( value = "下级部门")
    private List<DepartmentMenuVO> children;

    public String getTitle() {
        return this.departmentName;
    }
}
