import request from '@/utils/request'

/**
 * 获取开发商列表
 * @param params
 */
export const getDevelopers = (params) => {
  return request({
    url: 'base/developer',
    params,
    method: 'get'
  })
}

/**
 * 获取所有开发商列表
 */
export const getAllDevelopers = () => {
  return request({
    url: 'base/developer/all',
    method: 'get'
  })
}

/**
 * 添加开发商信息
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
export const addDeveloper = ({
     companyName, personName, type,
     userName, password, nickName, status, userType, email,
     mobile, userDesc, avatar }) => {
  const data = {
    companyName: companyName,
    personName: personName,
    userName: userName,
    nickName: nickName,
    password: password,
    status: status,
    type: type,
    email: email,
    mobile: mobile,
    userDesc: userDesc,
    avatar: avatar
  }
  return request({
    url: 'base/developer/add',
    method: 'post',
    data
  })
}

/**
 * 更新开发商信息
 * @param developerId
 * @param nickName
 * @param status
 * @param email
 * @param mobile
 * @param avatar
 */
export const updateDeveloper = ({
        companyName, personName, type,
        id, nickName, status, userType, email,
        mobile, userDesc, avatar }) => {
  const data = {
    id: id,
    companyName: companyName,
    personName: personName,
    type: type,
    nickName: nickName,
    status: status,
    userType: userType,
    email: email,
    mobile: mobile,
    userDesc: userDesc,
    avatar: avatar
  }
  return request({
    url: 'base/developer/update',
    method: 'post',
    data
  })
}

/**
 * 修改密码
 * @param developerId
 */
export const updatePassword = ({ userId, password }) => {
  const data = {
    userId: userId,
    password: password
  }
  return request({
    url: 'base/developer/update/password',
    data,
    method: 'post'
  })
}
