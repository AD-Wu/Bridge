
const vm = new Vue({
    el: '#app',
    data: {
        isCollapse: false,
        defaultPage:'/page/text',
        menuItems: [
            {
                path: '/page/text',
                name: '测试页'
            },
            {
                path: '/page/eleHome',
                name: '表格'
            }
        ]
    },
    methods: {
        handleCollapse() {
            this.isCollapse = !this.isCollapse;
        },
        goTo(path) {
            this.path = path;
        }
    }
})