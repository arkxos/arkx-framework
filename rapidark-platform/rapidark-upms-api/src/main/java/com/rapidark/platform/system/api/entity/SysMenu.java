package com.rapidark.platform.system.api.entity;

import com.rapidark.platform.system.api.constants.UserConstants;

import com.rapidark.framework.common.utils.StringUtils;
import com.rapidark.framework.common.core.constant.Constants;
import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统资源-菜单信息
 *
 * @author: liuyadu
 * @date: 2018/10/24 16:21
 * @description:
 */
@Data
@Entity
@Table(name="sys_menu")
@Schema(description = "菜单")
public class SysMenu extends AbstractIdLongEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单Id
     */
    @Id
    @Column(name = "menu_Id")
    @Schema(title = "菜单id")
    private Long menuId;

	@NotNull(message = "菜单父ID不能为空")
	@Schema(description = "菜单父id")
	private Long parentId;

	/**
	 * 服务ID
	 */
	private String appCode;

    /**
     * 服务ID
     */
    private String serviceId;

	@Schema(description = "菜单编码")
    private String code;

	@NotBlank(message = "菜单名称不能为空")
	@Schema(description = "菜单名称")
    private String name;

	/**
	 * 菜单权限标识
	 */
	@Schema(description = "菜单权限标识")
	private String permission;

	@Schema(description = "菜单图标")
    private String icon;

	@Schema(description = "排序值")
	private Integer sortOrder;

    /**
     * 类型（M目录 C菜单 F按钮）
     */
	@NotNull(message = "菜单类型不能为空")
	@Schema(description = "菜单类型,0:菜单 1:按钮")
    private int menuType;

    /**
     * 请求协议:/,http://,https://
     */
    private String scheme;

	/**
	 * 前端路由标识路径
	 */
	@Schema(description = "前端路由标识路径")
    private String path;

    // 组件路径
    private String component = "";

    /**
     * 打开方式:_self窗口内,_blank新窗口
     */
    /**
     * 集成模式：0：正常；1：iframe，2：link，3：micro
     */
    private int integrateMode;

    /**
     * 优先级 越小越靠前
     */
    private Integer priority;

    /**
     * 描述
     */
    private String menuDesc;

	@Schema(description = "菜单是否显示")
    private Integer visible = 1;

    /**
     * 路由参数
     */
    private String queryParam;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
	@Schema(description = "路由缓冲")
    private int keepAlive;

    /**
     * 子菜单
     */
//    @TableField(exist = false)
    @Transient
    private List<SysMenu> children = new ArrayList<>();

    /**
     * 获取路由名称
     */
    public String getRouteName() {
        String routerName = StringUtils.capitalize(path);
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame()) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * 获取路由地址
     */
    public String getRouterPath() {
        String routerPath = this.path;
        // 内链打开外网方式
        if (getParentId() != 0L && isInnerLink()) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0L == getParentId() && UserConstants.TYPE_DIR.equals(getMenuType())
                && UserConstants.INTEGRATE_MODE_NORMAL == getIntegrateMode()) {
            routerPath = "/" + this.path;
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame()) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     */
    public String getComponentInfo() {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(this.component) && !isMenuFrame()) {
            component = this.component;
        } else if (StringUtils.isEmpty(this.component) && getParentId() != 0L && isInnerLink()) {
            component = UserConstants.INNER_LINK;
        } else if (StringUtils.isEmpty(this.component) && isParentView()) {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     */
    public boolean isMenuFrame() {
        return getParentId() == 0L && UserConstants.TYPE_MENU.equals(menuType) && integrateMode == UserConstants.INTEGRATE_MODE_NORMAL;
    }

    /**
     * 是否为内链组件
     */
    public boolean isInnerLink() {
        return integrateMode == UserConstants.INTEGRATE_MODE_NORMAL && StringUtils.ishttp(path);
    }

    /**
     * 是否为parent_view组件
     */
    public boolean isParentView() {
        return getParentId() != 0L && UserConstants.TYPE_DIR.equals(menuType);
    }

    /**
     * 内链域名特殊字符替换
     */
    public static String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(path, new String[]{Constants.HTTP, Constants.HTTPS, Constants.WWW, ".", ":"},
                new String[]{"", "", "", "/", "/"});
    }

    @Override
    public Long getId() {
        return menuId;
    }

    @Override
    public void setId(Long id) {
        this.menuId = id;
    }

	public List<SysMenu> getChildren() {
		if(children == null) {
			children = new ArrayList<>();
		}
		return children;
	}

}
