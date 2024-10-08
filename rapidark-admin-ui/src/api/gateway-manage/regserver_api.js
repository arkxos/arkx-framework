//regserver_api.js
import request from '@/utils/request'


/**
 * 添加客户端关联的注册服务
 */
export const addRegServer = data => {
    return request({
        url: '/base/regServer/add',
        method: 'post',
        data
    })
};

/**
 * 修改客户端关联的注册服务
 */
export const updateRegServer = data => {
    return request({
        url: '/base/regServer/update',
        method: 'post',
        data
    })
};

/**
 * 查询客户端关联的注册服务分页列表
 */
export const regServerPageList = data => {
    return request({
        url: '/base/regServer/serverPageList',
        method: 'get',
        params: data
    })
};

/**
 * 查询客户端关联的注册服务分页列表
 */
export const regClientPageList = data => {
    return request({
        url: '/base/regServer/clientPageList',
        method: 'get',
        params: data
    })
};

/**
 * 启用客户端关联的注册服务访问状态
 */
export const startRegServer = data => {
    return request({
        url: '/base/regServer/start',
        method: 'post',
        params: data
    })
};

/**
 * 停止客户端关联的注册服务访问状态
 */
export const stopRegServer = data => {
    return request({
        url: '/base/regServer/stop',
        method: 'post',
        params: data
    })
};

/**
 * 全部启用客户端关联的注册服务访问状态
 */
export const startAllRegServer = data => {
    return request({
        url: '/base/regServer/startClientAllRoute',
        method: 'post',
        params: data
    })
};

/**
 * 全部停止客户端关联的注册服务访问状态
 */
export const stopAllRegServer = data => {
    return request({
        url: '/base/regServer/stopClientAllRoute',
        method: 'post',
        params: data
    })
};

/**
 * 取消客户端关联的注册服务
 */
export const deleteRegServer = data => {
    return request({
        url: '/base/regServer/delete',
        method: 'post',
        params: data
    })
};

/**
 * 获取未注册网关路由列表
 */
export const notRegServerPageList = data => {
    return request({
        url: '/base/regServer/notRegServerPageList',
        method: 'get',
        params: data
    })
};

// ==========================服务端管理=============================

/**
 * 添加客户端关联的注册服务
 */
export const addRegClient = data => {
    return request({
        url: '/base/regServer/add',
        method: 'post',
        data
    })
};

/**
 * 全部启用客户端关联的注册服务访问状态
 */
export const startAllRegClient = data => {
    return request({
        url: '/base/regServer/startRouteAllClient',
        method: 'post',
        params: data
    })
};

/**
 * 全部停止客户端关联的注册服务访问状态
 */
export const stopAllRegClient = data => {
    return request({
        url: '/base/regServer/stopRouteAllClient',
        method: 'post',
        params: data
    })
};

/**
 * 启用客户端关联的注册服务访问状态
 */
export const startRegClient = data => {
    return request({
        url: '/base/regServer/start',
        method: 'post',
        params: data
    })
};

/**
 * 停止客户端关联的注册服务访问状态
 */
export const stopRegClient = data => {
    return request({
        url: '/base/regServer/stop',
        method: 'post',
        params: data
    })
};

/**
 * 取消客户端关联的注册服务
 */
export const deleteRegClient = data => {
    return request({
        url: '/base/regServer/delete',
        method: 'post',
        params: data
    })
};

/**
 * 获取当前网关路由未注册客户端列表
 */
export const notRegClientPageList = data => {
    return request({
        url: '/base/regServer/notRegClientPageList',
        method: 'get',
        params: data
    })
};

/**
 * 获取当前网关路由未注册客户端列表
 */
export const regClientList = data => {
    return request({
        url: '/base/regServer/regClientList',
        method: 'get',
        params: data
    })
};

/**
 * 创建当前网关路由注册客户端的TOKEN令牌
 */
 export const createRegClientToken = data => {
    return request({
        url: '/base/regServer/createToken',
        method: 'post',
        data
    })
};

/**
 * 移除当前网关路由注册客户端的TOKEN令牌
 */
 export const removeRegClientToken = data => {
    return request({
        url: '/base/regServer/removeToken',
        method: 'post',
        data
    })
};
