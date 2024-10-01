//ip_api.js
import request from '@/utils/request'


/**
 * 添加网关路由服务
 */
export const addIp = data => {
    return request({
        url: '/base/ip/add',
        method: 'post',
        data
    })
};

/**
 * 添加网关路由服务
 */
export const updateIp = data => {
    return request({
        url: '/base/ip/update',
        method: 'post',
        data
    })
};

/**
 * 查询网关路由列表
 */
export const ipPageList = data => {
    return request({
        url: '/base/ip/pageList',
        method: 'post',
        data
    })
};

/**
 * 删除网关路由
 */
export const deleteIp = data => {
    return request({
        url: '/base/ip/delete',
        method: 'get',
        data
    })
};
