//regserver_api.js
import request from '@/utils/request'


/**
 * 添加客户端关联的注册服务
 */
export const addRegServer = data => {
    return request({
        url: '/gatewayManage/regServer/add',
        method: 'post',
        data
    })
};

/**
 * 修改客户端关联的注册服务
 */
export const updateRegServer = data => {
    return request({
        url: '/gatewayManage/regServer/update',
        method: 'post',
        data
    })
};

/**
 * 查询客户端关联的注册服务分页列表
 */
export const regServerPageList = data => {
    return request({
        url: '/gatewayManage/regServer/serverPageList',
        method: 'post',
        data
    })
};

/**
 * 查询客户端关联的注册服务分页列表
 */
export const regClientPageList = data => {
    return request({
        url: '/gatewayManage/regServer/clientPageList',
        method: 'post',
        data
    })
};

/**
 * 启用客户端关联的注册服务访问状态
 */
export const startRegServer = data => {
    return request({
        url: '/gatewayManage/regServer/start',
        method: 'get',
        data
    })
};

/**
 * 停止客户端关联的注册服务访问状态
 */
export const stopRegServer = data => {
    return request({
        url: '/gatewayManage/regServer/stop',
        method: 'get',
        data
    })
};

/**
 * 全部启用客户端关联的注册服务访问状态
 */
export const startAllRegServer = data => {
    return request({
        url: '/gatewayManage/regServer/startClientAllRoute',
        method: 'get',
        data
    })
};

/**
 * 全部停止客户端关联的注册服务访问状态
 */
export const stopAllRegServer = data => {
    return request({
        url: '/gatewayManage/regServer/stopClientAllRoute',
        method: 'get',
        data
    })
};

/**
 * 取消客户端关联的注册服务
 */
export const deleteRegServer = data => {
    return request({
        url: '/gatewayManage/regServer/delete',
        method: 'get',
        data
    })
};

/**
 * 获取未注册网关路由列表
 */
export const notRegServerPageList = data => {
    return request({
        url: '/gatewayManage/regServer/notRegServerPageList',
        method: 'post',
        data
    })
};

// ==========================服务端管理=============================

/**
 * 添加客户端关联的注册服务
 */
export const addRegClient = data => {
    return request({
        url: '/gatewayManage/regServer/add',
        method: 'post',
        data
    })
};

/**
 * 全部启用客户端关联的注册服务访问状态
 */
export const startAllRegClient = data => {
    return request({
        url: '/gatewayManage/regServer/startRouteAllClient',
        method: 'get',
        data
    })
};

/**
 * 全部停止客户端关联的注册服务访问状态
 */
export const stopAllRegClient = data => {
    return request({
        url: '/gatewayManage/regServer/stopRouteAllClient',
        method: 'get',
        data
    })
};

/**
 * 启用客户端关联的注册服务访问状态
 */
export const startRegClient = data => {
    return request({
        url: '/gatewayManage/regServer/start',
        method: 'get',
        data
    })
};

/**
 * 停止客户端关联的注册服务访问状态
 */
export const stopRegClient = data => {
    return request({
        url: '/gatewayManage/regServer/stop',
        method: 'get',
        data
    })
};

/**
 * 取消客户端关联的注册服务
 */
export const deleteRegClient = data => {
    return request({
        url: '/gatewayManage/regServer/delete',
        method: 'get',
        data
    })
};

/**
 * 获取当前网关路由未注册客户端列表
 */
export const notRegClientPageList = data => {
    return request({
        url: '/gatewayManage/regServer/notRegClientPageList',
        method: 'post',
        data
    })
};

/**
 * 获取当前网关路由未注册客户端列表
 */
export const regClientList = data => {
    return request({
        url: '/gatewayManage/regServer/regClientList',
        method: 'post',
        data
    })
};

/**
 * 创建当前网关路由注册客户端的TOKEN令牌
 */
 export const createRegClientToken = data => {
    return request({
        url: '/gatewayManage/regServer/createToken',
        method: 'post',
        data
    })
};

/**
 * 移除当前网关路由注册客户端的TOKEN令牌
 */
 export const removeRegClientToken = data => {
    return request({
        url: '/gatewayManage/regServer/removeToken',
        method: 'post',
        data
    })
};
