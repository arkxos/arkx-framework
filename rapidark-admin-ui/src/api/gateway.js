import request from '@/utils/request'

export const refreshGateway = () => {
  const data = {}
  return request({
    url: 'actuator/open/refresh',
    data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
    },
    method: 'post'
  })
}

export const getAccessLogs = ({ page, limit, bizId, bizStatus, path, ip, serviceId }) => {
  const params = { page: page, limit: limit, bizId: bizId, bizStatus: bizStatus, path: path, ip: ip, serviceId: serviceId }
  return request({
    url: 'base/gateway/access/logs',
    params,
    method: 'get'
  })
}

/**
 * 获取服务列表
 */
export const getServiceList = () => {
  return request({
    url: 'base/gateway/service/list',
    method: 'get'
  })
}
