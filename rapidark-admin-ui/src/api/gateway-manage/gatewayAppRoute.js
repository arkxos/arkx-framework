//sys_api.js
import request from '@/utils/request'


/**
 * 添加字典组
 */
export const getGateWayInfo = data => {
    return request({
        url: '',
        method: 'post',
		headers: {'Content-type': 'multipart/form-data'},
        data
    })
};


/**
 * 添加网关路由服务
 */
export const addRoute = data => {
    return request({
        url: '/base/gatewayAppRoute/add',
        method: 'post',
        data
    })
};

/**
 * 添加网关路由服务
 */
export const updateRoute = data => {
    return request({
        url: '/base/gatewayAppRoute/update',
        method: 'post',
        data
    })
};

/**
 * 查询网关路由分页列表
 */
export const routePageList = data => {
    return request({
        url: '/base/gatewayAppRoute/pageList',
        method: 'post',
        data
    })
};

/**
 * 启用网关路由
 */
export const startRoute = data => {
    return request({
        url: '/base/gatewayAppRoute/start',
        method: 'get',
        data
    })
};

/**
 * 停止网关路由
 */
export const stopRoute = data => {
    return request({
        url: '/base/gatewayAppRoute/stop',
        method: 'get',
        data
    })
};

/**
 * 删除网关路由
 */
export const deleteRoute = data => {
    return request({
        url: '/base/gatewayAppRoute/delete',
        method: 'get',
        data
    })
};
