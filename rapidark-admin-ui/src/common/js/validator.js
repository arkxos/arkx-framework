import {
  numOrAlphabet,
  mPattern,
  numPattern,
  cnPattern,
  cnAndEnPattern,
  posPattern,
  moneyPattern,
  maxFourDecimals,
  maxThreeDecimals,
  maxThreeDecimalsCanMinus,
  maxTwoDecimals,
  maxOneDecimals,
  maxTwoDecimalsCanMinus,
  maxDecimals,
  numberPattern,
  volidateIdNo,
  IDcard
} from './utils'

// 数字或字母校验
export const validateNumOrAlphabet = (rule, value, callback) => {
  if (value && !numOrAlphabet(value)) {
    callback(new Error(rule.message))
  } else {
    callback()
  }
}

// 手机号校验
export const validatePhone = (rule, value, callback) => {
  if (value && !mPattern(value)) {
    callback(new Error(rule.message))
  } else {
    callback()
  }
}

// 数字校验
export const validateNumber = (rule, value = '', callback) => {
  if (value && !numPattern(value)) {
    callback(new Error(rule.message))
  } else {
    callback()
  }
}

// 中文和英文校验
export const validateCnAndEn = (rule, value = '', callback) => {
  if (value && !cnAndEnPattern(value)) {
    callback(new Error(rule.message))
  } else {
    callback()
  }
}

// 非负整数校验
export const validatePos = (rule, value = '', callback) => {
  if (value && !posPattern(value)) {
    callback(new Error(rule.message))
  } else {
    callback()
  }
}

// 验证> 0的正整数
export const validatePosNoZero = (rule, value = '', callback) => {
  if (value && (parseFloat(value) <= 0 || !posPattern(value))) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 空校验
export const validateEmpty = (rule, value = '', callback) => {
  if (typeof value === 'number') {
    callback()
  } else if (typeof value !== 'string' && value) {
    callback()
  } else if (value && value.trim && !!value.trim() && value !== 'false') {
    callback()
  } else {
    callback(new Error(rule.message))
  }
}

// 空校验 非字符串
export const validateEmptyNoString = (rule, value = '', callback) => {
  if (!value) {
    callback(new Error(rule.message))
  } else {
    callback()
  }
}

// 空校验 非字符串
export const validateArrayEmpty = (rule, value = [], callback) => {
  if (Array.isArray(value) && value.length > 0) {
    callback()
  } else {
    callback(new Error(rule.message))
  }
}

// 中文校验
export const validateCn = (rule, value = '', callback) => {
  if (value && cnPattern(value)) {
    callback(new Error(rule.message))
  } else {
    callback()
  }
}

// 金额校验
export const validateMoney = (rule, value = '', callback) => {
  if (value && !moneyPattern(value)) {
    callback(new Error(rule.message))
  } else {
    callback()
  }
}

// 验证4位小数
export const validMaxFourDecimals = (rule, value = '', callback) => {
  if (value && !maxFourDecimals(value)) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 验证3位小数
export const validMaxThreeDecimals = (rule, value = '', callback) => {
  if (value && !maxThreeDecimals(value)) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 验证99位小数
export const validMaxDecimals = (rule, value = '', callback) => {
  if (value && !maxDecimals(value)) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 验证3位小数 可负数
export const validMaxThreeDecimalsCanMinus = (rule, value = '', callback) => {
  if (value && !maxThreeDecimalsCanMinus(value)) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 验证2位小数
export const validMaxTwoDecimals = (rule, value = '', callback) => {
  if (value && !maxTwoDecimals(value)) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 验证1小数
export const validMaxOneDecimals = (rule, value = '', callback) => {
  if (value && !maxOneDecimals(value)) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 验证2位小数 可负数
export const validMaxTwoDecimalsCanMinus = (rule, value = '', callback) => {
  if (value && !maxTwoDecimalsCanMinus(value)) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 验证> 0的数值，最多2位小数
export const validTwoDecimals = (rule, value = '', callback) => {
  if (value && (parseFloat(value) <= 0 || !maxTwoDecimals(value))) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 验证>= 0的数值，最多2位小数
export const validTwoDecimalsAndZero = (rule, value = '', callback) => {
  if (value && (parseFloat(value) < 0 || !maxTwoDecimals(value))) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 验证 <= 0
export const validNoZero = (rule, value = '', callback) => {
  if (value && parseFloat(value) <= 0) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 验证>= 0且≤100的数值，最多2位小数
export const validTwoDecimalsAndZeroHundred = (rule, value = '', callback) => {
  if (value && (parseFloat(value) < 0 || parseFloat(value) > 100 || !maxTwoDecimals(value))) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 验证> 0且≤100的数值，最多2位小数
export const validTwoDecimalsAndBigZeroHundred = (rule, value = '', callback) => {
  if (value && (parseFloat(value) <= 0 || parseFloat(value) > 100 || !maxTwoDecimals(value))) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 固定电话校验
export const validateCustomerTel = (rule, value = '', callback, form) => {
  let {
    customerTel1,
    customerTel2,
    customerTel3
  } = form

  if ((customerTel1 && !numberPattern(customerTel1)) || (customerTel2 && !numberPattern(customerTel2))) {
    return callback(new Error(rule.message))
  } else if (customerTel3 && !numberPattern(customerTel3)) {
    return callback(new Error(rule.message))
  }

  callback()
}

// 是否校验
export const isValid = (obj, validator) => {
  return (rule, value = '', callback) => {
    if (obj.isSubmit) {
      validator(rule, value, callback)
    }
  }
}

// 数字校验
export const validateNumberPattern = (rule, value = '', callback) => {
  if (value && !numberPattern(value)) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 身份证校验
export const validateIdNoPattern = (rule, value = '', callback) => {
  if (value && !volidateIdNo(value)) {
    return callback(new Error(rule.message))
  }
  callback()
}

// 身份证号验证
export const validateIDcard = (rule, value, callback) => {
  if (!!value.trim() && !IDcard(value)) {
    callback(new Error(rule.message))
  } else {
    callback()
  }
}

// 身份证号验证
export const validateIDLength = (rule, value, callback) => {
  if (value.length === 15) {
    callback(new Error(rule.message))
  } else {
    callback()
  }
}
