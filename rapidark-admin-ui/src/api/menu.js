import request from '@/utils/request'

/**
 * 获取菜单列表
 */
export const getMenus = () => {
  return request({
    url: 'base/menu/all',
    method: 'get'
  })
}

/**
 * 添加菜单
 * @param menuCode
 * @param menuName
 * @param icon
 * @param scheme
 * @param path
 * @param target
 * @param status
 * @param parentId
 * @param priority
 * @param menuDesc
 * @param serviceId
 */
export const addMenu = ({ menuCode, menuName, icon, scheme, path, component, target, status, visible, parentId, priority, menuDesc, serviceId }) => {
  const data = {
    menuCode: menuCode,
    menuName: menuName,
    icon: icon,
    scheme: scheme,
    path: path,
    component: component,
    target: target,
    status: status,
    visible: visible,
    parentId: parentId,
    priority: priority,
    menuDesc: menuDesc,
    serviceId: serviceId
  }
  return request({
    url: 'base/menu/add',
    data,
    method: 'post'
  })
}

/**
 * 更新菜单
 * @param menuId
 * @param menuCode
 * @param menuName
 * @param icon
 * @param scheme
 * @param path
 * @param target
 * @param status
 * @param parentId
 * @param priority
 * @param menuDesc
 * @param serviceId
 */
export const updateMenu = ({ menuId, menuCode, menuName, icon, scheme, path, component, target, status, visible, parentId, priority, menuDesc, serviceId }) => {
  const data = {
    menuId: menuId,
    menuCode: menuCode,
    menuName: menuName,
    icon: icon,
    scheme: scheme,
    path: path,
    component: component,
    target: target,
    status: status,
    visible: visible,
    parentId: parentId,
    priority: priority,
    menuDesc: menuDesc,
    serviceId: serviceId
  }
  return request({
    url: 'base/menu/update',
    data: data,
    method: 'post'
  })
}
/**
 * 删除菜单
 * @param menuId
 */
export const removeMenu = (menuId) => {
  const data = {
    menuId: menuId
  }
  return request({
    url: 'base/menu/remove',
    data,
    method: 'post'
  })
}

export const getAllServicesInMenu = () => {
  return request({
    url: 'base/menu/services',
    method: 'get'
  })
}
