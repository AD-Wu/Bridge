let chinese = false;
EventHelper.addHandler(window, "load", function () {
    let title = document.getElementById("title");
    let user = document.getElementById("user");
    let pwd = document.getElementById("pwd");
    let loginButton = document.getElementById("loginButton");
    let copyright = document.getElementById("copyright");

    if (chinese) {
        title.innerText = "Golden Gate Bridge"
        loginButton.innerText = "请输入用户";
        copyright.innerText = "© 2021 Golden Gate Bridge 版权所有 | 由AD设计"
        user.placeholder = "用户";
        pwd.placeholder = "密码";
    } else {
        title.innerText = "Golden Gate Bridge"
        loginButton.innerText = "Enter user";
        copyright.innerText = "© 2021 Golden Gate Bridge. All rights reserved | Design by AD"
        user.placeholder = "user";
        pwd.placeholder = "pwd";
    }
});

function canLogin() {
    let user = document.getElementById("user").value;
    if (user == null || "" === user.trim()) {
        return false;
    }
    let loginButton = document.getElementById("loginButton");
    loginButton.disbled = true;
    return true;
}

function checkUser() {
    let user = document.getElementById("user").value;
    let loginButton = document.getElementById("loginButton");
    if (user != null && "" !== user.trim()) {
        loginButton.disabled = false;
        if(chinese){
            loginButton.innerText="登录";
        }else{
            loginButton.innerText="login";
        }
    }else{
        loginButton.disabled = true;
        if(chinese){
            loginButton.innerText="请输入用户";
        }else{
            loginButton.innerText="Enter user";
        }
    }
}
