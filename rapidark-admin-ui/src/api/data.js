import request from '@/utils/request'
import qs from 'qs'

/**
 * 保存错误日志
 * @param info
 */
export const saveErrorLogger = info => {
  return request({
    url: 'save_error_logger',
    data: info,
    method: 'post'
  })
}

export function initData(url, params) {
  return request({
    url: url + '?' + qs.stringify(params, { indices: false }),
    method: 'get'
  })
}

export function download(url, params) {
  return request({
    url: url + '?' + qs.stringify(params, { indices: false }),
    method: 'get',
    responseType: 'blob'
  })
}
