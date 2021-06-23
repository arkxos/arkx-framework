import request from '@/utils/request'
import { encryptedData } from '@/utils/encrypt'
import { loginRSA } from '@/config'
import { getToken } from '@/libs/util'
import config from '@/config'
import store from '@/store'

/**
 * 用户登录
 * @param username
 * @param password
 */
export async function login(data) {
  if (loginRSA) {
    data = {
      loginInfo: await encryptedData(data)
    }
  }
  return request({
    url: '/admin/login/token', // '/login',
    method: 'post',
    data: data
  })
}

export async function socialLogin(data) {
  if (loginRSA) {
    data = await encryptedData(data)
  }
  return request({
    url: '/socialLogin',
    method: 'post',
    data,
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request({
    url: 'admin/current/user', // '/userInfo',
    method: 'get',
  })
}

/**
 * 登出
 */
export function logout() {
  const token = store.getters['user/token']
  return request({
    url: 'admin/logout/token',
    // headers: {
    //   'Content-Type': 'multipart/form-data;charset=UTF-8'
    // },
    data: { token: token },
    method: 'post'
    // url: '/logout',
    // method: 'get',
  })
}

export function register(data) {
  return request({
    url: '/register',
    method: 'post',
    data,
  })
}

/**
 * 获取登录用户菜单权限
 */
export const getCurrentUserMenu = () => {
  const params = {
    serviceId: config.serviceId
  }
  return request({
    url: 'base/current/user/menu',
    params,
    method: 'get'
  })
}

export const updateCurrentUserInfo = ({ nickName, userDesc, avatar }) => {
  const data = {
    nickName: nickName,
    userDesc: userDesc,
    avatar: avatar
  }
  return request({
    url: 'base/current/user/update',
    data,
    method: 'post'
  })
}

/**
 * 获取用户列表
 * @param params
 */
export const getUsers = (params) => {
  return request({
    url: 'base/user',
    params,
    method: 'get'
  })
}

/**
 * 获取所有用户列表
 */
export const getAllUsers = () => {
  return request({
    url: 'base/user/all',
    method: 'get'
  })
}

/**
 * 添加用户信息
 * @param userName
 * @param password
 * @param nickName
 * @param status
 * @param userType
 * @param email
 * @param mobile
 * @param userDesc
 * @param avatar
 */
export const addUser = ({ userName, password, nickName, status, userType, email, mobile, userDesc, avatar }) => {
  const data = {
    userName: userName,
    nickName: nickName,
    password: password,
    status: status,
    userType: userType,
    email: email,
    mobile: mobile,
    userDesc: userDesc,
    avatar: avatar
  }
  return request({
    url: 'base/user/add',
    data,
    method: 'post'
  })
}

/**
 * 更新用户信息
 * @param userId
 * @param nickName
 * @param status
 * @param userType
 * @param email
 * @param mobile
 * @param userDesc
 * @param avatar
 */
export const updateUser = ({ userId, nickName, status, userType, email, mobile, userDesc, avatar }) => {
  const data = {
    userId: userId,
    nickName: nickName,
    status: status,
    userType: userType,
    email: email,
    mobile: mobile,
    userDesc: userDesc,
    avatar: avatar
  }
  return request({
    url: 'base/user/update',
    data,
    method: 'post'
  })
}

/**
 * 分配用户角色
 * @param data
 */
export const addUserRoles = ({ userId, grantRoles }) => {
  const data = { userId: userId, roleIds: grantRoles.join(',') }
  return request({
    url: 'base/user/roles/add',
    data,
    method: 'post'
  })
}

/**
 * 获取用户角色
 * @param userId
 */
export const getUserRoles = (userId) => {
  const params = {
    userId: userId
  }
  return request({
    url: 'base/user/roles',
    params,
    method: 'get'
  })
}

/**
 * 修改密码
 * @param userId
 * @param password
 */
export const updatePassword = ({ userId, password }) => {
  const data = {
    userId: userId,
    password: password
  }
  return request({
    url: 'base/user/update/password',
    data,
    method: 'post'
  })
}

/**
 * 修改用户登录密码
 */
export const changePassword = (data) => {
  return request({
    url: '/base/current/user/rest/password',
    data,
    method: 'post'
  })
}
