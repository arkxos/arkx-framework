import request from '@/utils/request'

class ArkRequest {

  constructor() {
    this.request = request
  }

  /**
   * 设置请求头
   * @param {Object} headers 请求头
   */
  setHeaders(headers) {
    Object.keys(headers).forEach((key) => {
      this.request.defaults.headers[key] = headers[key]
    })
  }

  /**
   * 发送 get 请求
   * @param {String} url url地址
   * @param {Object} query 查询参数
   * @return json 数据
   */
  get(url, params = {}, isMe) {
    return request({
      url: url,
      params,
      method: 'get'
    })
  }

  /**
   * 发送 post 请求
   * @param {String} url url地址
   * @param {Object} body 请求参数
   * @return json 数据
   */
  post(url, data = {}, isMe) {
    return request({
      url: url,
      data,
      headers: {
        'Content-Type': 'multipart/form-data;charset=UTF-8'
      },
      method: 'post'
    })
  }

  setBaseUrl(url) {
    this.request.defaults.baseURL = url
  }

}

export default new ArkRequest()
