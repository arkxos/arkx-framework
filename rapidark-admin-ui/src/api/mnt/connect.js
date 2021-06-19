import request from '@/utils/request'

export function testDbConnect(data) {
  return request({
    url: 'base/database/testConnect',
    method: 'post',
    data
  })
}

export function testServerConnect(data) {
  return request({
    url: 'base/serverDeploy/testConnect',
    method: 'post',
    data
  })
}
