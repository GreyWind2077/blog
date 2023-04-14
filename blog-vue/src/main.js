
// import Vue from 'vue'
import App from './App'

import router from './router'
import store from './store'

import lodash from 'lodash'
import semantic from '../node_modules/semantic-ui-css/semantic.min.js'

import '../node_modules/semantic-ui-css/semantic.min.css'

// import ElementUI from 'element-ui'
import '@/assets/theme/index.css'

import '@/assets/icon/iconfont.css'

import '../static/css/me.css'

import {formatTime} from "./utils/time";


Vue.config.productionTip = false
// 使用semantic
 Vue.use(semantic)
// Vue.use(ElementUI)

Object.defineProperty(Vue.prototype, '$_', { value: lodash })


Vue.directive('title',  function (el, binding) {
  document.title = el.dataset.title
})
// 格式话时间
Vue.filter('format', formatTime)

new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: { App }
})
