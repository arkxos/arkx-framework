//monitor_api.js
import request from '@/utils/request'


/**
 * 查询监控列表
 */
 export const monitorList = data => {
    return request({
        url: '/gatewayManage/monitor/list',
        method: 'post',
        data
    })
};

/**
 * 关闭本次告警
 */
 export const closeMonitor = data => {
    return request({
        url: '/gatewayManage/monitor/close',
        method: 'get',
        data
    })
};
