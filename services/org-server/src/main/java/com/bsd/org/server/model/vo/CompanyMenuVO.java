package com.bsd.org.server.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: linrongxin
 * @Date: 2019/9/25 15:15
 */
@Data
public class CompanyMenuVO {
    /**
     * 公司ID
     */
    @Schema( value = "公司ID")
    private Long companyId;

    /**
     * 菜单title
     */
    @Schema( value = "菜单title")
    private String title;

    /**
     * 公司名称
     */
    @Schema( value = "公司名称")
    private String companyName;
    /**
     * 公司英文名称
     */
    @Schema( value = "公司英文名称")
    private String companyNameEn;
    /**
     * 公司下的部门
     */
    @Schema( value = "公司下的部门")
    private List<DepartmentMenuVO> children;

    public String getTitle() {
        return this.companyName;
    }
}
