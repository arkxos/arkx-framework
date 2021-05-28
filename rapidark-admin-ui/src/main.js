import Vue from 'vue'
import App from './App'
import i18n from './i18n'
import store from './store'
import router from './router'
import '@/vab'
import iView from 'iview'
import 'iview/dist/styles/iview.css'
import TreeTable from 'tree-table-vue'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import Treeselect from '@riophae/vue-treeselect'
import ArkIcon from '@/components/ArkIcon/ArkIcon'
import permission from './components/Permission'

/**
 * @description 正式环境默认使用mock，正式项目记得注释后再打包
 */
import { baseURL } from './config'
import { isExternal } from '@/utils/validate'
if (process.env.NODE_ENV === 'development-front' && !isExternal(baseURL)) {
  const { mockXHR } = require('@/utils/static')
  mockXHR()
}

Vue.use(iView, {
  // i18n: (key, value) => i18n.t(key, value)
})
// 注册组件
Vue.use(permission)
Vue.component(TreeTable.name, TreeTable)
Vue.component('treeselect', Treeselect)
Vue.component('ArkIcon', ArkIcon)

Vue.prototype.hasAuthority = function() {
  return true;
}
Vue.config.productionTip = false
new Vue({
  el: '#app',
  i18n,
  store,
  router,
  render: (h) => h(App),
})
