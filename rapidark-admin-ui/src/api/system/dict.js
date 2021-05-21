import request from '@/utils/request'

export function getDicts() {
  return request({
    url: 'base/dict/all',
    method: 'get'
  })
}

export function add(data) {
  return request({
    url: 'base/dict',
    method: 'post',
    data
  })
}

export function del(ids) {
  return request({
    url: 'base/dict/',
    method: 'delete',
    data: {
      ids: ids
    }
  })
}

export function edit(data) {
  return request({
    url: 'base/dict',
    method: 'put',
    data
  })
}

export default { add, edit, del }
