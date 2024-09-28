//client_api.js
import request from '@/utils/request'

/**
 * 添加客户端
 */
export const addClient = data => {
    return request({
        url: '/gatewayManage/client/add',
        method: 'post',
        data
    })
};

/**
 * 更新客户端
 */
export const updateClient = data => {
    return request({
        url: '/gatewayManage/client/update',
        method: 'post',
        data
    })
};

/**
 * 分页查询客户端列表
 */
export const clientPageList = data => {
    return request({
        url: '/gatewayManage/client/pageList',
        method: 'post',
        data
    })
};

/**
 * 启用客户端
 */
export const startClient = data => {
    return request({
        url: '/gatewayManage/client/start',
        method: 'get',
        data
    })
};

/**
 * 停止客户端
 */
export const stopClient = data => {
    return request({
        url: '/gatewayManage/client/stop',
        method: 'get',
        data
    })
};

/**
 * 删除客户端
 */
export const deleteClient = data => {
    return request({
        url: '/gatewayManage/client/delete',
        method: 'get',
        data
    })
};
