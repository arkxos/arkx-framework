import request from '@/utils/request'

export function get(dictName) {
  const params = {
    dictName,
    page: 0,
    size: 9999
  }
  return request({
    url: 'base/dictDetail',
    method: 'get',
    params
  })
}

export function getDictMap(dictName) {
  const params = {
    dictName,
    page: 0,
    size: 9999
  }
  return request({
    url: 'base/dictDetail/map',
    method: 'get',
    params
  })
}

export function add(data) {
  return request({
    url: 'base/dictDetail',
    method: 'post',
    data
  })
}

export function del(id) {
  return request({
    url: 'base/dictDetail/' + id,
    method: 'delete'
  })
}

export function edit(data) {
  return request({
    url: 'base/dictDetail',
    method: 'put',
    data
  })
}

export default { add, edit, del }
