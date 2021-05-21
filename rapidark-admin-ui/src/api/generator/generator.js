import request from '@/utils/request'

export function getAllTable() {
  return request({
    url: 'code/generator/jpa/tables/all',
    method: 'get'
  })
}

export function generator(tableName, type) {
  return request({
    url: 'code/generator/jpa/' + tableName + '/' + type,
    method: 'post',
    responseType: type === 2 ? 'blob' : ''
  })
}

export function save(data) {
  return request({
    url: 'code/generator/jpa',
    data,
    method: 'put'
  })
}

export function sync(tables) {
  return request({
    url: 'code/generator/sync',
    method: 'post',
    data: tables
  })
}

