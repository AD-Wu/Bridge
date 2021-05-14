import Vue from 'common/js/vue'
import VueRouter from 'common/js/vue-router'

Vue.use(VueRouter)

new VueRouter({
    routes: [
        {
            path: '/page/text',
            name: '测试页'
        },
        {
            path: '/page/eleHome',
            name: '表格'
        }
    ]
})