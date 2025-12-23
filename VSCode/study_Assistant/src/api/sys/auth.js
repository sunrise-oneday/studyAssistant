// src/api/sys/auth.js
import axios from 'axios';
import config from '@/api/config'; // 引入上面的配置文件

// 登录接口
export const login = (params) => {
    // 拼接地址：http://localhost:8080/api/user/login
    return axios.post(config.host + 'user/login', params);
};

// 注册接口 
export const register = (params) => {
    return axios.post(config.host + 'user/register', params);
};

// 退出登录 (前端清除数据)
export const logout = () => {
    config.token.clear();
    config.user.clear();
    config.tokenTime.clear();
};