//balanced_api.js
import request from '@/utils/request'

/**
 * 添加负载配置
 */
export const addBalanced = data => {
    return request({
        url: '/gatewayManage/balanced/add',
        method: 'post',
        data
    })
};

/**
 * 删除负载配置
 */
export const deleteBalanced = data => {
    return request({
        url: '/gatewayManage/balanced/delete',
        method: 'get',
        data
    })
};

/**
 * 更新负载配置
 */
export const updateBalanced = data => {
    return request({
        url: '/gatewayManage/balanced/update',
        method: 'post',
        data
    })
};

/**
 * 查找负载配置
 */
export const findByBalanced = data => {
    return request({
        url: '/gatewayManage/balanced/findById',
        method: 'get',
        data
    })
};


/**
 * 分页展示负载配置
 */
export const balancedPageList = data => {
    return request({
        url: '/gatewayManage/balanced/pageList',
        method: 'post',
        data
    })
};

/**
 * 启用负载服务
 */
export const startBalanced = data => {
    return request({
        url: '/gatewayManage/balanced/start',
        method: 'get',
        data
    })
};

/**
 * 禁用负载服务
 */
export const stopBalanced = data => {
    return request({
        url: '/gatewayManage/balanced/stop',
        method: 'get',
        data
    })
};

/**
 * 已注册负载服务
 */
export const loadServerRegList = data => {
    return request({
        url: '/gatewayManage/loadServer/regList',
        method: 'post',
        data
    })
};

/**
 * 未注册负载服务
 */
export const loadServerNotRegList = data => {
    return request({
        url: '/gatewayManage/loadServer/notRegPageList',
        method: 'post',
        data
    })
};
