//apidoc_api.js
import request from '@/utils/request'

/**
 * 查询网关路由列表
 */
export const apiDocList = data => {
    return request({
        url: '/gatewayManage/apiDoc/list',
        method: 'get',
        data
    })
};

/**
 * 保存API接口内容
 */
export const saveApiDoc = data => {
    return request({
        url: '/gatewayManage/apiDoc/save',
        method: 'post',
        data
    })
};

/**
 * 查询API接口内容
 */
export const findByApiDoc = data => {
    return request({
        url: '/gatewayManage/apiDoc/findById',
        method: 'get',
        data
    })
};
