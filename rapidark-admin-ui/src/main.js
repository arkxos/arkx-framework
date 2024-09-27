import Vue from 'vue'
import App from './App'
import i18n from './i18n'
import store from './store'
import router from './router'
import '@/vab'
import iView from 'iview'
import 'iview/dist/styles/iview.css'
import * as utils from './common/js/utils'
import TreeTable from 'tree-table-vue'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import Treeselect from '@riophae/vue-treeselect'
import ArkIcon from '@/components/ArkIcon/ArkIcon'
import permission from './components/Permission'
// 数据字典
import dict from './components/Dict'

import 'xe-utils'
import VXETable from 'vxe-table'
import 'vxe-table/lib/style.css'
import api from '@/api/handling/index'
import Component from '@/components/base/index'

/**
 * @description 正式环境默认使用mock，正式项目记得注释后再打包
 */
import { baseURL } from './config'
import { isExternal } from '@/utils/validate'
import { decodeUnicode } from '@/utils/util'
// if (process.env.NODE_ENV === 'development' && !isExternal(baseURL)) {
//   const { mockXHR } = require('@/utils/static')
//   mockXHR()
// }

Vue.use(iView, {
  // i18n: (key, value) => i18n.t(key, value)
})
Vue.use(VXETable)
// 给 vue 实例挂载内部对象，例如：
// Vue.prototype.$XModal = VXETable.modal
// Vue.prototype.$XPrint = VXETable.print
// Vue.prototype.$XSaveFile = VXETable.saveFile
// Vue.prototype.$XReadFile = VXETable.readFile
// 注册组件
Vue.use(permission)
Vue.use(Component)
Vue.component(TreeTable.name, TreeTable)
Vue.component('treeselect', Treeselect)
Vue.component('ArkIcon', ArkIcon)
Vue.use(dict)

// 将 api, utils 挂载在 Vue 的 prototype 下
Vue.prototype.$api = api
Vue.prototype.decodeUnicode = decodeUnicode
Vue.prototype.$utils = utils

Vue.prototype.hasAuthority = function() {
  return true;
}
Vue.mixin({
  methods: {
    // 全局混入区划判断
    areaAvailable(code) {
      if (!code) return false
      return code.startsWith('320')
    }
  }
})
Vue.config.productionTip = false
new Vue({
  el: '#app',
  i18n,
  store,
  router,
  render: (h) => h(App),
})
