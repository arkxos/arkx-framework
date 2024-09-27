/**
 * @description 路由拦截状态管理，目前两种模式：all模式与intelligence模式，其中partialRoutes是菜单暂未使用
 */
import Vue from 'vue'
import { asyncRoutes, asyncRoutesSimple, constantRoutes, resetRouter } from '@/router'
import { getRouterList } from '@/api/router'
import { convertRouter, filterRoutes } from '@/utils/routes'
import { authentication, rolesControl } from '@/config'
import { isArray } from '@/utils/validate'
import { listConvertTree, startWith } from '@/libs/util'
const state = () => ({
  routes: [],
  cachedRoutes: [],
})
const getters = {
  routes: (state) => {
    console.log('get routes', state.routes)
    return state.routes
  },
  cachedRoutes: (state) => state.cachedRoutes,
}
const mutations = {
  /**
   * @description 多模式设置路由
   * @param {*} state
   * @param {*} routes
   */
  setRoutes(state, routes) {
    console.log('set routes' , routes)
    state.routes = routes
  },
  /**
   * @description 设置缓存Name数组
   * @param {*} state
   * @param {*} routes
   */
  setCachedRoutes(state, routes) {
    state.cachedRoutes = routes
  },
  /**
   * @description 修改Meta
   * @param {*} state
   * @param options
   */
  changeMenuMeta(state, options) {
    function handleRoutes(routes) {
      return routes.map((route) => {
        if (route.name === options.name) Object.assign(route.meta, options.meta)
        if (route.children && route.children.length)
          route.children = handleRoutes(route.children)
        return route
      })
    }

    state.routes = handleRoutes(state.routes)
  },
}

function formatRouters(array) {
  let opt = {
    primaryKey: 'menuId',
    parentKey: 'parentId',
    startPid: '0'
  }
  let menus = listConvertTree(array, opt)
  menus = convertRouterTypeData(menus)
  return menus
}

function convertRouterTypeData(array) {
  console.log('convertRouterTypeData', array)
  let list = array.map(item => {
    let path = startWith(item.path, '/') ? item.path.substring(1) : item.path
    let url = item.scheme + item.path
    console.log(url)
    if (url && url.startsWith('/') && url.length > 1) {
      url = url.substring(1)
    }
    let router = {
      // 使用菜单id不使用menuCode防止修改后,刷新后缓存的页面无法找到
      name: `${item.menuCode}`,
      path: url || '',
      component: item.component,
      hidden: item.visible === 0,
      parentId: item.parentId,
      meta: {
        // access: access,
        hideInMenu: false,
        title: item.menuName,
        notCache: true,
        icon: item.icon || 'md-document',
        hideInBread: false,
        target: item.target
      },
      children: []
    }
    // 非根节点
    if (item.target === '_blank') {
      // 新窗口打开,使用meta.href
      router.meta.href = url
    }
    if (item.parentId === 0 || item.parentId === '0') {
      item.component = 'Layout'
    }
    if (item.children) {
      router.children = convertRouterTypeData(item.children)
    }
    return router
  })
  console.log('list ', list)
  return list
}

const actions = {
  /**
   * @description 多模式设置路由
   * @param {*} { commit }
   * @param mode
   * @returns
   */
  async setRoutes({ commit }, mode = 'none') {

    // 默认前端路由
    let routes = [...asyncRoutes]
    // 设置游客路由关闭路由拦截(不需要可以删除)
    const control = mode === 'visit' ? false : rolesControl
    // 设置后端路由(不需要可以删除)
    if (authentication === 'all') {
      const { data } = await getRouterList()
      const list = data
      if (!isArray(list)) {
        Vue.prototype.$baseMessage(
          '路由格式返回有误！',
          'error',
          false,
          'vab-hey-message-error'
        )
      }
      console.log('setRoutes')
      let menus = formatRouters(list)
      if (menus[menus.length - 1].path !== '*') {
        menus.push({ path: '*', redirect: '/404', hidden: true })
      }
      routes = convertRouter(menus)
    }

    const initRoutes = constantRoutes.concat(asyncRoutes)
    // 根据权限和rolesControl过滤路由
    let finallyRoutes = filterRoutes([...initRoutes, ...routes], control)
    finallyRoutes = initRoutes.concat(routes) // constantRoutes.concat(finallyRoutes)

    console.log('ddddddddddddd', finallyRoutes)

    // 设置菜单所需路由
    commit('setRoutes', finallyRoutes)
    // 根据可访问路由重置Vue Router
    await resetRouter(finallyRoutes)
  },
  /**
   * @description 设置缓存Name数组
   * @param {*} { commit }
   * @param {*} routes
   */
  setCachedRoutes({ commit }, routes) {
    commit('setCachedRoutes', routes)
  },
  /**
   * @description 修改Route Meta
   * @param {*} { commit }
   * @param options
   */
  changeMenuMeta({ commit }, options = {}) {
    commit('changeMenuMeta', options)
  },
}
export default { state, getters, mutations, actions }
