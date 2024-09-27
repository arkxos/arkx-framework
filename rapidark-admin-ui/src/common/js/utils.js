import Viewer from 'viewerjs'
import 'viewerjs/dist/viewer.css'

import { Message, MessageBox } from 'element-ui'
import { BigNumber } from 'bignumber.js'

import './uuid'
import api from '@/api/handling/index'
import axios from 'axios'
import Vue from 'vue'

let baseURL = ''

// 确认弹框
export const tpawsConfirm = (message, callback, options = {}, params) => {
  const currentMessage = message ? message : '请确定?'
  options.type = options.type ? options.type : 'warning'
  options.title = options.title ? options.title : '提示'
  MessageBox.confirm(currentMessage, options)
    .then(() => {
      callback && callback(params)
    })
    .catch((err) => {
      console.log(err)
    })
}

// url特殊字符编码
export const encodeURIComponentByObj = (obj) => {
  Object.keys(obj).forEach((key) => {
    obj[key] = encodeURIComponent(obj[key])
  })
  return obj
}

// 根据参数下载文件
export const exportInfoByUrlParam = (urlStr = '', param = {}) => {
  let tempArr = []
  Object.keys(param).forEach((key) => {
    tempArr.push(key + '=' + param[key])
  })
  const url = `${baseURL}` + urlStr + `?` + tempArr.join('&')
  window.open(url)
}

// 函数节流
export const throttle = (
  obj = {
    timer: 0
  },
  cb,
  date = 200
) => {
  if (!obj.timer) {
    obj.timer = 0
  }
  clearTimeout(obj.timer)
  obj.timer = setTimeout(() => {
    cb()
  }, date)
}

// 打印信息
export const log = (message, ...optionalParams) => {
  if (process.env.NODE_ENV !== 'production') {
    console.log(message, optionalParams)
  }
}

// 打印信息
export const msgSuccess = (message = '') => {
  Message.success(message)
}

// 打印信息
export const msgInfo = (message = '') => {
  Message.info(message)
}

// 打印信息
export const msgError = (message = '系统错误，请联系管理员') => {
  if (message.message) {
    message = message.message
  }
  Message.error(message)
}

// 数字校验
export const numPattern = (val) => {
  return /^-?\d*\.?\d+$/.test(val)
}

// 字母或数字
export const numOrAlphabet = (val) => {
  return /^[0-9a-zA-Z]+$/.test(val)
}

// 手机号校验
export const mPattern = (val) => {
  return /^1[123456789]\d{9}$/.test(val)
}

// 中文校验
export const cnPattern = (val) => {
  return /[\u4E00-\u9FA5]/.test(val)
}
// 中文校验和英文
export const cnAndEnPattern = (val) => {
  return /^[a-zA-Z\u4e00-\u9fa5]+$/.test(val)
}
// 数字校验
export const numberPattern = (val) => {
  return /^\d+$/g.test(val)
}

// // 金额校验
// export const moneyPattern = val => {
//   return /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/.test(
//     val
//   )
// }

// 正整数校验
export const posPattern = (val) => {
  return /^([1-9]\d{0,35}|0)?$/.test(val)
}

// 金额校验
export const moneyPattern = (val) => {
  return /^([1-9]\d{0,35}|0)(\.\d{1,2})?$/.test(val)
}

// 金额校验
export const moneyPatternSp = (val) => {
  return /^\d+(\.\d{1,4})?$/.test(val)
}

// 校验 最多4位小数
export const maxFourDecimals = (val) => {
  return /^([1-9]\d{0,35}|0)(\.\d{1,4})?$/.test(val)
}

// 校验 最多3位小数
export const maxThreeDecimals = (val) => {
  return /^([1-9]\d{0,35}|0)(\.\d{1,3})?$/.test(val)
}

// 校验 最多99位小数
export const maxDecimals = (val) => {
  return /^([1-9]\d{0,35}|0)(\.\d{1,99})?$/.test(val)
}

// 校验 最多3位小数 可负数
/* eslint-disable */
export const maxThreeDecimalsCanMinus = (val) => {
  return /^(\-)?([1-9]\d{0,35}|0)(\.\d{1,3})?$/.test(val)
}

// 校验 最多2位小数
export const maxTwoDecimals = (val) => {
  return /^([1-9]\d{0,35}|0)(\.\d{1,2})?$/.test(val)
}

// 校验 最多1位小数
export const maxOneDecimals = (val) => {
  return /^([1-9]\d{0,35}|0)(\.\d{1,1})?$/.test(val)
}

// 校验 最多2位小数 可负数
/* eslint-disable */
export const maxTwoDecimalsCanMinus = (val) => {
  return /^(\-)?([1-9]\d{0,35}|0)(\.\d{1,2})?$/.test(val)
}

// 计算周岁
export const jsGetAge = (strBirthday) => {
  if (!strBirthday) {
    return ''
  }
  let returnAge
  let strBirthdayArr = strBirthday.split('-')
  let birthYear = strBirthdayArr[0]
  let birthMonth = strBirthdayArr[1]
  let birthDay = strBirthdayArr[2]

  let d = new Date()
  let nowYear = d.getFullYear()
  let nowMonth = d.getMonth() + 1
  let nowDay = d.getDate()

  if (nowYear === birthYear) {
    returnAge = 0 // 同年 则为0岁
  } else {
    let ageDiff = nowYear - birthYear // 年之差
    if (ageDiff > 0) {
      if (nowMonth === birthMonth) {
        let dayDiff = nowDay - birthDay // 日之差
        if (dayDiff < 0) {
          returnAge = ageDiff - 1
        } else {
          returnAge = ageDiff
        }
      } else {
        let monthDiff = nowMonth - birthMonth // 月之差
        if (monthDiff < 0) {
          returnAge = ageDiff - 1
        } else {
          returnAge = ageDiff
        }
      }
    } else {
      returnAge = -1 // 返回-1 表示出生日期输入错误 晚于今天
    }
  }

  return String(returnAge) // 返回周岁年龄
}

// 日期格式化
export const dateFormat = (date = '', fmt = 'yyyy-MM-dd') => {
  if (date !== null) {
    // author: meizz
    if (typeof date === 'string') {
      return date
    }
    const o = {
      'M+': date.getMonth() + 1, // 月份
      'd+': date.getDate(), // 日
      'h+': date.getHours(), // 小时
      'm+': date.getMinutes(), // 分
      's+': date.getSeconds(), // 秒
      'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
      S: date.getMilliseconds() // 毫秒
    }
    if (/(y+)/.test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
    }
    for (var k in o) {
      if (new RegExp('(' + k + ')').test(fmt)) {
        fmt = fmt.replace(RegExp.$1, RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length))
      }
    }
    return fmt
  }
  return ''
}

// 格式化金额
export const fmoney = (s = 0, n = 0) => {
  // 判断是否是NaN
  if (isNaN(s)) {
    return s
  }

  // 判断不为空
  if (!s && typeof s !== 'number') {
    return ''
  }
  // 不存在小数位 不保留小数
  if (String(s).indexOf('.') === -1) {
    n = 0
  }
  s = parseFloat(s).toFixed(n) + ''
  let l = s
    .split('.')[0]
    .split('')
    .reverse()
  const r = s.split('.')[1]
  let t = ''
  for (let i = 0; i < l.length; i++) {
    t += l[i] + ((i + 1) % 3 === 0 && i + 1 !== l.length ? ',' : '')
  }
  const result = t
    .split('')
    .reverse()
    .join('')

  return r ? result + '.' + r : result
}

// 下载文件 兼容ie
export const downloadFile = (file, type) => {
  // let saveLink = document.createElement('a')
  // saveLink.href = url
  // saveLink.download = fileName
  // saveLink.click()
  if (type === 'APP') {
    window.open(`${baseURL}/upload/shareFileDownload?idFile=${file.idFile}&fileName=${file.name}&imgSource=APP`)
  } else if (type === 'TYSY') {
    window.open(`${baseURL}/upload/shareFileDownload?idFile=${file.idFile}&fileName=${file.name}&imgSource=TYSY`)
  } else {
    window.open(`${baseURL}/upload/shareFileDownload?idFile=${file.idFile}&fileName=${file.name}`)
  }
}

// 理赔模板下载
export const downloadClaimTemplate = (file) => {
  window.open(`${baseURL}/upload/claimTemplateDownload?idFile=${file.idFile}&fileName=${file.name}`)
}

// 影像文件下载
export const downloadImageFile = (file) => {
  window.open(`${baseURL}/upload/imageExcelDownload?idFileIndex=${file.idFileIndex}&fileName=${file.fileName}`)
}

// 获取字符长度
export const getStrLength = (str = '') => {
  return str.replace(/[\u0391-\uFFE5]/g, 'aa').length
}

// 生成随机数
export const getRandomString = (length = 16) => {
  const $chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnoprstuvwxyz0123456789'
  let maxPos = $chars.length
  let retStr = ''
  for (let i = 0; i < length; i++) {
    retStr += $chars.charAt(Math.floor(Math.random() * maxPos))
  }
  return retStr
}

// 去除特殊符号
export const replaceSpecialSymbols = (param = {}, symbols = ['%']) => {
  const temp = {
    ...param
  }
  Object.keys(temp).forEach((key) => {
    if (temp[key]) {
      if (typeof temp[key] === 'string') {
        symbols.forEach((symbol) => {
          temp[key] = temp[key].replace(new RegExp(symbol, 'g'), '#')
        })
      } else if (typeof temp[key] === 'object' && !Array.isArray(temp[key])) {
        temp[key] = replaceSpecialSymbols(temp[key])
      }
    }
  })
  return temp
}

// 路由跳转 携带参数
export const routerPush = (
  $router = null,
  path = '',
  query = {},
  $route = {
    query: {}
  }
) => {
  if (!$router) {
    return
  }

  $router.push({
    path: path,
    query: {
      ...$route.query,
      ...query
    }
  })
}

// 数字相加
export const plusByBigNumber = (x, ...args) => {
  x = new BigNumber(x)
  args.forEach((arr) => {
    x = x.plus(arr)
  })
  return x.toNumber()
}

// 数字相减
export const subByBigNumber = (x, ...args) => {
  x = new BigNumber(x)
  args.forEach((arr) => {
    x = x.minus(arr)
  })
  return x.toNumber()
}

// 数字相除
export const divByBigNumber = (x, ...args) => {
  x = new BigNumber(x)
  args.forEach((arr) => {
    x = x.div(arr)
  })
  return x.toNumber()
}

// 数字相乘
export const mulByBigNumber = (x, ...args) => {
  x = new BigNumber(x)
  args.forEach((arr) => {
    x = x.times(arr)
  })
  return x.toNumber()
}

export const isFunction = (func) => {
  return func && typeof func === 'function'
}

// 根据 matrix 获取 rotate
export const getRotate = (a, b, c, d, e, f) => {
  let aa = Math.round((180 * Math.asin(a)) / Math.PI)
  let bb = Math.round((180 * Math.acos(b)) / Math.PI)
  let cc = Math.round((180 * Math.asin(c)) / Math.PI)
  let dd = Math.round((180 * Math.acos(d)) / Math.PI)
  let deg = 0
  if (aa === bb || -aa === bb) {
    deg = dd
  } else if (-aa + bb === 180) {
    deg = 180 + cc
  } else if (aa + bb === 180) {
    deg = 360 - cc || 360 - dd
  }
  return deg >= 360 ? 0 : deg
}

// 通过 transform 来获取rotate
export const getRotateByTransformStyle = (element) => {
  if (typeof element === 'undefined') return

  if (!(element instanceof HTMLElement)) return

  const transform = window.getComputedStyle(element)['transform']
  const matrix = transform.slice(7, -1).split(',')
  return getRotate(...matrix)
}

export const isNumber = (num) => typeof num === 'number'

// 校验身份证号
export const volidateIdNo = (data) => {
  let pattern = /(^\d{15}$)|(^\d{17}([0-9]|X)$)/
  if (pattern.test(data)) {
    let year = ''
    let month = ''
    let day = ''
    let len = data.length
    if (len == 15) {
      year = 19 + data.substring(6, 8)
      month = data.substring(8, 10)
      day = data.substring(10, 12)
    }
    if (len == 18) {
      year = data.substring(6, 10)
      month = data.substring(10, 12)
      day = data.substring(12, 14)
    }
    let birthday = new Date(year + '/' + month + '/' + day)
    if (birthday.getFullYear() == year && birthday.getMonth() + 1 == month && birthday.getDate() == day) {
      return true
    } else {
      return false
    }
  } else {
    return false
  }
}

// 获取图片地址 影像系统
export const getImgSrc = (file) => {
  // 图片如果有源地址 直接返回源地址
  if (file.originalUrl) {
    return file.originalUrl
  }

  // 理赔附件如果有源地址 直接返回源地址
  if (file.documentUrl) {
    // debugger
    return file.documentUrl
  }

  let baseURL
  if (process.env.NODE_ENV !== 'production') {
    baseURL = 'http://10.25.84.233:8080'
  } else {
    baseURL = `${location.origin}`
  }
  return `${baseURL}/mhis-tpaws/upload/imageFileDownload?idFileIndex=${file.idFileIndex}&fileName=${file.fileName}`
}

// 获取图片地址 IOBS
export const getIOBSImgSrc = (file) => {
  let baseURL
  if (process.env.NODE_ENV !== 'production') {
    baseURL = 'http://10.25.84.233:8080'
  } else {
    baseURL = `${location.origin}`
  }
  return `${baseURL}/mhis-tpaws/upload/shareFileDownload?idFile=${file.idFileIndex ||
    file.idFile}&fileName=${file.fileName || file.name}&type=preview`
}
// 获取图片地址 fastDFS
export const getFastDFSImgSrc = (file) => {
  const urlMap = {
    test: 'http://paybjfb-sx-stg.paic.com.cn/paybjfbjsnj', // 开发测试域名
    prod: 'http://180.101.236.245:8082/group1' // 生产地址域名
  }
  const baseURL = Vue.prototype.$FdfsUrl || urlMap[process.env.CLI_ENV] || 'http://180.101.236.245:8082/group1'
  return `${baseURL}/${file.idFileIndex || file.idFile || file}?idFile=${file.idFileIndex ||
    file.idFile ||
    file}&fileName=${file.fileName || file.name || file}&type=preview`
}

// 打开portal菜单
export const openPortalUrl = (message) => {
  message['path'] = message['path'] || ''
  const parentWin = window.parent

  if (process.env.NODE_ENV !== 'production') {
    window.open('#' + message['path'], '_blank')
  } else {
    //message['path'] = `${location.origin}/mhis-tpaws/viewPage${message['path']}`
    message['path'] = `${location.origin}/mhis-tpaws/index.html#${message['path']}` // 处理服务调经办系统跳转问题
    message['type'] = 'openNewView'
    parentWin.postMessage(message, parentWin.location.origin)
  }
}

export const validFileType = (file) => {
  const allowFileList = [
    'xls',
    'doc',
    'jpg',
    'htm',
    'bmp',
    'tif',
    'ppt',
    'pdf',
    'gif',
    'mht',
    'mpp',
    'msg',
    'rtf',
    'rar',
    'zip',
    'txt',
    'log',
    'html',
    'docx',
    'xlsx',
    'pptx',
    'jpeg',
    'png',
    'tiff'
  ]
  if (allowFileList.indexOf(file.name.slice(file.name.lastIndexOf('.') + 1).toLowerCase()) < 0) {
    msgInfo(
      '只能上传xls,doc,jpg,htm,bmp,tif,ppt,pdf,gif,mht,mpp,msg,rtf,rar,zip,txt,log,html,docx,xlsx,pptx,jpeg,png,tiff 格式的文件'
    )
    return false
  }
  return true
}

// fileType img pdf
export const filePreview = (src = '', fileType = '') => {
  const div = document.createElement('div')
  div.classList.add('image-preview')

  // 显示图片
  let previewObj = null
  if (src.indexOf('.pdf') !== -1 || fileType === 'pdf') {
    // 创建关闭按钮
    const i = document.createElement('i')
    i.classList.add('el-icon-close')
    // 关闭事件
    const close = () => {
      // document.body.style.overflow = 'visible'
      i.removeEventListener('click', close)
      document.body.removeChild(div)
    }
    i.addEventListener('click', close)
    div.appendChild(i)
    // 关闭按钮下移
    i.style.top = '60px'
    previewObj = previewPDF(src)
  } else {
    // 图片地址 关闭方法
    console.log(55555555, src)

    previewObj = previewImage(src, () => {
      // document.body.style.overflow = 'visible'
      document.body.removeChild(div)
    })
  }
  div.appendChild(previewObj)

  // document.body.style.overflow = 'hidden'
  document.body.appendChild(div)
}

export const previewPDF = (src = '') => {
  const iframe = document.createElement('div')
  iframe.classList.add('pdf-container')
  iframe.innerHTML = `<iframe style="width: 100%; height: 100%" src="${src}"></iframe>`
  return iframe
}

export const previewImage = (src = '', close) => {
  const imgContainer = document.createElement('div')
  imgContainer.style.display = 'none'
  imgContainer.classList.add('img-container')

  let viewer = null
  const onLoad = () => {
    if (!viewer) {
      viewer = new Viewer(imgContainer, {
        hidden() {
          if (close) {
            close()
          }
          this.viewer.destroy()
        }
      })
    }

    setTimeout(() => {
      viewer.show()
    })
  }

  const onError = (img) => () => {
    // TODO: 修改图片错误地址
    img.src = 'static/images/nopic.svg'
    onLoad()
  }

  if (typeof src === 'string') {
    src = [src]
  }
  console.log(33333333, src)

  src.forEach((s) => {
    const img = document.createElement('img')
    img.src = s
    img.addEventListener('load', onload)
    img.addEventListener('error', onError(img))
    imgContainer.appendChild(img)
  })
  onLoad()
  return imgContainer
}

// 获取打印url
export const getDownloadUrl = () => {
  return process.env.NODE_ENV === 'development' ? `${location.origin}/api` : `${location.origin}`
}

// 判断游览器类型
export const myBrowser = () => {
  const userAgent = navigator.userAgent
  if (userAgent.indexOf('Opera') > -1) {
    return 'Opera'
  }
  if (userAgent.indexOf('Firefox') > -1) {
    return 'Firefox'
  }
  if (userAgent.indexOf('Chrome') > -1) {
    return 'Chrome'
  }
  if (userAgent.indexOf('Safari') > -1) {
    return 'Safari'
  }
  if (userAgent.indexOf('compatible') > -1 && userAgent.indexOf('MSIE') > -1 && !isOpera) {
    return 'IE'
  }
  if (userAgent.indexOf('Trident') > -1) {
    return 'Edge'
  }
}

export const isIEOrEdge = () => {
  return myBrowser() === 'IE' || myBrowser() === 'Edge'
}

// chrome下载
function download(url) {
  // 创建隐藏的可下载链接
  const eleLink = document.createElement('a')
  eleLink.style.display = 'none'
  eleLink.download = url
  eleLink.href = url
  console.log(eleLink)

  document.body.appendChild(eleLink)
  // 触发点击
  eleLink.click()
  // 然后移除
  document.body.removeChild(eleLink)
}

// IE下载
function SaveAs5(url) {
  const oPop = window.open(url, '', 'width=1, height=1, top=5000, left=5000')
  for (; oPop.document.readyState != 'complete'; ) {
    if (oPop.document.readyState == 'complete') break
  }
  oPop.document.execCommand('SaveAs')
  oPop.close()
}

// 下载文件
export const downloadByUrl = (url) => {
  if (isIEOrEdge()) {
    SaveAs5(url)
  } else {
    download(url)
  }
}

export function multiDownLoad(urlArr) {
  for (let i = 0; i < urlArr.length; i++) {
    const iframe = document.createElement('iframe')
    iframe.style.display = 'none'
    iframe.style.height = 0
    iframe.src = urlArr[i]
    document.body.appendChild(iframe)
    setTimeout((res) => {
      iframe.remove()
    }, 5 * 60 * 1000)
  }
}

// 下载多个文件
export const downloadMoreFile = (srcArr = []) => {
  if (srcArr.length === 0) {
    return
  }

  // const createIframe = (src) => {
  //   const iframe = document.createElement('iframe')
  //   iframe.style.display = 'none'
  //   iframe.src = src
  //   iframe.onload = () => {
  //     document.body.removeChild(iframe)
  //   }
  //   document.body.appendChild(iframe)
  // }

  srcArr.forEach((src) => {
    setTimeout(() => {
      downloadByUrl(src)
    }, 100)
  })
}

// 显示
export const showDefaultValue = (attr, defaultVal = '-') => {
  if (attr === null || attr === undefined || (typeof attr === 'string' && attr === '')) {
    return defaultVal
  }
  return attr
}

// UnicodeConverter 转码
export const GB2312UnicodeConverter = {
  ToUnicode: function(str) {
    return escape(str)
      .toLocaleLowerCase()
      .replace(/%u/gi, '\\u')
  },
  ToGB2312: function(str) {
    return unescape(str.replace(/\\u/gi, '%u'))
  }
}

// 获取当前时间，以及上个月的这个时间
export const getQueryTime = () => {
  let date = new Date()
  var daysInMonth = new Array([0], [31], [28], [31], [30], [31], [30], [31], [31], [30], [31], [30], [31])
  var strYear = date.getFullYear()
  var strDay = date.getDate()
  var strMonth = date.getMonth() + 1
  if (strYear % 4 == 0 && strYear % 100 != 0) {
    daysInMonth[2] = 29
  }
  if (strMonth - 1 == 0) {
    strYear -= 1
    strMonth = 12
  } else {
    strMonth -= 1
  }
  strDay = daysInMonth[strMonth] >= strDay ? strDay : daysInMonth[strMonth]
  if (strMonth < 10) {
    strMonth = '0' + strMonth
  }
  if (strDay < 10) {
    strDay = '0' + strDay
  }
  let datastr = strYear + '-' + strMonth + '-' + strDay
  return {
    datastr
  }
}

// 时间格式化
export const formatDate = function(date) {
  var y = date.getFullYear()
  var m = date.getMonth() + 1
  m = m < 10 ? '0' + m : m
  var d = date.getDate()
  d = d < 10 ? '0' + d : d
  return y + '-' + m + '-' + d
}

// 身份证号验证
export const IDcard = (val) => {
  // return /(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(val)

  /**
   * 检验18位身份证号码（15位号码可以只检测生日是否正确即可）
   * @author wolfchen
   * @param cid 18为的身份证号码
   * @return Boolean 是否合法
   **/
  const isCnNewID = (cid) => {
    let arrExp = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2] // 加权因子
    let arrValid = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'] // 校验码
    let strID = cid.toString()
    let sum = 0
    let idx
    for (var i = 0; i < strID.length - 1; i++) {
      // 对前17位数字与权值乘积求和
      sum += parseInt(cid.substr(i, 1), 10) * arrExp[i]
    }
    // 计算模（固定算法）
    idx = sum % 11
    // 检验第18为是否与校验码相等
    return arrValid[idx] === cid.substr(17, 1).toUpperCase()
  }

  if (/(^\d{15}$)/.test(val)) {
    return true
  } else if (/(^\d{17}([0-9]|X)$)/.test(val)) {
    return isCnNewID(val)
  }
}

/**
 * 根据code获取地址全称
 * @param codeArr
 * @param detail
 * @param split
 */
export const addressName = async (codeArr = [], detail = '', split = '') => {
  const res = []
  const getArea = async (arr, list) => {
    let top = arr[0]
    if (top) {
      let city = list.find((o) => o.areaId === top)
      if (city) {
        const { areas } = await api.getAddressAreaInfoByParentid(city.areaId)
        res.push(city.areaName)
        arr.shift()
        await getArea(arr, areas)
      }
    }
  }
  const proves = await api.getAllProvinces()
  if (proves && proves.areas) {
    await getArea(codeArr, proves.areas)
  }
  res.push(detail)
  return res.join(split)
}

// 需要添加header下载
export const exportData = (obj) => {
  let fileType = ''
  if (obj.fileType.indexOf('zip') != -1) {
    fileType = 'application/zip'
  } else if (obj.fileType.indexOf('xlsx') != -1) {
    fileType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  } else if (obj.fileType.indexOf('xls') != -1) {
    fileType = 'application/vnd.ms-excel'
  } else if (obj.fileType.indexOf('pdf') != -1) {
    fileType = 'application/pdf'
  }
  console.log(obj)

  axios({
    url: obj.url,
    method: obj.method,
    data: obj.data || {},
    responseType: 'blob',
    xsrfHeaderName: 'Authorization',
    headers: {
      'Content-Type': 'application/json',
      userName: window.localStorage.getItem('userName')
    }
  })
    .then((response) => {
      let blob = new Blob([response.data], {
        type: fileType
      })
      if (obj.fileType === 'pdf') {
        window.open(window.URL.createObjectURL(blob))
        return
      }
      let url = window.URL.createObjectURL(blob)
      let link = document.createElement('a')
      link.style.display = 'none'
      link.href = url
      console.log('response', response, url)
      link.setAttribute('download', obj.fileName)
      document.body.appendChild(link)
      link.click()
    })
    .catch((error) => {
      console.log(error)
    })
}

// post 导出数据
export const exportPostData = (url, list) => {
  let tempForm = document.createElement('form');
  tempForm.id = 'tempForm1';
  tempForm.method = 'post';
  tempForm.action = url;
  tempForm.target = '_self';

  let opt = document.createElement('input');
  opt.type = 'hidden';
  opt.name = 'param';
  console.log(JSON.stringify(list));
  opt.value = JSON.stringify(list);
  tempForm.appendChild(opt);


  if(window.attachEvent){
      tempForm.attachEvent("onsubmit",function(){});        //IE
  }else{
    tempForm.addEventListener("submit",function(){},false);    //firefox
  }
  document.body.appendChild(tempForm);
  if(tempForm.fireEvent){
      tempForm.fireEvent("onsubmit");
  }else{
      tempForm.dispatchEvent(new Event("submit"));
  }
  tempForm.submit();//提交POST请求
  document.body.removeChild(tempForm);
}
