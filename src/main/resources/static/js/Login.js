new Vue({
    el: '#app',
    data: function () {
        return {
            //数据绑定
            index: 0,
            bgImages: [
                '../images/cat.jpg'
            ],
            show: {
                diplay: 'blok',
            },
            loginForm: {
                username: 'admin',
                password: '123456'
            },
            //表单验证规则
            loginFormRules: {
                username: [{
                    required: true,
                    message: '请输入登录名',
                    trigger: 'blur'
                },
                    {
                        min: 3,
                        max: 10,
                        message: '登录名长度在 3 到 10 个字符',
                        trigger: 'blur'
                    }
                ],
                password: [{
                    required: true,
                    message: '请输入密码',
                    trigger: 'blur'
                },
                    {
                        min: 6,
                        max: 15,
                        message: '密码长度在 6 到 15 个字符',
                        trigger: 'blur'
                    }
                ]
            }
        }
    },
    methods: {
        //添加表单重置方法
        resetLoginForm() {
            //this=>当前组件对象，其中的属性$refs包含了设置的表单ref
            //   console.log(this)
            this.$refs.LoginFormRef.resetFields()
        },
        login() {
            //点击登录的时候先调用validate方法验证表单内容是否有误
            this.$refs.LoginFormRef.validate(async valid => {
                console.log(this.loginFormRules)
                //如果valid参数为true则验证通过
                if (!valid) {
                    return
                }

                //发送请求进行登录
                const {
                    data: res
                } = await this.$http.post('login', this.loginForm)
                //   console.log(res);
                if (res.meta.status !== 200) {
                    return this.$message.error('登录失败:' + res.meta.msg) //console.log("登录失败:"+res.meta.msg)
                }

                this.$message.success('登录成功')
                console.log(res)
                //保存token
                window.sessionStorage.setItem('token', res.data.token)
                // 导航至/home
                this.$router.push('/home')
            })
        },
        changBg() {
            // alert(11);
            // const bglogin = document.getElementsByClassName("bg-login");
            // console.log(bglogin);
            if (this.index === 0) {
                this.index = 3;
            } else {
                this.index = this.index - 1;
            }
            $(".bg-login>li:eq(" + this.index + ")").fadeIn("3000").siblings().fadeOut("3000");
            console.log(this.index);
        }
    },
    created() {
        this.index = this.bgImages.length
        setInterval(this.changBg, 7000);
    }
})
