import request from '@/utils/request'
import config from '@/config'

/**
 * 获取登录用户菜单权限
 */
export function getRouterList() {
  const params = {
    serviceId: config.serviceId
  }
  return request({
    url: 'base/current/user/menu', // '/router/getList',
    method: 'get',
    params,
  })
}
