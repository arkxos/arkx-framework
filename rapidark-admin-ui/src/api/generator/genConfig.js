import request from '@/utils/request'

export function get(tableName) {
  return request({
    url: 'code/generator/jpa/genConfig/' + tableName,
    method: 'get'
  })
}

export function update(data) {
  return request({
    url: 'code/generator/jpa/genConfig',
    data,
    method: 'put'
  })
}
