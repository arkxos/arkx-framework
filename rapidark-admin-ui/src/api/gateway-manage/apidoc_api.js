//apidoc_api.js
import request from '@/utils/request'

/**
 * 查询网关路由列表
 */
export const apiDocList = data => {
    return request({
        url: '/base/apiDoc/list',
        method: 'get',
        params: data
    })
};

/**
 * 保存API接口内容
 */
export const saveApiDoc = data => {
    return request({
        url: '/base/apiDoc/save',
        method: 'post',
        data
    })
};

/**
 * 查询API接口内容
 */
export const findByApiDoc = data => {
    return request({
        url: '/base/apiDoc/findById',
        method: 'get',
        params: data
    })
};
