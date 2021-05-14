const vm = new Vue({
    el: '#app',
    data: {
        tableData: [
            {
                date: '1000-01-01',
                name: '张三',
                province: '北京',
                city: '朝阳区',
                address: '天安门广场1号',
                zip: 101010
            },
            {
                date: '2000-02-02',
                name: '李四',
                province: '上海',
                city: '普陀区',
                address: '上海市普陀区金沙江路 1518 弄',
                zip: 202020
            },
            {
                date: '3000-03-03',
                name: '王五',
                province: '深圳',
                city: '湾仔',
                address: '深圳123号',
                zip: 303030
            },
            {
                date: '4000-04-04',
                name: '赵六',
                province: '福建',
                city: '厦门',
                address: '厦门市湖里坂上社369号',
                zip: 404040
            },
            {
                date: '5000-05-05',
                name: '钱七',
                province: '浙江',
                city: '杭州',
                address: '杭州市文三路90号',
                zip: 505050
            },
            {
                date: '6000-06-06',
                name: '周八',
                province: '江苏省',
                city: '南京',
                address: '江苏南通456号',
                zip: 606060
            }, {
                date: '1000-01-01',
                name: '张三',
                province: '北京',
                city: '朝阳区',
                address: '天安门广场1号',
                zip: 101010
            },
            {
                date: '2000-02-02',
                name: '李四',
                province: '上海',
                city: '普陀区',
                address: '上海市普陀区金沙江路 1518 弄',
                zip: 202020
            },
            {
                date: '3000-03-03',
                name: '王五',
                province: '深圳',
                city: '湾仔',
                address: '深圳123号',
                zip: 303030
            },
            {
                date: '4000-04-04',
                name: '赵六',
                province: '福建',
                city: '厦门',
                address: '厦门市湖里坂上社369号',
                zip: 404040
            },
            {
                date: '5000-05-05',
                name: '钱七',
                province: '浙江',
                city: '杭州',
                address: '杭州市文三路90号',
                zip: 505050
            },
            {
                date: '6000-06-06',
                name: '周八',
                province: '江苏省',
                city: '南京',
                address: '江苏南通456号',
                zip: 606060
            }, {
                date: '1000-01-01',
                name: '张三',
                province: '北京',
                city: '朝阳区',
                address: '天安门广场1号',
                zip: 101010
            },
            {
                date: '2000-02-02',
                name: '李四',
                province: '上海',
                city: '普陀区',
                address: '上海市普陀区金沙江路 1518 弄',
                zip: 202020
            },
            {
                date: '3000-03-03',
                name: '王五',
                province: '深圳',
                city: '湾仔',
                address: '深圳123号',
                zip: 303030
            },
            {
                date: '4000-04-04',
                name: '赵六',
                province: '福建',
                city: '厦门',
                address: '厦门市湖里坂上社369号',
                zip: 404040
            },
            {
                date: '5000-05-05',
                name: '钱七',
                province: '浙江',
                city: '杭州',
                address: '杭州市文三路90号',
                zip: 505050
            },
            {
                date: '6000-06-06',
                name: '周八',
                province: '江苏省',
                city: '南京',
                address: '江苏南通456号',
                zip: 606060
            }],
        tableDataTemp: [],
        multipleSelection: [],
        tableHeight: 700,
        search: '',
        pageSize: 10,//默认每页数据量
        currentPage: 1,//当前页码
        start: 1, //查询的页码
        totalCount: 0, //默认数据总数
        isCollapse: false,
        menuItems:[
            {path:'/findProject',name:'发现项目'},
            {path:'/communityActivity',name:'社区动态'},
            {path:'/publishProject',name:'发布项目'},
            {path:'/personalCenter',name:'个人中心'}
        ]
        // ,
        // routes: [
        //     {
        //         //配置了home
        //         path: '/home',
        //         name: 'Home',
        //         component: Home,
        //         //在home中继续跳转FromData
        //         children: [{
        //             name: "FromData",
        //             path: "FromData",
        //             component: FromData
        //         }]
        //     }
        // ]
    },
    created() {
        Vue.nextTick(() => {
            //载入表格数据
            this.loadData(this.currentPage, this.pageSize);
            //初始化websocket
            // vm.initWS();
        });
    },
    mounted() {
        this.$nextTick(() => {
            this.tableHeight = window.innerHeight - 170;
        })
    },
    methods: {
        handleCollapse() {
            this.isCollapse = !this.isCollapse;
        },
        deleteRow(index, rows) {
            rows.splice(index, 1);
        },

        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        tableRowClassName({row, rowIndex}) {
            if (rowIndex === 1) {
                return 'warning-row';
            } else if (rowIndex === 3) {
                return 'success-row';
            }
            return '';
        },
        tableRowStripe({row, rowIndex}) {
            if ((rowIndex + 1) % 2 === 0) {
                return 'stripe-row';
            }
        },
        handleSearch: function () {
            if (this.search === '') {
                return this.tableData;
            } else {
                return this.tableData.filter(data => {
                    // filter()对象遍历,true 返回对象参数值,如果多条数据,自动使用数组拼接
                    for (let k in data) {
                        let v = data[k] + '';
                        if (v.toLowerCase().includes(this.search.toLowerCase())) {
                            return data;
                        }
                    }
                })
            }
        },
        //每页显示数据量变更
        handleSizeChange: function (val) {
            this.pageSize = val;
            this.loadData(this.currentPage, this.pageSize);
        },
        //页码变更
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.loadData(this.currentPage, this.pageSize);
        },

        //从后台获取数据
        loadData: function (page, pageSize) {
            // let {word, disable} = vm.sensitiveWordForm;
            // const params = {page, pageSize, word, disable};
            // this.loading = true;
            // $.ajax({
            //     type: "POST",
            //     url: baseUrl + "/sms/sensitiveController/tableQuery",
            //     contentType: "application/json",
            //     data: JSON.stringify(params),
            //     success: function (r) {
            //         vm.tableData = r.resultValue.items;
            //         vm.totalCount = r.resultValue.itemCount;
            //         vm.loading = false;
            //     },
            //     error: function () {
            //         vm.loading = false;
            //     }
            // });
        },
        // toggleSelection(rows) {
        //     if (rows) {
        //         rows.forEach(row => {
        //             this.$refs.multipleTable.toggleRowSelection(row);
        //         });
        //     } else {
        //         this.$refs.multipleTable.clearSelection();
        //     }
        // }
    }

})