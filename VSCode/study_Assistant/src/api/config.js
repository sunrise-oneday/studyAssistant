// src/api/config.js

const config = {
    // 后端接口基础地址 (Spring Boot 默认端口 8080)
    // 注意：你的 Controller 路径是 /api/user/login，所以这里 host 设为 /api/
    host: 'http://localhost:8080/api/', 
    
    // 静态资源基础地址 (如果有图片上传等功能会用到)
    hostfile: 'http://localhost:8080/',
    
    // 网站基础信息
    webInfo: {
        department: '教务处', // 或者 '计算机学院'
        schoolName: 'AI 校园学习助手'
    },

    // 用户信息管理
    user: {
        // 初始化时尝试从本地读取
        data: (() => {
            try {
                return JSON.parse(localStorage.getItem('user')) || { name: "", role: "" };
            } catch (e) {
                return { name: "", role: "" };
            }
        })(),
        // 获取用户信息对象
        get: function () {
            try {
                return JSON.parse(localStorage.getItem('user'));
            } catch (e) {
                return null;
            }
        },
        // 设置用户信息 (登录成功后调用)
        set: function (user) {
            localStorage.setItem('user', JSON.stringify(user));
            this.data = user; 
        },
        // 清除用户信息 (退出登录时调用)
        clear: function() {
            localStorage.removeItem('user');
            this.data = { name: "", role: "" }; // 重置内存数据
        }
    },

    // Token 管理
    token: {
        data: localStorage.getItem('token'),
        set: function (token) {
            localStorage.setItem('token', token);
            this.data = token;
        },
        get: function () {
            return localStorage.getItem('token');
        },
        clear: function() {
            localStorage.removeItem('token');
            this.data = null;
        }
    },

    // Token 过期时间管理 (可选，配合后端过期时间)
    tokenTime: {
        data: localStorage.getItem('tokenTime'),
        set: function (tokenTime) {
            localStorage.setItem('tokenTime', tokenTime);
        },
        get: function () {
            return localStorage.getItem('tokenTime');
        },
        clear: function() {
            localStorage.removeItem('tokenTime');
        }
    }
}

export default config;